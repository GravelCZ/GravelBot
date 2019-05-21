package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Hashtable;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZLP.Bot.APIs.Imgur.ImgurAPI;
import cz.GravelCZLP.Bot.APIs.PasteBinAPI.PasteBinAPI;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Main.Main;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class McInfoCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length == 0) {
			sendMessage(channel, "You need to supply the IP.");
			return;
		}
		Runnable r = new Runnable() {

			@SuppressWarnings("unused")
			@Override
			public void run() {
				try {
					String input = args[0];

					String[] split = input.split(":");
					
					if (split.length > 2) {
						split = new String[] { input };
					}
					
					String addressIp = split[0];
					int port = split.length > 1 ? getInt(split[1], 25565) : 25565;
					
					try {
						if (port == 25565) {
							Hashtable<String, String> hashtable = new Hashtable<String, String>();
							hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
							hashtable.put("java.naming.provider.url", "dns:");
							hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
							DirContext dircontext = new InitialDirContext(hashtable);
							Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + input,
									new String[] { "SRV" });
							String[] astring = attributes.get("srv").get().toString().split(" ", 4);
							addressIp = astring[3];
							port = getInt(astring[2], 25565);
						}	
					} catch (Exception e) {
						//ignore. it is an Ip address. hopefully.
					}
					
					Socket s = new Socket();
					
					InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(addressIp), port);
					
					s.connect(address, 2500);
					s.setSoTimeout(2500);
					
					DataInputStream dis = new DataInputStream(s.getInputStream());
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					DataOutputStream handshake = new DataOutputStream(bos);

					handshake.writeByte(0x00);
					writeVarInt(handshake, 404);
					writeVarInt(handshake, address.getHostString().length());
					handshake.writeBytes(address.getHostString());
					handshake.writeShort(address.getPort());
					writeVarInt(handshake, 1);

					writeVarInt(dos, bos.size());
					dos.write(bos.toByteArray());

					dos.writeByte(0x01);
					dos.writeByte(0x00);

					int size = readVarInt(dis);
					int id = readVarInt(dis);

					if (id == -1) {
						s.close();
						throw new IOException("premature end of stream.");
					}
					if (id != 0x00) {
						s.close();
						throw new IOException("Invalid packet id.");
					}

					int len = readVarInt(dis);
					if (len == -1) {
						s.close();
						throw new IOException("Premature end of stream.");
					}
					if (len == 0) {
						s.close();
						throw new IOException("Invalid String lenght");
					}
					byte[] in = new byte[len];

					dis.readFully(in);

					s.close();

					String json = new String(in);

					JSONObject obj = new JSONObject(json);

					int maxPlayers = obj.getJSONObject("players").getInt("max");
					int onlinePlayers = obj.getJSONObject("players").getInt("online");
					int protocolVersion = obj.getJSONObject("version").getInt("protocol");

					String descText = "";
					if (obj.has("description")) {
						Object objTxt = obj.get("description");
						if (objTxt instanceof String) {
							descText = obj.getString("description");
						} else {
							descText = obj.getJSONObject("description").getString("text");
							if (descText.isEmpty()) {
								JSONArray array = obj.getJSONObject("description").getJSONArray("extra");
								StringBuffer buf = new StringBuffer();
								for (int i = 0; i < array.length(); i++) {
									buf.append(array.getJSONObject(i).getString("text"));
								}
								descText = buf.toString();
							}
						}
					}

					StringBuilder mods = new StringBuilder();
					boolean foundMods = false;
					String type = null;
					if (obj.has("modinfo")) {
						foundMods = true;
						type = obj.getJSONObject("modinfo").getString("type");
						JSONArray array = obj.getJSONObject("modinfo").getJSONArray("modList");
						if (array.length() == 0) { // it is a fake
							foundMods = false;
						}
						if (type.equals("FML")) {
							for (int i = 0; i < array.length(); i++) {
								JSONObject modObj = array.getJSONObject(i);
								String modId = modObj.getString("modid");
								String version = modObj.getString("version");
								mods.append(modId + " ver: " + version + "\n");
							}
						}
					}

					String versionFromMotd = obj.getJSONObject("version").getString("name");

					String data = null;
					String image = null;
					if (obj.has("favicon")) {
						data = obj.getString("favicon").split(",")[1].replaceAll("\n", "");
					}

					descText = descText.replaceAll("§[0-9;a-f;rlonmk]", "");
					descText = descText.replaceAll("\\ {2,}", "");

					if (data != null) {
						byte[] dataBytes = Base64.getDecoder().decode(data.getBytes("UTF-8"));
						String sha = Utils.toB64(Utils.sha256(dataBytes));
						
						PreparedStatement ps = Main.getDBManager().prepareStatement("SELECT faviconUrl FROM favicons WHERE sha = ?;");
						ps.setString(1, sha);

						ResultSet rs = ps.executeQuery();
						
						boolean upload = true;
						if (rs.next()) {
							upload = false;
							image = rs.getString("faviconUrl");
						}

						if (upload) {
							String[] responses = ImgurAPI.upload(dataBytes,
									descText.replaceAll("\n", "").replaceAll("\r", ""));
							image = responses[2];
							
							PreparedStatement ps1 = Main.getDBManager().prepareStatement("INSERT INTO favicons(sha, faviconUrl, id, deleteHash, type) VALUES(?,?,?,?,?);");
							ps1.setString(1, sha);
							ps1.setString(2, responses[2]);
							ps1.setString(3, responses[0]);
							ps1.setString(4, responses[1]);
							ps1.setString(5, responses[3]);
							
							ps1.execute();
						}
					}

					MCVersions mcv = MCVersions.getFromId(protocolVersion);

					EmbedBuilder b = new EmbedBuilder();
					b.withColor(0, 255, 0);
					b.withAuthorName("GravelBot by GravelCZLP");
					b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
					b.withTitle(input);
					b.withDescription("The information about: " + input);
					if (image != null) {
						b.withThumbnail(image);
					}
					b.appendField("Players online", String.valueOf(onlinePlayers), true);
					b.appendField("Max Players", String.valueOf(maxPlayers), true);
					b.appendField("Version", versionFromMotd, true);
					b.appendField("Protocol number", String.valueOf(protocolVersion) + " ("
							+ mcv.name.replaceAll("%VN%", String.valueOf(protocolVersion)) + ")", true);
					b.appendField("MOTD (may look a little weird)", descText, false);
					if (foundMods) {
						if (type.equals("FML")) {
							String url = PasteBinAPI.paste(
									type + ":"
											+ Base64.getEncoder().encodeToString(Utils.sha256(input.getBytes("UTF-8"))),
									mods.toString(), true);
							b.appendField("Mod Info: Forge", "This is the mod list:" + url, false);
						} else {
							b.appendField("Mod info: Unknown",
									"Found mods, but not FML(Forge) type. Did not get the list of mods.", false);
						}
					}
					b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J; v" + Utils.getVersion());
					b.withFooterIcon("https://i.imgur.com/MraElzj.png");
					sendMessage(channel, b.build());
				} catch (Exception e) {
					sendMessage(channel, "Internal error occured: " + e.getClass().getName() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}

			private int readVarInt(DataInputStream dis) throws IOException {
				int i = 0;
				int j = 0;
				while (true) {
					int k = dis.readByte();
					i |= (k & 0x7F) << j++ * 7;
					if (j > 5) {
						throw new RuntimeException("VarInt too big");
					}
					if ((k & 0x80) != 128) {
						break;
					}
				}
				return i;
			}

			private void writeVarInt(DataOutputStream handshake, int i) throws IOException {
				while (true) {
					if ((i & 0xFFFFFF80) == 0) {
						handshake.writeByte(i);
						return;
					}
					handshake.writeByte(i & 0x7F | 0x80);
					i >>>= 7;
				}
			}

			public int getInt(String v, int defaultValue) {
				try {
					return Integer.valueOf(v);
				} catch (Exception e) {
					return defaultValue;
				}
			}
		};

		Thread t = new Thread(r);
		t.setName("Minecraft server info thread.");
		t.start();
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

	private enum MCVersions {

		v1_13_2("1.13.2",
				404),
		v1_13_1("1.13.1",
				401),
		v1_13("1.13",
				393),
		v1_12_2("1.12.2",
				340),
		v1_12_1("1.12.1",
				338),
		v1_12("1.12",
				335),
		v1_11_21("1.11.2/1.11.1",
				316),
		v1_11("1.11",
				315),
		v1_10_x("1.10 - 1.10.2",
				210),
		v1_9_34("1.9.3/1.9.4",
				110),
		v1_9_2("1.9.2",
				109),
		v1_9_1("1.9.1",
				108),
		v1_9("1.9",
				107),
		v1_8_x("1.8.x",
				47),
		v1_7_10("1.7.10",
				5),
		v1_7_245("1.7.2, 1.7.4, 1.7.5",
				4),
		UNKNOWN("Unknown(Snapsnot / lower then 13w41a) (Version number: %VN%)",
				-1);

		private String name;
		private int id;

		private MCVersions(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public static MCVersions getFromId(int id) {
			for (MCVersions v : MCVersions.values()) {
				if (v.id == id) {
					return v;
				}
			}
			return MCVersions.UNKNOWN;
		}
	}
}

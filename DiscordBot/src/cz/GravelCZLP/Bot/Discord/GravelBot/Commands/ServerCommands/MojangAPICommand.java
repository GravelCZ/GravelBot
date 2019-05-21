package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import cz.GravelCZLP.Bot.APIs.Imgur.ImgurAPI;
import cz.GravelCZLP.Bot.APIs.MojangAPI.MojangAPI;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Main.Main;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class MojangAPICommand implements ICommand {

	private HashMap<String, Pair<Long, String>> c = new HashMap<>(); // uuid, last time, base64 png
	
	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length == 0) {
			printHelp(channel);
			return;
		}
		if (args[0].equalsIgnoreCase("nametouuid")) {
			if (args.length >= 2) {
				if (args[1].length() < 4 || args[1].length() > 16) {
					sendMessage(channel, "Name has incorrect format");
					return;
				}
				String[] response = MojangAPI.nameToUUID(args[1]);
				if (response[0] == "1") {
					sendMessage(channel, "Invalid username");
					return;
				}
				sendMessage(channel, "Current name: " + response[1] + " UUID: " + response[0]);
				return;
			} else {
				printHelp(channel);
			}
		} else if (args[0].equalsIgnoreCase("namehistory")) {
			if (args.length >= 2) {
				if (args[1].length() < 4 || args[1].length() > 16) {
					sendMessage(channel, "Name has incorrect format");
					return;
				}
				String[] resp = MojangAPI.nameToUUID(args[1]);
				if (resp[0] == "1") {
					sendMessage(channel, "Invalid username.");
					return;
				}
				Pair<String[], Integer> rs = MojangAPI.getPlayerNameHistory(resp[0]);
				
				String[] response = rs.getKey();
				StringBuffer names = new StringBuffer();
				for (int i = 0; i < response.length; i++) {
					if (i == response.length - 1) {
						names.append(response[i]);
					} else {
						names.append(response[i] + ", ");
					}
				}
				sendMessage(channel, "Username: " + resp[1] + ", Name history: " + names.toString());
				return;
			} else {
				printHelp(channel);
			}
		} else if (args[0].equalsIgnoreCase("skin")) {
			if (args.length >= 2) {
				if (args[1].length() < 4 || args[1].length() > 16) {
					sendMessage(channel, "Name has incorrect format");
					return;
				}
				String[] response = MojangAPI.nameToUUID(args[1]);
				if (response[0] == "1") {
					sendMessage(channel, "Invalid username");
					return;
				}
				boolean ask = true;
				if (c.containsKey(response[0])) {
					Pair<Long, String> v = c.get(response[0]);
					if ((System.currentTimeMillis() - v.getKey()) < 60 * 1000) {
						ask = false;
					}
				}
				Pair<String, Integer> url = null;
				if (ask) {
					url = MojangAPI.getSkinUrl(response[0]);
					if (url.getValue() != 200) {
						sendMessage(channel, "Got response code: " + url.getValue());
						JSONObject obj = new JSONObject(url.getValue());
						sendMessage(channel, obj.getString("TooManyRequestsException") + ": " + obj.getString("errorMessage"));
						return;
					}	
				}
				try {
					Pair<byte[], Integer> dataResp = null;
					if (ask) {
						Logger.debug("Skin URL: " + url.getKey());
						dataResp = Utils.downloadFile(new URL(url.getKey()), new HashMap<>());
						
						if (dataResp.getValue() != 200) {
							sendMessage(channel, "Got response code: " + dataResp.getValue());
							JSONObject obj = new JSONObject(dataResp.getValue());
							sendMessage(channel, obj.getString("TooManyRequestsException") + ": " + obj.getString("errorMessage"));
							return;
						}	
					}
					
					byte[] data = new byte[0];
					if (ask) {
						data = dataResp.getKey();
						c.put(response[0], Pair.of(System.currentTimeMillis(), Utils.toB64(data)));
					} else {
						data = Base64.getUrlDecoder().decode(c.get(response[0]).getValue());
					}
					String sha = Utils.toB64(Utils.sha256(data));
					
					if (args.length >= 3) {
						if (args[2].equalsIgnoreCase("head2d")) {
							BufferedImage fullSkin = ImageIO.read(new ByteArrayInputStream(data));
							BufferedImage head = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
							BufferedImage headSmall = fullSkin.getSubimage(8, 8, 8, 8);
							BufferedImage head2Small = fullSkin.getSubimage(40, 8, 8, 8);
							headSmall.createGraphics().drawImage(head2Small, 0, 0, null);
							AffineTransform at = AffineTransform.getScaleInstance(head.getWidth() / headSmall.getWidth(), head.getHeight() / headSmall.getHeight());
							head.createGraphics().drawRenderedImage(headSmall, at);
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							ImageIO.write(head, "png", bos);
							data = bos.toByteArray();
							sha = Utils.toB64(Utils.sha256(data));
						}
						if (args[2].equalsIgnoreCase("head3d")) {
							Pair<byte[], Integer> resp = Utils.downloadFile(new URL("https://crafatar.com/renders/head/" + response[0] + "?overlay"), new HashMap<>());
							if ((int) (resp.getValue() / 100) != 2) {
								sendMessage(channel, "Got response code: " + resp.getValue());
								return;
							}
							
							data = resp.getKey();
							sha = Utils.toB64(Utils.sha256(data));
						}
						if (args[2].equalsIgnoreCase("skin3d")) {
							Pair<byte[], Integer> resp = Utils.downloadFile(new URL("https://crafatar.com/renders/body/" + response[0] + "?overlay"), new HashMap<>());
							if ((int) (resp.getValue() / 100) != 2) {
								sendMessage(channel, "Got response code: " + resp.getValue());
								return;
							}
							data = resp.getKey(); 
							sha = Utils.toB64(Utils.sha256(data));
						}
					}
					
					PreparedStatement ps = Main.getDBManager().prepareStatement("SELECT url FROM playerSkins WHERE hash = ?;");
					ps.setString(1, sha);
					
					ResultSet rs = ps.executeQuery();
					
					String urlSkin = "";
					boolean upload = true;
					
					if (rs.next()) {
						upload = false;
						urlSkin = rs.getString("url");
					}

					if (upload) {
						String[] resp = ImgurAPI.upload(data, response[0]);
						urlSkin = resp[2];
						
						PreparedStatement ps1 = Main.getDBManager().prepareStatement("INSERT INTO playerSkins(hash, url, deletehash, id, type) VALUES(?,?,?,?,?)");
						ps1.setString(1, sha);
						ps1.setString(2, resp[2]);
						ps1.setString(3, resp[1]);
						ps1.setString(4, resp[0]);
						ps1.setString(5, resp[3]);
						ps1.execute();
					}
					
					EmbedBuilder b = new EmbedBuilder();
					b.withColor(0, 255, 0);
					b.withAuthorName("GravelBot by GravelCZLP");
					b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
					
					b.withTitle(response[1]);
					b.withImage(urlSkin);
					
					b.withFooterText("GravelCZLP - Author | Bot writren in Java | API is Discord4J | v" + Utils.getVersion());
					b.withFooterIcon("https://i.imgur.com/MraElzj.png");
					sendMessage(channel, b.build());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void printHelp(IChannel channel) {
		EmbedBuilder b = new EmbedBuilder();
		b.withColor(0, 255, 0);
		b.withAuthorName("GravelBot by GravelCZLP");
		b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
		
		b.appendField("nametouuid [name]", "Returns the uuid of the user name.", false);
		b.appendField("namehistory [name]", "Returns the name history of the player.", false);
		b.appendField("skin [name]", "Sends you the skin of the player.", false);
		
		b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J; v" + Utils.getVersion());
		b.withFooterIcon("https://i.imgur.com/MraElzj.png");
		sendMessage(channel, b.build());
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}
	
}

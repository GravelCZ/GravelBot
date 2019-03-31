package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import cz.GravelCZLP.Bot.APIs.Imgur.ImgurAPI;
import cz.GravelCZLP.Bot.APIs.MojangAPI.MojangAPI;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class MojangAPICommand implements ICommand {

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
				String[] response = MojangAPI.getPlayerNameHistory(resp[0]);
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
				String url = MojangAPI.getSkinUrl(response[0]);
				try {
					Logger.debug("Skin URL: " + url);
					byte[] data = Utils.downloadFile(new URL(url), new HashMap<>());
					String sha = Utils.toB64(Utils.sha256(data));
					
					File folder = new File("./BotDataFolder/mcskins/");
					if (!folder.exists()) {
						folder.mkdirs();
					}
					File lookupFile = new File("./BotDataFolder/mcskins/playerSkins.txt");
					if (!lookupFile.exists()) {
						lookupFile.createNewFile();
					}
					
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
							data = Utils.downloadFile(new URL("https://crafatar.com/renders/head/" + response[0] + "?overlay"), new HashMap<>());
							sha = Utils.toB64(Utils.sha256(data));
						}
						if (args[2].equalsIgnoreCase("skin3d")) {
							data = Utils.downloadFile(new URL("https://crafatar.com/renders/body/" + response[0] + "?overlay"), new HashMap<>());
							sha = Utils.toB64(Utils.sha256(data));
						}
					}
					
					String[] lines = Utils.getLinesFromFile(lookupFile);
					String urlSkin = "";
					boolean upload = true;
					for (int i = 0; i < lines.length; i++) {
						String[] split = lines[i].split(";");
						if (split[0].equalsIgnoreCase(sha)) {
							urlSkin = split[1];
							upload = false;
							break;
						}
					}
					if (upload) {
						String[] resp = ImgurAPI.upload(data, response[0]);
						urlSkin = resp[2];
						String finalString = sha + ";" + resp[2] + ";" + resp[1] + ";" + resp[0] + ";" + resp[3];
						Utils.appendToFile(lookupFile, finalString);
					}
					
					EmbedBuilder b = new EmbedBuilder();
					b.withColor(0, 255, 0);
					b.withAuthorName("GravelBot by GravelCZLP");
					b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
					
					b.withTitle(response[1]);
					b.withImage(urlSkin);
					
					b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J; v" + Utils.getVersion());
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

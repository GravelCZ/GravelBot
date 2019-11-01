package cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands.Admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IPrivateCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZ.Bot.Utils.Logger;
import cz.GravelCZ.Bot.Utils.Pair;
import cz.GravelCZ.Bot.Utils.Utils;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class SetPFPCommand implements IPrivateCommand {

	private long lastImageChange = 0;

	public SetPFPCommand() {
		File f = new File("./BotDataFolder/profilePics/");
		if (!f.exists()) {
			f.mkdirs();
		}
	}
	@Override
	public void execute(Message msg, PrivateChannel channel, User user, String content, String[] args) {
		if ((System.currentTimeMillis() - lastImageChange) < 10.5 * 60 * 1000) {
			sendMessage(channel, "I cannot change my profile picture this fast.");
			return;
		}
		List<Attachment> attachments = msg.getAttachments();
		if (attachments.size() == 0 && args.length == 0) {
			sendMessage(channel, "This command needs to have an Image attachment with it or URL as an argument.");
			return;
		}
		String urlTxt = null;
		if (attachments.size() != 0) {
			urlTxt = attachments.get(0).getUrl();
		} else if (args.length != 0) {
			urlTxt = args[0];
		}
		if (args.length != 0 && args[0].equalsIgnoreCase("user")) {
			if (args.length >= 2) {
				long id = 0;
				try {
					id = Long.parseLong(args[1]);
				} catch (NumberFormatException e) {
					sendMessage(channel, "Not a valid id.");
					return;
				}	
				User u = msg.getJDA().retrieveUserById(id).complete();
				if (u == null) {
					sendMessage(channel, "Cannot get the user, does the user exist and can the bot see him ?(They need to have mutual guild)");
					return;
				}
				urlTxt = u.getAvatarUrl();
			}
		}
		
		Logger.log(urlTxt);
		
		boolean isImage = Utils.isImage(urlTxt) || Utils.isGif(urlTxt) || urlTxt.endsWith(".webp");
		if (!isImage) {
			sendMessage(channel, "That is not a valid image, it needs to be .png, .jpg, .jpeg");
			return;
		}
		
		URL url = null;
		try {
			url = new URL(urlTxt);
		} catch (MalformedURLException e) {
			sendMessage(channel, "That is not a valid URL");
			e.printStackTrace();
			return;
		}
		
		Pair<byte[], Integer> imageResp = Utils.downloadFile(url, new HashMap<>());
		if (imageResp.getValue() != 200) {
			sendMessage(channel, "Got response code: " + imageResp.getValue());
			return;
		}
		
		byte[] image = imageResp.getKey();
		String name = Utils.toB64(Utils.sha256(image));
		int i = urlTxt.lastIndexOf('.');
		String suffix = "png";
		if (i > 0) {
			suffix = urlTxt.substring(i + 1);
		}
		File f = new File("./BotDataFolder/profilePics/" + name + "." + suffix);
		
		try {
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(image);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (f.exists()) {
			try {
				msg.getJDA().getSelfUser().getManager().setAvatar(Icon.from(f)).queue();
			} catch (IOException e) {
				e.printStackTrace();
			}
			channel.sendMessage("I changed my profile picture!").queue();
		}
		
		lastImageChange = System.currentTimeMillis();
		
	}

	@Override
	public boolean canExecute(User user) {
		return PermissionsService.isGravel(user);
	}

}

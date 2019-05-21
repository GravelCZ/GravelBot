package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PrivateCommands.Admin;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.RequestResponse;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IMessage.Attachment;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuffer.RequestFuture;

public class SetPFPCommand implements ICommand {

	private String gravel = "GravelCZLP;331866918023725056";

	private long lastImageChange = 0;

	public SetPFPCommand() {
		File f = new File("./BotDataFolder/profilePics/");
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
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
		if (args[0].equalsIgnoreCase("user")) {
			if (args.length >= 2) {
				long id = 0;
				try {
					id = Long.parseLong(args[1]);
				} catch (NumberFormatException e) {
					sendMessage(channel, "Not a valid id.");
					return;
				}	
				IUser user = msg.getClient().getUserByID(id);
				if (user == null) {
					RequestFuture<IUser> userFuture = RequestBuffer.request(new RequestResponse<IUser, Long>(id) {
						
						@Override
						public IUser request() {
							return msg.getClient().fetchUser(this.s);
						}
						
					});
					user = userFuture.get();
				}
				if (user == null) {
					sendMessage(channel, "Cannot get the user, does the user exist and can the bot see him ?(They need to have mutual guild)");
					return;
				}
				urlTxt = user.getAvatarURL();
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
		String suffix = FilenameUtils.getExtension(urlTxt);
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
			RequestBuffer.request(() -> {
				msg.getClient().changeAvatar(Image.forFile(f));
				channel.sendMessage("I changed my profile picture!");
			});
		}
		
		lastImageChange = System.currentTimeMillis();
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		String[] split = gravel.split(";");
		if (user.getName().equals(split[0]) && user.getStringID().equals(split[1])) {
			return true;
		}
		return false;
	}

}

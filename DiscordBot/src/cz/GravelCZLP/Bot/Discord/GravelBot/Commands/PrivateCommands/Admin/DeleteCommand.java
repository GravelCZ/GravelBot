package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PrivateCommands.Admin;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.GravelCZLP.Bot.APIs.Imgur.ImgurAPI;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Main.Main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class DeleteCommand implements ICommand {

	private String gravel = "GravelCZLP;331866918023725056";
	
	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length == 0) {
			return;
		}
		try {
			if (args[0].equalsIgnoreCase("skins")) {
				ResultSet rs = Main.getDBManager().executeQuery("SELECT hash,deleteHash FROM playerSkins;");
				while (rs.next()) {
					String deleteHash = rs.getString("deleteHash");
					ImgurAPI.delete(deleteHash);
					PreparedStatement ps = Main.getDBManager().prepareStatement("DELETE FROM playerSkins WHERE hash = ?");
					ps.setString(1, rs.getString("hash"));
					ps.execute();
				}
			} else if (args[0].equalsIgnoreCase("pfps")) {
				File pfpFolder = new File("./BotDataFolder/profilePics/");
				List<File> files = new ArrayList<>( Arrays.asList(pfpFolder.listFiles()));
				files.forEach(f -> f.delete());
			} else if (args[0].equalsIgnoreCase("favs")) {
				ResultSet rs = Main.getDBManager().executeQuery("SELECT sha,deleteHash FROM favicons;");
				while (rs.next()) {
					String deleteHash = rs.getString("deleteHash");
					ImgurAPI.delete(deleteHash);
					PreparedStatement ps = Main.getDBManager().prepareStatement("DELETE FROM favicons WHERE sha = ?");
					ps.setString(1, rs.getString("sha"));
					ps.execute();
				}
			} else if (args[0].equalsIgnoreCase("deepfried")) {
				File deepFried = new File("./BotDataFolder/deepfryer/deepfried/");
				File originals = new File("./BotDataFolder/deepfryer/originals/");
				List<File> files = new ArrayList<>();
				files.addAll(Arrays.asList(deepFried.listFiles()));
				files.addAll(Arrays.asList(originals.listFiles()));
				files.forEach(f -> f.delete());
			}
			sendMessage(channel, "Done.");
		} catch (Exception e) {
			sendMessage(channel, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
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

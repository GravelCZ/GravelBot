package cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands.Admin;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.GravelCZ.Bot.APIs.Imgur.ImgurAPI;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IPrivateCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZ.Bot.Main.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class DeleteCommand implements IPrivateCommand {

	@Override
	public void execute(Message msg, PrivateChannel channel, User user, String content, String[] args) {
		if (args.length == 0) {
			return;
		}
		try {
			if (args[0].equalsIgnoreCase("skins")) {
				ResultSet rs = Main.getDBManager().getSource().getConnection().prepareStatement("SELECT hash,deleteHash FROM playerSkins;").executeQuery();
				while (rs.next()) {
					String deleteHash = rs.getString("deleteHash");
					ImgurAPI.delete(deleteHash);
					PreparedStatement ps = Main.getDBManager().getSource().getConnection().prepareStatement("DELETE FROM playerSkins WHERE hash = ?");
					ps.setString(1, rs.getString("hash"));
					ps.execute();
				}
			} else if (args[0].equalsIgnoreCase("pfps")) {
				File pfpFolder = new File("./BotDataFolder/profilePics/");
				List<File> files = new ArrayList<>( Arrays.asList(pfpFolder.listFiles()));
				files.forEach(f -> f.delete());
			} else if (args[0].equalsIgnoreCase("favs")) {
				ResultSet rs = Main.getDBManager().getSource().getConnection().prepareStatement("SELECT sha,deleteHash FROM favicons;").executeQuery();
				while (rs.next()) {
					String deleteHash = rs.getString("deleteHash");
					ImgurAPI.delete(deleteHash);
					PreparedStatement ps = Main.getDBManager().getSource().getConnection().prepareStatement("DELETE FROM favicons WHERE sha = ?");
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
	public boolean canExecute(User user) {
		return PermissionsService.isGravel(user);
	}

}

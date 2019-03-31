package cz.GravelCZLP.Bot.Discord.GravelBot.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class PermissionsService {

	private static List<String> admins = new ArrayList<>(Arrays.asList("GravelCZLP;331866918023725056"));
	private static List<String> roles = new ArrayList<>(Arrays.asList("Admin"));
	private static String botRoleName = "GravelBot";

	public static boolean isAdmin(IUser user, IGuild guild) {
		List<IRole> Iroles = user.getRolesForGuild(guild);
		for (String name : admins) {
			String[] split = name.split(";");
			if (user.getName().equals(split[0])) {
				if (user.getStringID().equals(split[1])) {
					return true;
				}
			}
		}
		for (String role : roles) {
			for (IRole irole : Iroles) {
				if (!irole.isManaged()) {
					if (irole.getName().contains(role)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean canExecute(IUser user, IGuild guild, boolean adminCommand, boolean musicBotCommand) {
		List<IRole> Iroles = user.getRolesForGuild(guild);
		for (String name : admins) {
			String[] split = name.split(";");
			if (user.getName().equals(split[0])) {
				if (user.getStringID().equals(split[1])) {
					return true;
				}
			}
		}
		if (adminCommand) {
			return isAdmin(user, guild);
		}
		if (musicBotCommand) {
			if (isAdmin(user, guild)) {
				return true;
			}
			for (IRole irole : Iroles) {
				if (!irole.isManaged()) {
					if (irole.getName().contains(botRoleName)) {
						return true;
					}
				}
			}
		}
		Logger.debug("User: " + user.getName() + " does not have a permission.");
		return false;
	}

}

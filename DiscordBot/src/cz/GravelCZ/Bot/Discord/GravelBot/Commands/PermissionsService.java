package cz.GravelCZ.Bot.Discord.GravelBot.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class PermissionsService {

	private static List<String> admins = new ArrayList<>(Arrays.asList("331866918023725056"));
	private static List<String> roles = new ArrayList<>(Arrays.asList("Admin"));
	private static String botRoleName = "GravelBot";

	public static boolean isAdmin(Member member) {
		for (String id : admins) {
			boolean flag = member.getId().equals(id);
			if (flag)
			{
				return true;
			}
		}
		
		for (String role : roles) {
			boolean flag = member.getRoles().stream().anyMatch(r -> !r.isManaged() && r.getName().contains(role));
			if (flag) 
			{
				return true;
			}
		}
		return false;
	}

	public static boolean canExecute(Member member, boolean adminCommand, boolean musicBotCommand) {
		List<Role> roles = member.getRoles();
		for (String id : admins) {
			if (member.getId().equals(id)) {
				return true;
			}
		}
		if (adminCommand) {
			return isAdmin(member);
		}
		if (musicBotCommand) {
			if (isAdmin(member)) {
				return true;
			}
			boolean flag = roles.stream().anyMatch(r -> r.isManaged() && r.getName().contains(botRoleName));
			if (flag) {
				return true;
			}
		}
		Logger.debug("User: " + member.getUser().getName() + " does not have a permission.");
		return false;
	}

	public static boolean isGravel(User user) {
		return user.getId().equals("331866918023725056");
	}

}

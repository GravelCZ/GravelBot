package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ThanosSnapCommand implements IServerCommand {

	private boolean dryRun = true;
	
	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		Member me = g.getSelfMember();
		if (!me.getPermissions().stream().anyMatch(p -> p == Permission.BAN_MEMBERS)) {
			sendMessage((TextChannel) channel, "I have not obtained all the infinity stones yet.");
			return;
		}
		List<Member> members = new ArrayList<>( g.getMembers() );
		members.removeIf(member -> member.getUser().isBot() || member.getPermissions().contains(Permission.BAN_MEMBERS) 
				|| member.getPermissions().contains(Permission.ADMINISTRATOR)
				|| member.getPermissions().contains(Permission.MANAGE_SERVER)
				|| member.getIdLong() == g.getOwnerIdLong());
		
		Collections.shuffle(members);
		StringBuffer bannedUsers = new StringBuffer();
		
		for (int i = 0; i < members.size() / 2; i++) {
			Member user = members.get(i);
			bannedUsers.append(" " + user.getUser().getName() + (user.getNickname()));
			if (!dryRun) {
				g.ban(user, 0, "You have been snaped out of existence. (" + g.getName() + ")").queue();;
			} else {
				Logger.log("Would ban user: " + user.getUser().getName());
			}
		}
		if (!dryRun) {
			String bannedUsersNames = bannedUsers.toString();
			if (bannedUsersNames.length() + "Banned users: ".length() >= 2000) {
				int size = (int) Math.floor(bannedUsersNames.length() / 2000.0);
				String[] bannedUsersArray = new String[size];
				for (int i = 0; i < bannedUsersArray.length; i++) {
					bannedUsersArray[i] = "";
				}
				for (int i = 0; i < 2000; i++) { 
					int arrayToUse = (int) Math.floor(i / 2000.0);
					bannedUsersArray[arrayToUse] += bannedUsersNames.charAt(i);
				}
				sender.getUser().openPrivateChannel().queue(c -> {
					c.sendMessage("Banned users: ");
				});
				for (int i = 0; i < bannedUsersArray.length; i++) {
					int j = i;
					sender.getUser().openPrivateChannel().queue(c -> {
						c.sendMessage(bannedUsersArray[j]);
					});
				}
			} else {
				sender.getUser().openPrivateChannel().queue(c -> {
					c.sendMessage(bannedUsersNames);
				});
			}	
		}
	}
	
	@Override
	public boolean canExecute(Member m) {
		return PermissionsService.canExecute(m, true, false);
	}

}

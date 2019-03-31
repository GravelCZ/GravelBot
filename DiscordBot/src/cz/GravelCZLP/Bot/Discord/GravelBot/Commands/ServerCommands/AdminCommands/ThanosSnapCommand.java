package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

public class ThanosSnapCommand implements ICommand {

	private boolean dryRun = true;
	
	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		IUser ourUser = guild.getClient().getOurUser();
		EnumSet<Permissions> s = ourUser.getPermissionsForGuild(guild);
		if (!s.contains(Permissions.BAN)) {
			sendMessage(channel, "I have not obtained all the infinity stones yet.");
			return;
		}
		List<IUser> users = new ArrayList<>( guild.getUsers() );
		users.removeIf(user -> user.isBot() || user.getPermissionsForGuild(guild).contains(Permissions.BAN) 
				|| user.isBot() || user.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)
				|| user.isBot() || user.getPermissionsForGuild(guild).contains(Permissions.MANAGE_SERVER)
				|| user.getLongID() == guild.getOwnerLongID());
		
		Collections.shuffle(users);
		StringBuffer bannedUsers = new StringBuffer();
		
		for (int i = 0; i < users.size() / 2; i++) {
			IUser user = users.get(i);
			bannedUsers.append(" " + user.getName() + (user.getNicknameForGuild(guild)));
			if (!dryRun) {
				RequestBuffer.request(() -> {
					guild.banUser(user, "You have been snaped out of existence. (" + guild.getName() + ")");
				});
			} else {
				Logger.log("Would ban user: " + user.getName());
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
				sendMessage(sender.getOrCreatePMChannel(), "Banned users: ");
				for (int i = 0; i < bannedUsersArray.length; i++) {
					int j = i;
					sendMessage(sender.getOrCreatePMChannel(), bannedUsersArray[j]);
				}
			} else {
				sendMessage(sender.getOrCreatePMChannel(), bannedUsersNames);
			}	
		}
	}
	
	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, true, false);
	}

}

package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZLP.Bot.Discord.GravelBot.Listeners.ShitpostHandler;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class NoShitPostCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length < 1) {
			sendMessage(channel, "Usage: !/shitpost [enable/disable]");
			return;
		}
		if (args[0].equalsIgnoreCase("enable")) {
			ShitpostHandler.unblock(guild);
			sendMessage(channel, "I have enabled shitpost features on this server.");
		} else if (args[0].equalsIgnoreCase("disable")) {
			ShitpostHandler.block(guild);
			sendMessage(channel, "I have disabled shitpost features on this server.");
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, true, false);
	}

}

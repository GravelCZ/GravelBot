package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZ.Bot.Discord.GravelBot.Listeners.ShitpostHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class NoShitPostCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		if (args.length < 1) {
			sendMessage(channel, "Usage: !/shitpost [enable/disable]");
			return;
		}
		if (args[0].equalsIgnoreCase("enable")) {
			ShitpostHandler.unblock(g);
			sendMessage(channel, "I have enabled shitpost features on this server.");
		} else if (args[0].equalsIgnoreCase("disable")) {
			ShitpostHandler.block(g);
			sendMessage(channel, "I have disabled shitpost features on this server.");
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, true, false);
	}

}

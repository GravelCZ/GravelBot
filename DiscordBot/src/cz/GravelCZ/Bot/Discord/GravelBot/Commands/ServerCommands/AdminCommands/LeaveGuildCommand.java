package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class LeaveGuildCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		sendMessage(channel, "Alright, i am going to leave the server. But first i will inform the owner");
		sendMessage(sender.getGuild().getOwner().getUser().openPrivateChannel().complete(), "I am leaving your server (" + sender.getGuild().getName() + ") on the request of " + msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator());
		g.leave().queue();;
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, true, false);
	}

}

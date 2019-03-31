package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

public class LeaveGuildCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		sendMessage(channel, "Alright, i am going to leave the server. But first i will inform the owner");
		sendMessage(guild.getOwner().getOrCreatePMChannel(), "I am leaving your server (" + guild.getName() + ") on the request of " + msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator());
		RequestBuffer.request(() -> {
			guild.leave();
		});
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, true, false);
	}
}

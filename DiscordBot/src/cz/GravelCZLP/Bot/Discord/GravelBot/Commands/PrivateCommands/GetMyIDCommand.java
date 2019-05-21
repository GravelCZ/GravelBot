package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PrivateCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

public class GetMyIDCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		RequestBuffer.request(() -> {
			channel.sendMessage(String.valueOf(sender.getLongID()));
		});
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}

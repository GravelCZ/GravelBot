package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class FlipACoinCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser user, IGuild guild, String content, String[] args) {
		double d = Utils.getRandom().nextDouble();
		Logger.log("User: " + user.getName() + " fliped a coin. Result: " + d);
		if (d == 0.0) {
			sendMessage(channel, user.mention() + " Sorry, it landed on its side. (0)");
		} else if (d > 0 && d <= 0.5) {
			sendMessage(channel, user.mention() + " Its Heads. (" + d + ")");
		} else if (d > 0.5 && d <= 1.0) {
			sendMessage(channel, user.mention() + " Its Tails. (" + d + ")");
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}

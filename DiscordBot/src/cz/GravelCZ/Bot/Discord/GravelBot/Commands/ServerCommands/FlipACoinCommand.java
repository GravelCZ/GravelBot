package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Utils.Logger;
import cz.GravelCZ.Bot.Utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class FlipACoinCommand implements IServerCommand {


	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		double d = Utils.getRandom().nextDouble();
		Logger.log("User: " + sender.getUser().getName() + " fliped a coin. Result: " + d);
		if (d == 0.0) {
			sendMessage(channel, sender.getAsMention() + " Sorry, it landed on its side. (0)");
		} else if (d > 0 && d <= 0.5) {
			sendMessage(channel, sender.getAsMention() + " Its Heads. (" + d + ")");
		} else if (d > 0.5 && d <= 1.0) {
			sendMessage(channel, sender.getAsMention() + " Its Tails. (" + d + ")");
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return true;
	}

}

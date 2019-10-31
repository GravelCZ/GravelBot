package cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IPrivateCommand;
import cz.GravelCZ.Bot.Utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
public class PrivateHelpCommand implements IPrivateCommand {

	@Override
	public void execute(Message msg, PrivateChannel channel, User user, String content, String[] args) {
		EmbedBuilder b = EmbedUtils.newBuilder();
		b.addField("!/help", "Displays this help", false);
		b.addField("!/rule34 [tags]", "You pervert", false);
		b.addField("!/deleteall", "Deletes all the mesages bot has sent, if you need this for some reason", false);
		b.addField("!/getmyid", "Returns your snowflake ID", false);
		b.addField("!/setpfp [url] (or image attachment)", "This will chnage my profile picture, you need to be GravelCZ tho.", false);
		sendMessage(channel, b.build());
	}

	@Override
	public boolean canExecute(User user) {
		return true;
	}

}

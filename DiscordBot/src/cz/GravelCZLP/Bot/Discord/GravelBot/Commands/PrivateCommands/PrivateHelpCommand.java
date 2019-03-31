package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PrivateCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class PrivateHelpCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		EmbedBuilder b = new EmbedBuilder();
		b.withTitle("Help for GravelBot");
		b.withColor(0, 255, 0);
		b.withAuthorName("GravelBot by GravelCZLP");
		b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
		b.appendField("!/help", "Displays this help", false);
		b.appendField("!/rule34 [tags]", "You pervert", false);
		b.appendField("!/deleteall", "Deletes all the mesages bot has sent, if you need this for some reason", false);
		b.appendField("!/getmyid", "Returns your snowflake ID", false);
		b.appendField("!/setpfp [url] (or image attachment)", "This will chnage my profile picture, you need to be GravelCZLP tho.", false);
		b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J; v" + Utils.getVersion());
		b.withFooterIcon("https://i.imgur.com/MraElzj.png");
		sendMessage(channel, b.build());
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}

package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class AboutCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		EmbedBuilder b = new EmbedBuilder();
		b.withTitle("About GravelBot");
		b.withColor(0, 255, 0);
		b.withAuthorName("GravelBot by GravelCZLP");
		b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
		b.appendField("Hello o/", "My name is GravelBot and i can do cool shit! !/help for help", false);
		b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J");
		b.withFooterIcon("https://i.imgur.com/MraElzj.png");
		sendMessage(channel, b.build());
	}
	
	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}
	
}

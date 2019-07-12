package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Runnables.PewDiePieVsTGayRunnable;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class TheYTWarCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {		
		EmbedBuilder b = new EmbedBuilder();
		b.withTitle("PewDiePie VS T-Series");
		b.withDescription("Fight of the century");
		b.withColor(255, 0, 0);
		b.withImage("https://i.imgur.com/s2TznU6.jpg");
		b.appendField("Subsciber Difference", String.valueOf(PewDiePieVsTGayRunnable.getSubDifference()), true);
		b.appendField("Has PewDiePie Lost?", PewDiePieVsTGayRunnable.hasPdpLost() ? "**Yes** RIP!!" : "No", true);
		b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J; v" + Utils.getVersion());
		b.withFooterIcon("https://i.imgur.com/MraElzj.png");
		sendMessage(channel, b.build());
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}

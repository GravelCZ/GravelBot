package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class ServerHelpCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		EmbedBuilder b = new EmbedBuilder();
		b.withTitle("Help for GravelBot");
		b.withColor(0, 255, 0);
		b.withAuthorName("GravelBot by GravelCZLP");
		b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
		b.withThumbnail("https://i.imgur.com/6KakBeM.png");
		b.appendField("__Commands for EVERYONE__:", "Everyone can use them.", false);
		b.appendField("!/help", "Displays this help.", false);
		b.appendField("!/flipacoin", "Flips a coin.", false);
		b.appendField("!/randomwikiarticle", "Gives you a random article from Wikipedia.", false);
		b.appendField("!/bestgirl", "Sends you an image of the best girl.", false);
		b.appendField("!/deepfry [URL or image attachment] [optional: force]", "This will deep fry the image\n"
				+ "args: force - this will force the bot to re-deep fry the image", false);
		b.appendField("!/question", "The bot gives you a random question.", false);
		b.appendField("!/answer [answer (can have spaces)]", "You answer the bots question", false);
		b.appendField("__Question Answer Mechansm__", "**You type !/question the bot sends you a question\n" 
				+ "You type __!/answer [answer]__ to answer the bot. You have one shot at this.\n"
				+ "If you __guess wrong__, the bot will give you the right answer.\n"
				+ "If you dont know the answer type: __!/answer [anything]__ and the bot will tell you the correct answer.**", false);
		b.appendField("__Admin commands__:", "Only for admins", false);
		b.appendField("!/deleteall", "Deletes the whole history of the channel, aka: every single message in this channel from all users", false);
		b.appendField("!/shitpost [enable/disable]", "Will block/unblock shitposting features on this server", false);
		b.appendField("!/thanosnap", "Will ban half the users on this server (Does not include: Bots, users with permission: Ban, Administrator, Manage Server) (Currently disabled)", false);
		
		b.appendField("__MusicBot Commands__:", "For the Music bot.", false);
		b.appendField("!/join", "Joins your current voice channel.", false);
		b.appendField("!/leave", "Leaves the voice channel, but does not stop playing.", false);
		b.appendField("!/play", "Plays the default audio stream from Gravel.", false);
		b.appendField("!/twitch", "Play live stream from twitch", false);
		b.appendField("!/stop", "Stops playing.", false);
		b.appendField("!/volume [0 to 100]", "Sets the volume of the bot. If you go too high he __**might not like that**__.", false);
		b.appendField("__QnA__:", "It is QnA Time", false);
		b.appendField("How many possible questions are there ?", "About **" + Utils.getLoadedQuestions() + "**", false);
		b.appendField("Who is the (cute) girl the bot has as a profile picture ?", "It is Yuno Gasai from Mirai Nikki when she goes **full psycho!**", false);
		b.appendField("__I GUESS__", "__**THEY**__", true);
		b.appendField("__NEVER MISS__", "__**HUH?**__", true);
		b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J; v" + Utils.getVersion());
		b.withFooterIcon("https://i.imgur.com/MraElzj.png");
		sendMessage(sender.getOrCreatePMChannel(), b.build());
	}
	
	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}
	
}

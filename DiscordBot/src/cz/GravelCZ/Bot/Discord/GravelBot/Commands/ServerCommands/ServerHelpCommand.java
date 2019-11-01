package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Utils.EmbedUtils;
import cz.GravelCZ.Bot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ServerHelpCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		EmbedBuilder b = EmbedUtils.newBuilder();
		b.setTitle("Help for GravelBot");
		b.setThumbnail("https://i.imgur.com/ZLbudIA.jpg");
		b.addField("__Commands for EVERYONE__:", "Everyone can use them.", false);
		b.addField("!/help", "Displays this help.", false);
		b.addField("!/flipacoin", "Flips a coin.", false);
		b.addField("!/randomwikiarticle", "Gives you a random article from Wikipedia.", false);
		b.addField("!/bestgirl", "Sends you an image of the best girl.", false);
		b.addField("!/deepfry [URL or image attachment] [optional: force]", "This will deep fry the image\n"
				+ "args: force - this will force the bot to re-deep fry the image", false);
		b.addField("!/question", "The bot gives you a random question.", false);
		b.addField("!/answer [answer (can have spaces)]", "You answer the bots question", false);
		b.addField("__Question Answer Mechansm__", "**You type !/question the bot sends you a question\n" 
				+ "You type __!/answer [answer]__ to answer the bot. You have one shot at this.\n"
				+ "If you __guess wrong__, the bot will give you the right answer.\n"
				+ "If you dont know the answer type: __!/answer [anything]__ and the bot will tell you the correct answer.**", false);
		b.addField("__Admin commands__:", "Only for admins", false);
		//b.addField("!/deleteall", "Deletes the whole history of the channel, aka: every single message in this channel from all users", false);
		b.addField("!/shitpost [enable/disable]", "Will block/unblock shitposting features on this server", false);
		b.addField("!/thanosnap", "Will ban half the users on this server (Does not include: Bots, users with permission: Ban, Administrator, Manage Server) (Currently disabled)", false);
		
		b.addField("__MusicBot Commands__:", "For the Music bot.", false);
		b.addField("!/join", "Joins your current voice channel.", false);
		b.addField("!/leave", "Leaves the voice channel, but does not stop playing.", false);
		b.addField("!/play", "Plays the default audio stream from Gravel.", false);
		b.addField("!/twitch", "Play live stream from twitch", false);
		b.addField("!/stop", "Stops playing.", false);
		b.addField("!/volume [0 to 100]", "Sets the volume of the bot. If you go too high he __**might not like that**__.", false);
		b.addField("__QnA__:", "It is QnA Time", false);
		b.addField("How many possible questions are there ?", "About **" + Utils.getLoadedQuestions() + "**", false);
		b.addField("Who is the (cute) girl the bot has as a profile picture ?", "It is Zero Two from Darling in the Franxx", false);
		sendMessage(sender.getUser().openPrivateChannel().complete(), b.build());
	}

	@Override
	public boolean canExecute(Member user) {
		return true;
	}
	
}

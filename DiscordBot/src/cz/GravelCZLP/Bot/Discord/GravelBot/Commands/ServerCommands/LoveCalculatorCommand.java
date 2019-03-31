package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class LoveCalculatorCommand implements ICommand {

	private Pattern mention = Pattern.compile("\\<\\@\\!?[0-9]{18}\\>");
	private Pattern id = Pattern.compile("[0-9]{18}");
	
	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length < 2) {
			sendMessage(channel, sender.mention() + " you need to mention 2 users.");
			return;
		}
		boolean firstArg = mention.matcher(args[0]).matches();
		boolean secArg = mention.matcher(args[1]).matches();
		if (firstArg && secArg) {
			Matcher m1 = id.matcher(args[0]);
			Matcher m2 = id.matcher(args[1]);
			m1.find();
			m2.find();
			
			long id1 = Long.valueOf(m1.group());
			long id2 = Long.valueOf(m2.group());
			IUser user1 = guild.getUserByID(id1);
			IUser user2 = guild.getUserByID(id2);			
			String input = user1.getDisplayName(guild) + ":" + user1.getStringID() + "+" + user2.getDisplayName(guild) + ":" + user2.getStringID();
			int output = Utils.gravelHash(input);
			
			Random r = new Random(output);
			
			int love = r.nextInt(100);
			
			EmbedBuilder e = new EmbedBuilder();
			e.withColor(0, 255, 0);
			if (love > 70) {
				e.appendField("The love between " + user1.getDisplayName(guild) + " and " + user2.getDisplayName(guild) + " is:", ":heart: " + String.valueOf(love) + " :heart:", false);	
			} else {
				e.appendField("The love between " + user1.getDisplayName(guild) + " and " + user2.getDisplayName(guild) + " is:", ":broken_heart: " + String.valueOf(love) + " :broken_heart:", false);
			}
			
			sendMessage(channel, e.build());
			
		} else {
			sendMessage(channel, sender.mention() + " you need to mention 2 users.");
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}


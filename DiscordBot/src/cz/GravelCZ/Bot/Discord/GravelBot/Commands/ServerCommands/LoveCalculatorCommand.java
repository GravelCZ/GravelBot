package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.util.List;
import java.util.Random;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Utils.EmbedUtils;
import cz.GravelCZ.Bot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class LoveCalculatorCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		if (args.length != 2) {
			sendMessage(channel, sender.getAsMention() + " you need to mention 2 users.");
			return;
		}
		
		List<Member> members = msg.getMentionedMembers();
		if (members.size() == 2) {
			Member m1 = members.get(0);
			Member m2 = members.get(1);
			String user1String = m1.getUser().getName() + ":" + m1.getId() + ":" + m1.getUser().getDiscriminator();
			String user2String = m2.getUser().getName() + ":" + m2.getId() + ":" + m2.getUser().getDiscriminator();
			
			String input = "";
			if (user1String.hashCode() > user2String.hashCode()) {
				input = user1String + "+" + user2String;
			} else {
				input = user2String + "+" + user1String;
			}
			
			int output = Utils.gravelHash(input);
			
			Random r = new Random(output);
			
			int love = r.nextInt(100);
			
			EmbedBuilder e = EmbedUtils.newBuilder();
			if (love > 70) {
				e.addField("The love between " + m1.getEffectiveName() + " and " + m2.getEffectiveName() + " is:", ":heart: " + String.valueOf(love) + " :heart:", false);	
			} else {
				e.addField("The love between " + m1.getEffectiveName() + " and " + m2.getEffectiveName() + " is:", ":broken_heart: " + String.valueOf(love) + " :broken_heart:", false);
			}
			
			sendMessage(channel, e.build());
			
		} else {
			sendMessage(channel, sender.getAsMention() + " you need to mention 2 users.");
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return true;
	}

}


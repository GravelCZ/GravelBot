package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer.QAHandler.QuestionAnswerPair;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class QuestionCommand implements IServerCommand {

	private QAHandler handler;
	
	public QuestionCommand(QAHandler handler) {
		this.handler = handler;
	}

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		if (handler.isUserAnswering(sender)) {
			sendMessage(channel, "You have already asked " + sender.getAsMention());
			return;
		}
		QuestionAnswerPair pair = handler.getRandomQuestion();
		String question = pair.getQuestion();
		String url = "";
		
		Pattern pattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&\\/\\/=]*)");
		Matcher m = pattern.matcher(question);
		if (m.find()) {
			url = m.group();
			Logger.debug(url);
		}
		
		handler.setUserQA(sender, pair);
		
		EmbedBuilder b = new EmbedBuilder();
		b.setColor(new Color(0, 255, 0));
		b.setTitle("Question/Answer");
		if (!url.isEmpty()) {
			b.setImage(url);	
		}
		b.addField("Question is:", question, false);
		b.setFooter("Use: !answer [answer] to answer this question.");
		sendMessage(channel, b.build());
	}

	@Override
	public boolean canExecute(Member user) {
		return true;
	}
}

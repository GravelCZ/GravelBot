package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer.QAHandler.QuestionAnswerPair;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class QuestionCommand implements ICommand {

	private QAHandler handler;
	
	public QuestionCommand(QAHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (handler.isUserAnswering(sender)) {
			sendMessage(channel, "You have already asked " + sender.mention());
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
		b.withColor(0, 255, 0);
		b.withTitle("Question/Answer");
		if (!url.isEmpty()) {
			b.withImage(url);	
		}
		b.appendField("Question is:", question, false);
		b.withFooterText("Use: !answer [answer] to answer this question.");
		sendMessage(channel, b.build());
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}
}

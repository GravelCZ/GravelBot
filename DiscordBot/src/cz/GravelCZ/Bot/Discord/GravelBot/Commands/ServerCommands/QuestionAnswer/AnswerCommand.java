package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer;

import java.util.HashMap;
import java.util.Optional;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class AnswerCommand implements IServerCommand {
	
	private QAHandler handler;
	
	private HashMap<String, String> answers = new HashMap<>();
	
	public AnswerCommand(QAHandler handler) {
		this.handler = handler;
	}

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		if (!handler.isUserAnswering(sender)) {
			sendMessage(channel, "You did not ask for a question " + sender.getAsMention());
			return;
		}
		if (args.length == 0) {
			sendMessage(channel, "You need to say the answer dummy.");
			return;
		}
		String rightAnswer = "";
		String[] a = handler.getAnswers(sender);
		for (int i = 0; i < a.length; i++) {
			if (rightAnswer.isEmpty()) {
				rightAnswer += a[i];
			} else {
				rightAnswer += " " + a[i];
			}
		}
		answers.put(sender.getId(), rightAnswer);
		String answer = "";
		for (int i = 0; i < args.length; i++) {
			if (answer.isEmpty()) {
				answer += args[i];
			} else {
				answer += " " + args[i];
			}
		}
		Optional<Boolean> isAnswerRight = handler.isAnswerRight(sender, answer);
		if (isAnswerRight.isPresent()) {
			if (isAnswerRight.get().booleanValue()) {
				channel.sendMessage("You got it right! " + sender.getAsMention()).queue();
				channel.sendMessage("You can now ask again " + sender.getAsMention()).queue();
			} else {
				try {
					String answera = new String(answers.get(sender.getId()));
					answers.remove(sender.getId());
					channel.sendMessage("You got it wrong! " + sender.getAsMention() + "\n"
							+ "The possible correct answers were: " + answera + "\n"
							+ "You can now ask again.").queue();	
				} catch (Exception e) {}
			}
		}
		handler.removeUser(sender);
	}

	@Override
	public boolean canExecute(Member user) {
		return false;
	}
}

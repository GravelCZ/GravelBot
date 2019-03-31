package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer;

import java.util.HashMap;
import java.util.Optional;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

public class AnswerCommand implements ICommand {
	
	private QAHandler handler;
	
	private HashMap<String, String> answers = new HashMap<>();
	
	public AnswerCommand(QAHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (!handler.isUserAnswering(sender)) {
			sendMessage(channel, "You did not ask for a question " + sender.mention());
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
		answers.put(sender.getStringID(), rightAnswer);
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
				RequestBuffer.request(() -> {
					channel.sendMessage("You got it right! " + sender.mention());
					channel.sendMessage("You can now ask again " + sender.mention());
				});
			} else {
				try {
					RequestBuffer.request(() -> {
						String answera = new String(answers.get(sender.getStringID()));
						answers.remove(sender.getStringID());
						channel.sendMessage("You got it wrong! " + sender.mention());
						channel.sendMessage("The possible correct answers were: " + answera);
						channel.sendMessage("You can now ask again " + sender.mention());
					});	
				} catch (Exception e) {}
			}
		}
		handler.removeUser(sender);
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}
}

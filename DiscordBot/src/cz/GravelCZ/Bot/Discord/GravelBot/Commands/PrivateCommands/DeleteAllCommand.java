package cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IPrivateCommand;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageHistory.MessageRetrieveAction;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
public class DeleteAllCommand implements IPrivateCommand {

	@Override
	public void execute(Message msg, PrivateChannel channel, User user, String content, String[] args) {
		Logger.log(user.getName() + " nechce být pervert :D");
		MessageRetrieveAction historyAction = MessageHistory.getHistoryFromBeginning(channel);
		MessageHistory history = historyAction.complete();
		if (!history.isEmpty()) {
			history.getRetrievedHistory().stream().filter(msgh -> msgh.getAuthor().getIdLong() == channel.getJDA().getSelfUser().getIdLong()).forEach(msgh -> msgh.delete().queue());
		}
	}

	@Override
	public boolean canExecute(User user) {
		return false;
	}
	
}

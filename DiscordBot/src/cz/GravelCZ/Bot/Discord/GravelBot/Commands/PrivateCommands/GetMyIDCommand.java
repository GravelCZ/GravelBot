package cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IPrivateCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class GetMyIDCommand implements IPrivateCommand {

	@Override
	public void execute(Message msg, PrivateChannel channel, User user, String content, String[] args) {
		channel.sendMessage("Your id: " + user.getId()).queue();
	}

	@Override		
	public boolean canExecute(User user) {
		return true;
	}


}

package cz.GravelCZ.Bot.Discord.GravelBot.Commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public interface IPrivateCommand {
	
	public void execute(Message msg, PrivateChannel channel, User user, String content, String[] args);
	
	public boolean canExecute(User user);
	
	default void sendMessage(PrivateChannel c, String text) {
		c.sendMessage(text).queue();
	}
	
	
	default void sendMessage(PrivateChannel channel, MessageEmbed build)
	{
		channel.sendMessage(build).queue();
	}
}

package cz.GravelCZ.Bot.Discord.GravelBot.Commands;

import java.io.File;
import java.util.Optional;

import cz.GravelCZ.Bot.Main.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public interface IServerCommand {

	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args);

	public boolean canExecute(Member user);

	default boolean bypassCMDChannel() {
		return false;
	}

	default Optional<TextChannel> getBotSpamChannel(Guild g) {
		return g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
	}

	default void sendMessage(TextChannel c, String text) {
		c.sendMessage(text).queue();
	}

	default void sendMessage(PrivateChannel c, String text) {
		c.sendMessage(text).queue();
	}

	default void sendMessage(PrivateChannel c, MessageEmbed build) {
		c.sendMessage(build).queue();
	}

	default void sendMessage(TextChannel c, MessageEmbed e) {
		c.sendMessage(e).queue();
	}

	default void sendFile(TextChannel c, File f) {
		c.sendFile(f).queue();
	}

	default void sendFile(TextChannel c, File f, String text) {
		c.sendMessage(text).queue();
		c.sendFile(f).queue();
	}

}

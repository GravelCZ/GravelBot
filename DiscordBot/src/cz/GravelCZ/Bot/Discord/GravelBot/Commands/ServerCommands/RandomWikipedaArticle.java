package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands;

import cz.GravelCZ.Bot.APIs.Wikipedia.WikipediaRandomArticleAPI;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class RandomWikipedaArticle implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		String randomArticle = WikipediaRandomArticleAPI.getRandomArticleUrl();
		Logger.log("User: " + sender.getUser().getName() + " searched for a random wikipedia article: " + randomArticle);
		sendMessage(channel, "Here is a random article from wikipedia: " + randomArticle);	
	}

	@Override
	public boolean canExecute(Member user) {
		return true;
	}

}

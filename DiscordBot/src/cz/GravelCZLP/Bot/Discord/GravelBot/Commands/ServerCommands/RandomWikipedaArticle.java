package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import cz.GravelCZLP.Bot.APIs.Wikipedia.WikipediaRandomArticleAPI;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class RandomWikipedaArticle implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		String randomArticle = WikipediaRandomArticleAPI.getRandomArticleUrl();
		Logger.log("User: " + sender.getName() + " searched for a random wikipedia article: " + randomArticle);
		sendMessage(channel, "Here is a random article from wikipedia: " + randomArticle);
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}

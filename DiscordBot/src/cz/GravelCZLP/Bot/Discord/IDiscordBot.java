package cz.GravelCZLP.Bot.Discord;

import cz.GravelCZLP.Bot.Main.Main;

public interface IDiscordBot {

	public void init(Main m);
	
	public void shutdown();
	
}

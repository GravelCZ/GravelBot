package cz.GravelCZ.Bot.Discord;

import cz.GravelCZ.Bot.Main.Main;

public interface IDiscordBot {

	public void init(Main m);
	
	public void shutdown();
	
}

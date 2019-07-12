package cz.GravelCZLP.Bot.Discord.GravelBot.Events;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IGuild;

public class YoutubeVideoEndEvent extends Event {
	
	private IGuild g;
	
	public YoutubeVideoEndEvent(IGuild g) {
		this.g = g;
	}

	public IGuild getGuild() {
		return g;
	}

}

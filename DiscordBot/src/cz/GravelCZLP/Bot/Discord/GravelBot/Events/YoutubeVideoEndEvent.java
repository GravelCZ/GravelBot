package cz.GravelCZLP.Bot.Discord.GravelBot.Events;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IGuild;

public class YoutubeVideoEndEvent extends Event {
	
	private IGuild g;
	private int code;
	
	public YoutubeVideoEndEvent(IGuild g, int code) {
		this.g = g;
		this.code = code;
	}

	public IGuild getGuild() {
		return g;
	}

	public int getCode() {
		return code;
	}

}

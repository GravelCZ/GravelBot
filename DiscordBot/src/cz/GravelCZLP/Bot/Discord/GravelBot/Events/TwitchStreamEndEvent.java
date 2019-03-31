package cz.GravelCZLP.Bot.Discord.GravelBot.Events;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IGuild;

public class TwitchStreamEndEvent extends Event {

	private IGuild g;
	private int code;
	
	public TwitchStreamEndEvent(IGuild g, int code) {
		this.g = g;
		this.code = code;
	}
	
	public int getReturnCode() {
		return code;
	}
	
	public IGuild getGuild() {
		return g;
	}
	
}

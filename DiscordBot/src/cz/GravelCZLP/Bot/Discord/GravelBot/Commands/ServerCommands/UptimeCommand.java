package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Main.Main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class UptimeCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		int uptime = (int) TimeUnit.MILLISECONDS.toSeconds(Main.getUptime());
		int day = (int)TimeUnit.SECONDS.toDays(uptime);        
		long hours = TimeUnit.SECONDS.toHours(uptime) - (day *24);
		long minute = TimeUnit.SECONDS.toMinutes(uptime) - (TimeUnit.SECONDS.toHours(uptime)* 60);
		long second = TimeUnit.SECONDS.toSeconds(uptime) - (TimeUnit.SECONDS.toMinutes(uptime) *60);
		StringBuilder sb = new StringBuilder();
		sb.append("Uptime: **");
		if (day > 0) {
			sb.append(day + " days, ");
		}
		if (hours > 0) {
			sb.append(hours + " hours, ");
		}
		sb.append(minute + " minutes, and ");
		sb.append(second + " seconds.");
		sb.append("**");
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z" +")");
		sb.append(" (since: " + f.format(new Date(Main.getStartTime())));
		sendMessage(channel, sb.toString());
	}
	
	
	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}
}

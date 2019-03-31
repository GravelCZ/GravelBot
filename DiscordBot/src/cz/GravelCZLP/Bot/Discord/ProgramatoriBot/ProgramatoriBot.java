package cz.GravelCZLP.Bot.Discord.ProgramatoriBot;

import java.io.File;
import java.io.IOException;

import cz.GravelCZLP.Bot.Discord.IDiscordBot;
import cz.GravelCZLP.Bot.Discord.ProgramatoriBot.Listeners.ProgListener;
import cz.GravelCZLP.Bot.Main.Constants;
import cz.GravelCZLP.Bot.Main.Main;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

public class ProgramatoriBot implements IDiscordBot {

	private String roleChannel = null;
	
	@Override
	public void init(Main m) {
		File f = new File("./BotDataFolder/programmingBot.txt");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			String[] lines = Utils.getLinesFromFile(f);
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].startsWith("cfg")) {
					String[] split = lines[i].split(";");
					if (split[1].equalsIgnoreCase("roleChannel")) {
						roleChannel = split[2];
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ClientBuilder cb = new ClientBuilder();
		
		cb.withToken(Constants.programatoriBotKey);
		cb.withRecommendedShardCount(true);
		
		IDiscordClient c = cb.build();
		
		c.getDispatcher().registerListener(this);
		c.getDispatcher().registerListener(new ProgListener(this));
		
		c.login();
	}

	@Override
	public void shutdown() {
		File f = new File("./BotDataFolder/programmingBot.txt");
		Utils.writeToFile(f, "cfg;roleChannel;" + roleChannel);
	}

	@EventSubscriber
	public void onReady(ReadyEvent e) {
		e.getClient().changePresence(StatusType.ONLINE, ActivityType.LISTENING, " your role requests.");
	}
	
	public void setRoleChannel(String s) {
		roleChannel = s;
	}
	
	public String getChannelNameAndID() {
		return roleChannel;
	}

	public String getVersion() {
		return "BETA 1.0.3";
	}

}

package cz.GravelCZ.Bot.Discord.ProgramatoriBot;

import java.io.File;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import cz.GravelCZ.Bot.Discord.IDiscordBot;
import cz.GravelCZ.Bot.Discord.ProgramatoriBot.Listeners.ProgListener;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Main.Main;
import cz.GravelCZ.Bot.Utils.Utils;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class ProgramatoriBot implements IDiscordBot {

	private String roleChannel = null;
	
	private JDA jda;
	
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
		
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(Constants.programatoriBotKey);
		builder.addEventListeners(new ProgListener(this));
		
		
		try {
			jda = builder.build();
			jda.awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		File f = new File("./BotDataFolder/programmingBot.txt");
		Utils.writeToFile(f, "cfg;roleChannel;" + roleChannel);
		jda.shutdown();
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

package cz.GravelCZLP.Bot.Discord.GravelBot.Listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.handle.obj.IGuild;

public class ShitpostHandler {

	private static List<String> blockedGuilds = new ArrayList<>();
	
	public static boolean isBlocked(IGuild g) {
		return blockedGuilds.contains(g.getName() + ":" + g.getStringID());
	}
	
	public static void unblock(IGuild g) {
		blockedGuilds.remove(g.getName() + ":" + g.getStringID());
	}
	
	public static void block(IGuild g) {
		blockedGuilds.add(g.getName() + ":" + g.getStringID());
	}
	
	public static void load() throws Exception {
		File f = new File("./BotDataFolder/blockedGuilds.txt");
		if (!f.exists()) {
			f.createNewFile();
		}
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		int loaded = 0;
		while ((line = br.readLine()) != null) {
			blockedGuilds.add(line);
			loaded++;
		}
		br.close();
		Logger.log("Shitpost blocked guilds loaded: " + loaded);
	}
	
	public static void save() throws Exception {
		File f = new File("./BotDataFolder/blockedGuilds.txt");
		if (!f.exists()) {
			f.createNewFile();
		}
		FileWriter fw = new FileWriter(f, false);
		for (String s : blockedGuilds) {
			fw.write(s + "\n");
		}
		fw.close();
		Logger.log("Saved shitpost blocked Guilds.");
	}
}

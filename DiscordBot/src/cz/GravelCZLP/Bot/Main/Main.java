package cz.GravelCZLP.Bot.Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import cz.GravelCZLP.Bot.Discord.IDiscordBot;
import cz.GravelCZLP.Bot.Discord.GravelBot.GravelBot;
import cz.GravelCZLP.Bot.Discord.ProgramatoriBot.ProgramatoriBot;
import cz.GravelCZLP.Bot.Utils.DatabaseManager;
import cz.GravelCZLP.Bot.Utils.Logger;

public class Main {

	private static DatabaseManager dbman;
	
	private IDiscordBot gravelBot, progBot;

	private static long start = 0;
	
	public static void main(String[] args) {
		start = System.currentTimeMillis();
		new Main();
	}	
	
	public Main() {
		try {
			Logger.hook();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		dbman = new DatabaseManager();
		dbman.init("192.168.1.7", 3306, Constants.mysqlUsername, Constants.mysqlPassword, "bot");
		
		Logger.log("Loading Discord Bot.");
		
		startDiscordBot();
		startProgramatoriBot();
		
		Logger.log("Loaded Discord Bot!");
		
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				while (true) {
					try {
						String line = br.readLine();
						if (line != null && !line.isEmpty()) {
							if (line.equalsIgnoreCase("stop")) {
								System.exit(0);
							}
						}
					} catch (IOException e) {}
				}
			}

		});
		t.setName("System input thread");
		t.start();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				gravelBot.shutdown();
				progBot.shutdown();
			}

		}));
	}

	public static DatabaseManager getDBManager() {
		return dbman;
	}
	
	private void startProgramatoriBot() {
		progBot = new ProgramatoriBot();
		progBot.init(this);
	}

	private void startDiscordBot() {
		gravelBot = new GravelBot();
		gravelBot.init(this);
	}
	
	public static long getStartTime() {
		return start;
	}
	
	public static long getUptime() {
		return System.currentTimeMillis() - start;
	}

}

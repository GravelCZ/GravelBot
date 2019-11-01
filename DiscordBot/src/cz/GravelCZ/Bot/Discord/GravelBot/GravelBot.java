package cz.GravelCZ.Bot.Discord.GravelBot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import cz.GravelCZ.Bot.Discord.IDiscordBot;
import cz.GravelCZ.Bot.Discord.GravelBot.Listeners.ChatCommandListener;
import cz.GravelCZ.Bot.Discord.GravelBot.Listeners.GuildStuffListener;
import cz.GravelCZ.Bot.Discord.GravelBot.Listeners.ShitpostHandler;
import cz.GravelCZ.Bot.Discord.GravelBot.Runnables.DateCheckerRunnable;
import cz.GravelCZ.Bot.Discord.GravelBot.Runnables.RedditShitpostRunnable;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Main.Main;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class GravelBot implements IDiscordBot {
	
	private JDA jda;
	
	@Override
	public void init(Main m) {
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(Constants.DiscordAPIKey);
		
		try {
			builder.addEventListeners(new ChatCommandListener(), new GuildStuffListener());
			jda = builder.build();
			jda.awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		
		Runnable dateCheckerRunnable = new DateCheckerRunnable(jda);
		Runnable redditShitpostRunnable = new RedditShitpostRunnable(jda);
		
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(4);
		
		exec.scheduleAtFixedRate(dateCheckerRunnable, 0, 1, TimeUnit.SECONDS);
		exec.scheduleAtFixedRate(redditShitpostRunnable, 2, 2, TimeUnit.HOURS);
	}

	@Override
	public void shutdown() {
		try {
			ShitpostHandler.save();
			jda.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

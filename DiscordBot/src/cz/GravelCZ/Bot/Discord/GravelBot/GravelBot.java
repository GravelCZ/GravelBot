package cz.GravelCZ.Bot.Discord.GravelBot;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import cz.GravelCZ.Bot.Discord.IDiscordBot;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.HttpAudioProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Listeners.ChatCommandListener;
import cz.GravelCZ.Bot.Discord.GravelBot.Listeners.GuildStuffListener;
import cz.GravelCZ.Bot.Discord.GravelBot.Listeners.ShitpostHandler;
import cz.GravelCZ.Bot.Discord.GravelBot.Runnables.DateCheckerRunnable;
import cz.GravelCZ.Bot.Discord.GravelBot.Runnables.RedditShitpostRunnable;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class GravelBot extends ListenerAdapter implements IDiscordBot {
	
	@Override
	public void init(Main m) {
		JDABuilder builder = new JDABuilder();
		builder.setToken(Constants.DiscordAPIKey);
		
		JDA jda = null;
		try {
			jda = builder.build();
			jda.awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		
		jda.addEventListener(new ChatCommandListener(), new GuildStuffListener(), this);
		

		Runnable dateCheckerRunnable = new DateCheckerRunnable(jda);
		Runnable redditShitpostRunnable = new RedditShitpostRunnable(jda);
		
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(4);
		
		exec.scheduleAtFixedRate(dateCheckerRunnable, 0, 1, TimeUnit.SECONDS);
		exec.scheduleAtFixedRate(redditShitpostRunnable, 2, 2, TimeUnit.HOURS);
	}

	@Override
	public void onReady(ReadyEvent event) {
		List<Guild> guilds = event.getJDA().getGuilds();
		event.getJDA().getPresence().setActivity(Activity.playing("music from Gravel on " + event.getGuildAvailableCount() + " guilds!"));
		
		for (Guild g : guilds)
		{
			GAudioProcessor gau = new GAudioProcessor();
			AudioManager am = g.getAudioManager();
			am.setAutoReconnect(true);
			am.setSendingHandler(gau);
			am.setReceivingHandler(gau);
			
			if (am.getConnectedChannel() != null)
			{
				continue;
			}
			
			List<VoiceChannel> musicChannels = g.getVoiceChannelsByName("Music", false);
			List<VoiceChannel> vc = musicChannels.stream()
				.filter(v -> v.getPermissionOverride(g.getSelfMember()).getAllowed().stream().anyMatch(p -> p == Permission.VOICE_CONNECT))
				.filter(v -> v.getPermissionOverride(g.getSelfMember()).getAllowed().stream().anyMatch(p -> p == Permission.VOICE_SPEAK))
				.collect(Collectors.toList());
			Optional<VoiceChannel> v = vc.stream().findFirst();
			if (v.isPresent()) {
				try {
					((GAudioProcessor) am.getSendingHandler()).setAudioProvider(new HttpAudioProvider(new URL(Constants.local_audio)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				am.openAudioConnection(v.get());
				
			}
		}
	}

	@Override
	public void shutdown() {
		try {
			ShitpostHandler.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

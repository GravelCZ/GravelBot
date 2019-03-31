package cz.GravelCZLP.Bot.Discord.GravelBot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cz.GravelCZLP.Bot.Discord.IDiscordBot;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.HttpAudioProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Listeners.ChatCommandListener;
import cz.GravelCZLP.Bot.Discord.GravelBot.Listeners.GuildStuffListener;
import cz.GravelCZLP.Bot.Discord.GravelBot.Listeners.ShitpostHandler;
import cz.GravelCZLP.Bot.Discord.GravelBot.Timers.DateCheckerRunnable;
import cz.GravelCZLP.Bot.Discord.GravelBot.Timers.PewDiePieVsTGayRunnable;
import cz.GravelCZLP.Bot.Discord.GravelBot.Timers.RedditShitpostRunnable;
import cz.GravelCZLP.Bot.Main.Constants;
import cz.GravelCZLP.Bot.Main.Main;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.RequestBuffer;

public class GravelBot implements IDiscordBot {
	
	@Override
	public void init(Main m) {
		ClientBuilder cb = new ClientBuilder();
		
		cb.withToken(Constants.DiscordAPIKey);
		cb.withRecommendedShardCount();
		
		IDiscordClient client = cb.build();
		
		client.getDispatcher().registerListener(new ChatCommandListener());
		client.getDispatcher().registerListener(new GuildStuffListener());
		client.getDispatcher().registerListener(this);
		
		client.login();

		Runnable dateCheckerRunnable = new DateCheckerRunnable(client);
		Runnable redditShitpostRunnable = new RedditShitpostRunnable(client);
		Runnable PewDiePieVsTGayRunnable = new PewDiePieVsTGayRunnable(client);
		
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(4);
		
		exec.scheduleAtFixedRate(dateCheckerRunnable, 0, 1, TimeUnit.SECONDS);
		exec.scheduleAtFixedRate(redditShitpostRunnable, 2, 2, TimeUnit.HOURS);
		exec.scheduleAtFixedRate(PewDiePieVsTGayRunnable, 60, 1, TimeUnit.SECONDS);
	}

	@EventSubscriber
	public void onReady(ReadyEvent e) {
		List<IGuild> guilds = e.getClient().getGuilds();
		
		e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, "music from Gravel on " + guilds.size() + " guilds!");
		
		for (IGuild g : guilds) {
			g.getAudioManager().setAudioProcessor(new GAudioProcessor());
			
			if (g.getConnectedVoiceChannel() != null) {
				continue;
			}
			List<IVoiceChannel> musicChannels = new ArrayList<>(g.getVoiceChannelsByName("Music"));
			boolean successfull = false;
			musicChannels = musicChannels.stream()
					.filter(vc -> vc.getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.VOICE_CONNECT))
					.filter(vc -> vc.getModifiedPermissions(e.getClient().getOurUser()).contains(Permissions.VOICE_SPEAK))
					.collect(Collectors.toList());
			Optional<IVoiceChannel> svc = musicChannels.stream().findFirst();
			if (svc.isPresent()) {
				IVoiceChannel vc = svc.get();
				if (!vc.getConnectedUsers().isEmpty()) {
					RequestBuffer.request(() -> {
						vc.join();
					});
					successfull = true;	
				}
			}
			if (successfull) {
				try {
					g.getAudioManager().setAudioProvider(new HttpAudioProvider(new URL("http://192.168.1.7:8080/")));
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}	
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

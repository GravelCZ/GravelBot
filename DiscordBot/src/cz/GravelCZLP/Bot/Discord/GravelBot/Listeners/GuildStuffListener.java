package cz.GravelCZLP.Bot.Discord.GravelBot.Listeners;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.HttpAudioProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Events.TwitchStreamEndEvent;
import cz.GravelCZLP.Bot.Discord.GravelBot.Events.YoutubeVideoEndEvent;
import cz.GravelCZLP.Bot.Main.Constants;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.voice.VoiceDisconnectedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.VoiceDisconnectedEvent.Reason;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IPresence;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.RequestBuffer;

public class GuildStuffListener {
	
	@EventSubscriber
	public void onUserChangePer(PresenceUpdateEvent e) {
		IPresence np = e.getNewPresence();
		IPresence op = e.getOldPresence();
		if (np.getStatus() != op.getStatus()) {
			StringBuffer sb = new StringBuffer();
			if (np.getActivity().isPresent()) {
				String suffix = "";
				if (np.getActivity().get() == ActivityType.LISTENING) {
					suffix = " to ";
				}
				boolean append = true;
				if (np.getActivity().get() == ActivityType.PLAYING) {
					if (!np.getText().isPresent() || np.getText().get() == "") {
						append = false;
					}
				}
				if (append) {
					sb.append(np.getActivity().get().name() + suffix);
				}
			}
			if (np.getText().isPresent()) {
				sb.append(" " + np.getText().get());
			}
			if (np.getStreamingUrl().isPresent()) {
				sb.append(" at: " + np.getActivity().get());
			}
			Logger.log("User: " + e.getUser().getName() + " chaned status to: " + np.getStatus().name() + " " + sb.toString());
		}
	}
	
	@EventSubscriber
	public void onPermissionsChange(RoleUpdateEvent e) {
		IGuild guild = e.getGuild();
		IRole newRole = e.getRole();
		IRole oldRole = e.getOldRole();
		IUser we = e.getClient().getOurUser();
		if (!oldRole.getPermissions().contains(Permissions.BAN) && newRole.getPermissions().contains(Permissions.BAN)) {
			if (we.hasRole(newRole)) {
				List<IChannel> txtChannels = guild.getChannelsByName(Constants.textChatCommands);
				if (txtChannels == null || txtChannels.size() == 0) {
					return;
				}
				RequestBuffer.request(() -> {
					txtChannels.get(0).sendMessage("I have obtained all the infinity stones.");
				});
			}
		}
	}

	@EventSubscriber
	public void onUserVCSwitch(UserVoiceChannelMoveEvent e) {
		IVoiceChannel oldVC = e.getOldChannel();
		IVoiceChannel newVC = e.getNewChannel();
		IGuild g = e.getGuild();
		if (oldVC.getName().equals("Music")) {
			if ((oldVC.getConnectedUsers().size() - 1) == 0) {
				if (g.getConnectedVoiceChannel() != null) {
					RequestBuffer.request(() -> {
						oldVC.leave();
					});
					((IPlayerProvider) g.getAudioManager().getAudioProvider()).close();
				}
			}
		}
		if (newVC.getName().equals("Music")) {
			if ((newVC.getConnectedUsers().size() - 1) == 0) {
				IAudioManager manager = g.getAudioManager();
				try {
					GAudioProcessor p = (GAudioProcessor) manager.getAudioProcessor();
					p.getAudioProcessor().setVolume(0.05d);
					manager.setAudioProvider(new HttpAudioProvider(new URL("http://10.0.0.7:8080/")));
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				RequestBuffer.request(() -> {
					newVC.join();
				});
			}
		}
		if (g.getConnectedVoiceChannel() != null) {
			IVoiceChannel vc = g.getConnectedVoiceChannel();
			if ((vc.getConnectedUsers().size() - 1) == 0) {
				RequestBuffer.request(() -> {
					vc.leave();
				});
				((IPlayerProvider) g.getAudioManager().getAudioProvider()).close();
			}
		}
	}
	
	@EventSubscriber
	public void onUserVCLeave(UserVoiceChannelLeaveEvent e) {
		if (e.getGuild().getConnectedVoiceChannel() != null) {
			IVoiceChannel vc = e.getVoiceChannel();
			if (vc.getName().equals("Music")) {
				if ((vc.getConnectedUsers().size() - 1) == 0) {
					RequestBuffer.request(() -> {
						vc.leave();
					});
					((IPlayerProvider) e.getGuild().getAudioManager().getAudioProvider()).close();
				}
			}
		}
	}
	
	@EventSubscriber
	public void onUserVCJoin(UserVoiceChannelJoinEvent e) {
		if (e.getGuild().getConnectedVoiceChannel() == null) {
			if (e.getVoiceChannel().getName().equals("Music")) {
				IAudioManager manager = e.getGuild().getAudioManager();
				try {
					GAudioProcessor p = (GAudioProcessor) manager.getAudioProcessor();
					p.getAudioProcessor().setVolume(0.05d);
					manager.setAudioProvider(new HttpAudioProvider(new URL("http://10.0.0.7:8080/")));
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				RequestBuffer.request(() -> {
					e.getVoiceChannel().join();
				});
			}
		}
	}
	
	@EventSubscriber
	public void onTwitchStreamEnd(TwitchStreamEndEvent e) {
		try {
			IAudioManager mng = e.getGuild().getAudioManager();
			GAudioProcessor p = (GAudioProcessor) mng.getAudioProcessor();
			p.getAudioProcessor().setVolume(0.05);
			mng.setAudioProvider(new HttpAudioProvider(new URL("http://10.0.0.7:8080/")));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}

	@EventSubscriber
	public void onYoutubeVideoEnd(YoutubeVideoEndEvent e) {
		try {
			IAudioManager mng = e.getGuild().getAudioManager();
			GAudioProcessor p = (GAudioProcessor) mng.getAudioProcessor();
			p.getAudioProcessor().setVolume(0.05);
			mng.setAudioProvider(new HttpAudioProvider(new URL("http://10.0.0.7:8080/")));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}
	
	@EventSubscriber
	public void onGuildCreate(GuildCreateEvent e) {
		IAudioManager mng = e.getGuild().getAudioManager();
		if (!(mng.getAudioProcessor() instanceof GAudioProcessor)) {
			mng.setAudioProcessor(new GAudioProcessor());
		}
		e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, "music from Gravel on " + e.getClient().getGuilds().size() + " guilds!");
	}
	
	@EventSubscriber
	public void onGuildLeave(GuildLeaveEvent e) {
		Logger.log("Left guild: " + e.getGuild().getName() + "(" + e.getGuild().getStringID() + ")");
		e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, "music from Gravel on " + e.getClient().getGuilds().size() + " guilds!");
	}
	
	@EventSubscriber
	public void onDisconnect(VoiceDisconnectedEvent e) {
		Reason reason = e.getReason();
		if (reason == Reason.ABNORMAL_CLOSE) {
			Logger.error("Got disconnected from guild: " + e.getGuild().getName() + " Reason: " + reason.name());
			Runnable r = new Runnable() {

				@Override
				public void run() {
					if (e.getVoiceChannel() != null) {
						RequestBuffer.request(() -> {
							e.getVoiceChannel().leave();
						});
					}
					List<IVoiceChannel> channels = e.getGuild().getVoiceChannelsByName("Music");
					if (channels == null || channels.isEmpty()) {
						return;
					}
					RequestBuffer.request(() -> {
						channels.get(0).join();
					});
				}
			};
			Executors.newSingleThreadScheduledExecutor().schedule(r, 120, TimeUnit.SECONDS);
		}
	}

}

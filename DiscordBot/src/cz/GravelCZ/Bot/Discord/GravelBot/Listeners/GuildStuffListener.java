package cz.GravelCZ.Bot.Discord.GravelBot.Listeners;

import java.util.Optional;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class GuildStuffListener extends ListenerAdapter {
	
	@Override
	public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {
		boolean flag3 = event.getGuild().getSelfMember().getRoles().contains(event.getRole());
		
		if (flag3)
		{
			boolean flag1 = event.getNewPermissions().stream().anyMatch(p -> p == Permission.BAN_MEMBERS);
			boolean flag2 = event.getOldPermissions().stream().noneMatch(p -> p == Permission.BAN_MEMBERS);
			if (flag1 && flag2) {
				Optional<TextChannel> tc = event.getGuild().getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
				if (tc.isPresent()) {
					tc.get().sendMessage("I have obtained all the infinity stones. https://tenor.com/view/thanos-infinity-war-avengers-gif-14003260").queue();
				}
			}
		}
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		event.getJDA().getPresence().setActivity(Activity.playing("music from Gravel on " + event.getJDA().getGuilds().size() + " guilds!"));
		GAudioProcessor gau = new GAudioProcessor();
		AudioManager am = event.getGuild().getAudioManager();
		am.setAutoReconnect(true);
		am.setSendingHandler(gau);
		am.setReceivingHandler(gau);
	}
	
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		Logger.log("Left guild: " + event.getGuild().getName() + " (" + event.getGuild().getId() + ")"); 
	}
	
	
	
//	@EventSubscriber
//	public void onGuildCreate(GuildCreateEvent e) {
//		IAudioManager mng = e.getGuild().getAudioManager();
//		if (!(mng.getAudioProcessor() instanceof GAudioProcessor)) {
//			mng.setAudioProcessor(new GAudioProcessor());
//		}
//		e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, "music from Gravel on " + e.getClient().getGuilds().size() + " guilds!");
//	}
//	
//	@EventSubscriber
//	public void onGuildLeave(GuildLeaveEvent e) {
//		Logger.log("Left guild: " + e.getGuild().getName() + "(" + e.getGuild().getStringID() + ")");
//		e.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, "music from Gravel on " + e.getClient().getGuilds().size() + " guilds!");
//	}
	
//
//	@EventSubscriber
//	public void onUserVCSwitch(UserVoiceChannelMoveEvent e) {
//		IVoiceChannel oldVC = e.getOldChannel();
//		IVoiceChannel newVC = e.getNewChannel();
//		IGuild g = e.getGuild();
//		if (oldVC.getName().equals("Music")) {
//			if ((oldVC.getConnectedUsers().size() - 1) == 0) {
//				if (g.getConnectedVoiceChannel() != null) {
//					RequestBuffer.request(() -> {
//						oldVC.leave();
//					});
//					((IPlayerProvider) g.getAudioManager().getAudioProvider()).close();
//				}
//			}
//		}
//		if (newVC.getName().equals("Music")) {
//			if ((newVC.getConnectedUsers().size() - 1) == 0) {
//				IAudioManager manager = g.getAudioManager();
//				try {
//					GAudioProcessor p = (GAudioProcessor) manager.getAudioProcessor();
//					p.getAudioProcessor().setVolume(0.05d);
//					manager.setAudioProvider(new HttpAudioProvider(new URL("http://10.0.0.7:8080/")));
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				RequestBuffer.request(() -> {
//					newVC.join();
//				});
//			}
//		}
//		if (g.getConnectedVoiceChannel() != null) {
//			IVoiceChannel vc = g.getConnectedVoiceChannel();
//			if ((vc.getConnectedUsers().size() - 1) == 0) {
//				RequestBuffer.request(() -> {
//					vc.leave();
//				});
//				((IPlayerProvider) g.getAudioManager().getAudioProvider()).close();
//			}
//		}
//	}
//	
//	@EventSubscriber
//	public void onUserVCLeave(UserVoiceChannelLeaveEvent e) {
//		if (e.getGuild().getConnectedVoiceChannel() != null) {
//			IVoiceChannel vc = e.getVoiceChannel();
//			if (vc.getName().equals("Music")) {
//				if ((vc.getConnectedUsers().size() - 1) == 0) {
//					RequestBuffer.request(() -> {
//						vc.leave();
//					});
//					((IPlayerProvider) e.getGuild().getAudioManager().getAudioProvider()).close();
//				}
//			}
//		}
//	}
//	
//	@EventSubscriber
//	public void onUserVCJoin(UserVoiceChannelJoinEvent e) {
//		if (e.getGuild().getConnectedVoiceChannel() == null) {
//			if (e.getVoiceChannel().getName().equals("Music")) {
//				IAudioManager manager = e.getGuild().getAudioManager();
//				try {
//					GAudioProcessor p = (GAudioProcessor) manager.getAudioProcessor();
//					p.getAudioProcessor().setVolume(0.05d);
//					manager.setAudioProvider(new HttpAudioProvider(new URL("http://10.0.0.7:8080/")));
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				RequestBuffer.request(() -> {
//					e.getVoiceChannel().join();
//				});
//			}
//		}
//	}
//	
//	@EventSubscriber
//	public void onTwitchStreamEnd(TwitchStreamEndEvent e) {
//		try {
//			IAudioManager mng = e.getGuild().getAudioManager();
//			GAudioProcessor p = (GAudioProcessor) mng.getAudioProcessor();
//			p.getAudioProcessor().setVolume(0.05);
//			mng.setAudioProvider(new HttpAudioProvider(new URL("http://10.0.0.7:8080/")));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//	}
//
//	@EventSubscriber
//	public void onYoutubeVideoEnd(YoutubeVideoEndEvent e) {
//		try {
//			IAudioManager mng = e.getGuild().getAudioManager();
//			GAudioProcessor p = (GAudioProcessor) mng.getAudioProcessor();
//			p.getAudioProcessor().setVolume(0.05);
//			mng.setAudioProvider(new HttpAudioProvider(new URL("http://10.0.0.7:8080/")));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//	}
//	
//	@EventSubscriber
//	public void onDisconnect(VoiceDisconnectedEvent e) {
//		Reason reason = e.getReason();
//		if (reason == Reason.ABNORMAL_CLOSE) {
//			Logger.error("Got disconnected from guild: " + e.getGuild().getName() + " Reason: " + reason.name());
//			Runnable r = new Runnable() {
//
//				@Override
//				public void run() {
//					if (e.getVoiceChannel() != null) {
//						RequestBuffer.request(() -> {
//							e.getVoiceChannel().leave();
//						});
//					}
//					List<IVoiceChannel> channels = e.getGuild().getVoiceChannelsByName("Music");
//					if (channels == null || channels.isEmpty()) {
//						return;
//					}
//					RequestBuffer.request(() -> {
//						channels.get(0).join();
//					});
//				}
//			};
//			Executors.newSingleThreadScheduledExecutor().schedule(r, 120, TimeUnit.SECONDS);
//		}
//	}

}

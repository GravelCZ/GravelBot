package cz.GravelCZ.Bot.Discord.GravelBot.Listeners;

import java.net.URL;
import java.util.Optional;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.HttpAudioProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IAudioProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
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
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		VoiceChannel vcj = event.getChannelJoined();
		VoiceChannel vcl = event.getChannelLeft();
		Guild g = event.getGuild();
		AudioManager am = g.getAudioManager();
		if (vcl.getName().equals("Music") 
				&& am.isConnected() 
				&& am.getConnectedChannel().getName().equals("Music")) {
			if (vcl.getMembers().size() <= 1) {
				am.closeAudioConnection();
				AudioSendHandler handler = am.getSendingHandler();
				if (handler instanceof GAudioProcessor) {
					GAudioProcessor processor = (GAudioProcessor) handler;
					IAudioProvider provider = processor.getAudioProvider();
					if (provider instanceof IPlayerProvider) {
						((IPlayerProvider) provider).close();
					}
				}
				return;
			}
		}
		if (vcj.getName().equals("Music") && !am.isConnected())
		{
			am.setAutoReconnect(true);
			GAudioProcessor gau = new GAudioProcessor();
			am.setSendingHandler(gau);
			am.setReceivingHandler(gau);
			am.openAudioConnection(vcj);
			try {
				gau.setAudioProvider(new HttpAudioProvider(new URL(Constants.local_audio)));
			}catch (Exception e) {
				e.printStackTrace();
				Optional<TextChannel> tc = g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
				if (tc.isPresent()) {
					tc.get().sendMessage("Exception occured while trying to join to Music channel: " + e.getClass().getName() + ": " + e.getMessage()).queue();
				}
			}
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) 
	{
		VoiceChannel vc = event.getChannelLeft();
		Guild g = event.getGuild();
		AudioManager am = g.getAudioManager();
		if (vc.getName().equals("Music") 
				&& am.isConnected() 
				&& am.getConnectedChannel().getName().equals("Music")) {
			if (vc.getMembers().size() <= 1) {
				am.closeAudioConnection();
				AudioSendHandler handler = am.getSendingHandler();
				if (handler instanceof GAudioProcessor) {
					GAudioProcessor processor = (GAudioProcessor) handler;
					IAudioProvider provider = processor.getAudioProvider();
					if (provider instanceof IPlayerProvider) {
						((IPlayerProvider) provider).close();
					}
				}
			}
		}
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) 
	{
		VoiceChannel vc = event.getChannelJoined();
		Guild g = event.getGuild();
		AudioManager am = g.getAudioManager();
		if (vc.getName().equals("Music") && !am.isConnected())
		{
			am.setAutoReconnect(true);
			GAudioProcessor gau = new GAudioProcessor();
			am.setSendingHandler(gau);
			am.setReceivingHandler(gau);
			am.openAudioConnection(vc);
			try {
				gau.setAudioProvider(new HttpAudioProvider(new URL(Constants.local_audio)));
			}catch (Exception e) {
				e.printStackTrace();
				Optional<TextChannel> tc = g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
				if (tc.isPresent()) {
					tc.get().sendMessage("Exception occured while trying to join to Music channel: " + e.getClass().getName() + ": " + e.getMessage()).queue();
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
	
	@Override
	public void onReady(ReadyEvent event) {
		event.getJDA().getPresence().setActivity(Activity.playing("music from Gravel on " + event.getGuildAvailableCount() + " guilds!"));
	}

}

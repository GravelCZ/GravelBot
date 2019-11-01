package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.HttpAudioProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZ.Bot.Main.Constants;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		AudioManager am = g.getAudioManager();
		GAudioProcessor processor = (GAudioProcessor) am.getSendingHandler();
		if (processor != null) {
			if (processor.getAudioProvider() instanceof IPlayerProvider) {
				((IPlayerProvider) processor.getAudioProvider()).close();
			}
		} else {
			processor = new GAudioProcessor();
			am.setReceivingHandler(processor);
			am.setSendingHandler(processor);
			am.setAutoReconnect(true);
		}
		
		
		if (!am.isConnected()) {
			if (!sender.getVoiceState().inVoiceChannel()) {
				sendMessage(channel, "You are not in a voice channel.");
				return;
			} else {
				VoiceChannel voice = sender.getVoiceState().getChannel();
				g.getAudioManager().openAudioConnection(voice);
				EnumSet<Permission> p = g.getSelfMember().getPermissions(voice);
				if (p.contains(Permission.VOICE_CONNECT) && p.contains(Permission.VOICE_SPEAK)) {
					sendMessage(channel, ":white_check_mark: Connected to: **" + voice.getName() + "**.");	
				} else {
					sendMessage(channel, ":x: I don't have permissions to join.");	
				}
			}
		}
	
		if (args.length == 0) {
			try {
				processor.setAudioProvider(new HttpAudioProvider(new URL(Constants.local_audio)));
			} catch (Exception e1) {
				e1.printStackTrace();
				channel.sendMessage(e1.getClass().getName() + ": "+ e1.getMessage());
			}
		} else {
			String address = args[0];
			URL url = null;
			try {
				url = new URL(address);
			} catch (MalformedURLException e) {
				sendMessage(channel, "Not a valid URL: " + e.getMessage());
			}
			if (url == null) {
				return;
			}
			try {
				processor.setAudioProvider(new HttpAudioProvider(url));
			} catch (Exception e) {
				sendMessage(channel, e.getClass().getName() + ": " + e.getMessage());
				e.printStackTrace();
				return;
			}
			sendMessage(channel, "Now playing: " + address);
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, false, true);
	}
 
}

package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import java.awt.Color;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.UnsupportedAudioFileException;

import cz.GravelCZ.Bot.APIs.YoutubeAPI.YoutubeAPI;
import cz.GravelCZ.Bot.APIs.YoutubeAPI.YoutubeAPI.Info;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.YoutubeAudioProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZ.Bot.Utils.EmbedUtils;
import cz.GravelCZ.Bot.Utils.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class YoutubeCommand implements IServerCommand {

	private final Pattern urlPattern = Pattern.compile("https:\\/\\/www\\.youtube\\.com\\/watch\\?v=([a-zA-Z0-9-_]{11})", Pattern.MULTILINE);
	private final Pattern playlistPattern = Pattern.compile("https:\\/\\/www\\.youtube\\.com\\/playlist\\?list=([a-zA-Z0-9-_]{34})", Pattern.MULTILINE);

	
	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		if (args.length == 0) {
			sendMessage(channel, "Too few arguments, use **!/youtube help** for help");
			return;
		}
		if (args[0].equalsIgnoreCase("help")) {
			EmbedBuilder b = EmbedUtils.newBuilder();
			b.setColor(new Color(255, 0, 0));
			b.setTitle("Help for command: YouTube");
			b.addField("help", "Prints this help", false);
			b.addField("play [url or search]", "This will join your voice channel and play the video you submitted if it exists", false);
			sendMessage(channel, b.build());
			return;
		}
		if (args[0].equalsIgnoreCase("play")) {
			if (args.length <= 1) {
				sendMessage(channel, "Too few arguments, use **!/youtube help** for help");
				return;
			}
			if (!g.getAudioManager().isConnected()) {
				if (sender.getVoiceState().inVoiceChannel()) {
					sendMessage(channel, "You are not in a voice channel.");
					return;
				} else {
					VoiceChannel voice = sender.getVoiceState().getChannel();
					
					EnumSet<Permission> p = g.getSelfMember().getPermissions(voice);
					if (p.contains(Permission.VOICE_CONNECT) && p.contains(Permission.VOICE_SPEAK)) {
						sendMessage(channel, ":white_check_mark: Connected to: **" + voice.getName() + "**.");	
					} else {
						sendMessage(channel, ":x: I don't have permissions to join.");	
					}
				}
			}
			String url = "";
			String id = "";
			
			boolean playlist = false;
			
			Matcher mat = urlPattern.matcher(args[1]);
			Matcher mat2 = playlistPattern.matcher(args[1]);
			
			if (mat.find()) { // is a url
				url = mat.group();
				id = mat.group(1);
			} else if (mat2.find()) { // playlist
				url = mat2.group();
				id = mat2.group(1);
				playlist = true;
			} else {
				String query = "";
				for (int i = 1; i < args.length; i++) {
					query += args[i] + " ";
				}
				try {
					Pair<Optional<String>, Integer> resp = YoutubeAPI.search(query);
					if (!resp.getKey().isPresent()) {
						sendMessage(channel, "Got response code: " + resp.getValue());
						return;
					} else {
						url = "https://www.youtube.com/watch?v=" + resp.getKey().get();
						id = resp.getKey().get();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				GAudioProcessor processor = (GAudioProcessor) g.getAudioManager().getSendingHandler();
				
				if (processor.getAudioProvider() instanceof IPlayerProvider) {
					((IPlayerProvider) processor.getAudioProvider()).close();
				}
				processor.setAudioProvider(new YoutubeAudioProvider(url, g));
			} catch (IOException | UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			}
			
			Info info = null;
			
			try {
				if (playlist) {
					info = YoutubeAPI.getPlaylistInfo(id);
				} else {
					info = YoutubeAPI.getVideoInfo(id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			EmbedBuilder b = EmbedUtils.newBuilder();
			b.setColor(new Color(255, 0, 0));
			b.setDescription("YouTube");
			b.addField(info.channelName, info.title, false);
			b.setImage(info.thumbnail);
			sendMessage(channel, b.build());
			return;
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, false, true);
	}

}

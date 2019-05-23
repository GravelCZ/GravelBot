package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.tuple.Pair;

import cz.GravelCZLP.Bot.APIs.YoutubeAPI.YoutubeAPI;
import cz.GravelCZLP.Bot.APIs.YoutubeAPI.YoutubeAPI.Info;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.YoutubeAudioProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;

public class YoutubeCommand implements ICommand {

	private final Pattern urlPattern = Pattern.compile("https:\\/\\/www\\.youtube\\.com\\/watch\\?v=([a-zA-Z0-9-_]{11})", Pattern.MULTILINE);
	private final Pattern playlistPattern = Pattern.compile("https:\\/\\/www\\.youtube\\.com\\/playlist\\?list=([a-zA-Z0-9-_]{34})", Pattern.MULTILINE);
	
	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length == 0) {
			sendMessage(channel, "Too few arguments, use **!/youtube help** for help");
			return;
		}
		if (args[0].equalsIgnoreCase("help")) {
			EmbedBuilder b = new EmbedBuilder();
			b.withColor(255, 0, 0);
			b.withTitle("Help for command");
			b.withAuthorName("Help for command: Youtube");
			b.appendField("help", "Prints this help", false);
			b.appendField("play [url or search]", "This will join your voice channel and play the video you submitted if it exists", false);
			b.withFooterText("GravelCZLP - Author | Bot writren in Java | API is Discord4J");
			b.withFooterIcon("https://i.imgur.com/MraElzj.png");
			sendMessage(channel, b.build());
			return;
		}
		if (args[0].equalsIgnoreCase("play")) {
			if (args.length <= 1) {
				sendMessage(channel, "Too few arguments, use **!/youtube help** for help");
				return;
			}
			if (guild.getClient().getOurUser().getVoiceStateForGuild(guild) == null) {
				if (sender.getVoiceStateForGuild(guild) == null) {
					sendMessage(channel, "You are not in a voice channel.");
					return;
				} else {
					IVoiceChannel voice = sender.getVoiceStateForGuild(guild).getChannel();
					requestVoid(() -> {
						voice.join();
					});
					sendMessage(channel, ":white_check_mark: Connected to: **" + voice.getName() + "**.");
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
			} else if (mat2.find()) {
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
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				if (guild.getAudioManager().getAudioProvider() instanceof IPlayerProvider) {
					((IPlayerProvider) guild.getAudioManager().getAudioProvider()).close();
				}
				guild.getAudioManager().setAudioProvider(new YoutubeAudioProvider(url, guild));
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
			EmbedBuilder b = new EmbedBuilder();
			b.withColor(255, 0, 0);
			b.withDesc("YouTube");
			b.appendField(info.channelName, info.title, false);
			b.withImage(info.thumbnail);
			b.withFooterText("GravelCZLP - Author | Bot writren in Java | API is Discord4J");
			b.withFooterIcon("https://i.imgur.com/MraElzj.png");
			sendMessage(channel, b.build());
			return;
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, false, true);
	}

}

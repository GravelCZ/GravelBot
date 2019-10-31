package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import java.awt.Color;
import java.util.EnumSet;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZ.Bot.APIs.Twitch.TwitchAPI;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.TwitchAudioProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZ.Bot.Utils.EmbedUtils;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class TwitchCommand implements IServerCommand {
	
	public TwitchCommand() {
		TwitchAPI.auth();
	}

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		if (args.length == 0) {
			sendMessage(channel, "Too few arguments, use **!/twitch help** for help");
			return;
		}
		if (args[0].equalsIgnoreCase("help")) {
			EmbedBuilder b = EmbedUtils.newBuilder();
			b.setTitle("Help for command: twitch");
			b.addField("help", "Prints this help", false);
			b.addField("play [channel name]", "Will join your voice channel and play the stream of the user if they are streaming", false);
			sendMessage(channel, b.build());
			return;
		}
		if (args[0].equalsIgnoreCase("play")) {
			if (args.length <= 1) {
				sendMessage(channel, "Too few arguments. Use: **!/twitch play [channel name]**");
				return;
			}
			if (!g.getAudioManager().isConnected()) {
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
			
			String name = args[1];
			String userInfo = TwitchAPI.getUserInfo(name);
			JSONObject userInfoObj = new JSONObject(userInfo);
			JSONArray dataArray = userInfoObj.getJSONArray("data");
			if (dataArray.length() == 0) { // user does not exist
				sendMessage(channel, "That user does not exist.");
				return;
			}
			String streamInfo = TwitchAPI.getUserStream(name);
			JSONObject streamInfoObj = new JSONObject(streamInfo);
			if (streamInfoObj.isNull("stream")) {//user is offline or does not exist, in this case, the user is offline because we check it up there ^
				sendMessage(channel, "User is offline");
				return;
			}
			if (g.getAudioManager().isConnected()) {
				if (sender.getVoiceState().inVoiceChannel()) {
					sendMessage(channel, "You are not in a voice channel");
					return;
				}
				VoiceChannel voice = sender.getVoiceState().getChannel();
				EnumSet<Permission> p = g.getSelfMember().getPermissions(voice);
				if (p.contains(Permission.VOICE_CONNECT) && p.contains(Permission.VOICE_SPEAK)) {
					sendMessage(channel, ":white_check_mark: Connected to: **" + voice.getName() + "**.");	
				} else {
					sendMessage(channel, ":x: I don't have permissions to join.");	
				}
			}
			try {
				GAudioProcessor processor = (GAudioProcessor) g.getAudioManager().getSendingHandler();
				if (processor.getAudioProvider() instanceof IPlayerProvider) {
					((IPlayerProvider) processor.getAudioProvider()).close();
				}
				processor.setAudioProvider(new TwitchAudioProvider(name, g));
			} catch (Exception e) {
				sendMessage(channel, "Internal error occured: " + e.getClass().getName() + ": " + e.getMessage());
				e.printStackTrace();
			}
			GAudioProcessor p = (GAudioProcessor) g.getAudioManager().getSendingHandler();
			p.getAudioProcessor().setVolume(0.5d);
			
			JSONObject userStreamDataobj = streamInfoObj.getJSONObject("stream");
			
			Logger.log(userStreamDataobj.toString());
			
			JSONObject channelObj = userStreamDataobj.getJSONObject("channel");
			JSONObject previewObj = userStreamDataobj.getJSONObject("preview");
			EmbedBuilder b = EmbedUtils.newBuilder(); 
			b.setColor(new Color(75, 54, 124));
			
			b.setTitle(channelObj.getString("display_name"), channelObj.getString("url"));
			b.setDescription(channelObj.getString("status"));
			
			b.setAuthor(channelObj.getString("display_name"), channelObj.getString("url"), channelObj.getString("logo"));
			
			b.setThumbnail(channelObj.getString("logo"));
			
			if (channelObj.getString("game") != null && !channelObj.getString("game").isEmpty()) {
				b.addField(":video_game: Game:", channelObj.getString("game"), true);
			}
			b.addField(":eyes: Viewers:", String.valueOf(userStreamDataobj.getInt("viewers")), true);
			
			if(channelObj.getBoolean("mature")) {
				b.addField(":underage: Mature", "This stream is marked as mature", false);
			}
			
			b.setImage(previewObj.getString("large"));
			
			sendMessage(channel, b.build());
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, false, true);
	}

}

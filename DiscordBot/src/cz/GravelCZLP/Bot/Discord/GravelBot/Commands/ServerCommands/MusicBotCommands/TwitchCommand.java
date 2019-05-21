package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZLP.Bot.APIs.Twitch.TwitchAPI;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.TwitchAudioProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class TwitchCommand implements ICommand {
	
	public TwitchCommand() {
		TwitchAPI.auth();
	}
	
	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length == 0) {
			sendMessage(channel, "Too few arguments, use **!/twitch help** for help");
			return;
		}
		if (args[0].equalsIgnoreCase("help")) {
			EmbedBuilder b = new EmbedBuilder();
			b.withColor(0, 255, 0);
			b.withTitle("Help for command");
			b.withAuthorName("Help for command: Twitch");
			b.appendField("help", "Prints this help", false);
			b.appendField("play [channel name]", "Will join your voice channel and play the stream of the user if they are streaming", false);
			b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J");
			b.withFooterIcon("https://i.imgur.com/MraElzj.png");
			sendMessage(channel, b.build());
		}
		if (args[0].equalsIgnoreCase("play")) {
			if (args.length <= 1) {
				sendMessage(channel, "Too few arguments. Use: **!/twitch play [channel name]**");
				return;
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
			if (guild.getConnectedVoiceChannel() == null) {
				if (sender.getVoiceStates().size() == 0) {
					sendMessage(channel, "You are not in a voice channel");
					return;
				}
				IVoiceChannel voice = sender.getVoiceStates().get(guild.getLongID()).getChannel();
				if (voice == null) {
					sendMessage(channel, "You are not in a voice channel -_-");
					return;
				}
				try {
					RequestBuffer.request(() -> {
						voice.join();
					});
					sendMessage(channel, "Connected to: **" + voice.getName() + "**.");
				} catch (MissingPermissionsException e1) {
					sendMessage(channel, "I dont have permissions. **" + e1.getMessage() + "**");
				}
			}
			try {
				guild.getAudioManager().setAudioProvider(new TwitchAudioProvider(name, guild));
			} catch (Exception e) {
				sendMessage(channel, "Internal error occured: " + e.getClass().getName() + ": " + e.getMessage());
				e.printStackTrace();
			}
			GAudioProcessor p = (GAudioProcessor) guild.getAudioManager().getAudioProcessor();
			p.getAudioProcessor().setVolume(0.5d);
			
			JSONObject userStreamDataobj = streamInfoObj.getJSONObject("stream");
			
			Logger.log(userStreamDataobj.toString());
			
			JSONObject channelObj = userStreamDataobj.getJSONObject("channel");
			JSONObject previewObj = userStreamDataobj.getJSONObject("preview");
			EmbedBuilder b = new EmbedBuilder(); 
			b.withColor(75, 54, 124);
			
			b.withTitle(channelObj.getString("display_name"));
			b.withDescription(channelObj.getString("status"));
			b.withUrl(channelObj.getString("url"));
			
			b.withAuthorName(channelObj.getString("display_name"));
			b.withAuthorIcon(channelObj.getString("logo"));
			b.withAuthorUrl(channelObj.getString("url"));
			
			b.withThumbnail(channelObj.getString("logo"));
			
			if (channelObj.getString("game") != null && !channelObj.getString("game").isEmpty()) {
				b.appendField(":video_game: Game:", channelObj.getString("game"), true);
			}
			b.appendField(":eyes: Viewers:", String.valueOf(userStreamDataobj.getInt("viewers")), true);
			
			if(channelObj.getBoolean("mature")) {
				b.appendField(":underage: Mature", "This stream is marked as mature", false);
			}
			
			b.withImage(previewObj.getString("large"));
			
			b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J");
			b.withFooterIcon("https://i.imgur.com/MraElzj.png");
			
			sendMessage(channel, b.build());
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, false, true);
	}

}

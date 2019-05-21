package cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import cz.GravelCZLP.Bot.Discord.GravelBot.Events.TwitchStreamEndEvent;
import cz.GravelCZLP.Bot.Main.Constants;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.RequestBuffer;

public class TwitchAudioProvider implements IAudioProvider, IPlayerProvider {

	private AudioInputStream audioStream;
	
	private boolean closed = false;
	
	private Process ffmpeg;
	private Thread ffmpegThead;
	
	public TwitchAudioProvider(String twitchName, IGuild g) throws Exception {
		twitchName = twitchName.toLowerCase();
		
		HashMap<String, String> headers = new HashMap<>();
		
		headers.put("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0");
		
		Pair<String, Integer> out = Utils.makeUrlGetRequest(new URL("https://api.twitch.tv/api/channels/" + twitchName + "/access_token?client_id=" + Constants.twitchId), headers, false);
		
		if (out.getValue() != 200) {
			List<IChannel> channels = g.getChannelsByName(Constants.textChatCommands);
			if (channels == null || channels.isEmpty()) {
				return;
			}
			channels.get(0).sendMessage("Something went wrong with comunicationg with Twitch API. Check the logs for more information.");
			return;
		}
		 
		Logger.debug("Twitch Reply: " + out.getKey());
		
		JSONObject obj = new JSONObject(out);
		
		String token = obj.getString("token");
		String sig = obj.getString("sig");
		
		String infoUrl = "https://usher.ttvnw.net/api/channel/hls/" + twitchName + "?client_id=s16augpdjkvaj79cp6b25pflx7p867&token=" + URLEncoder.encode(token, "UTF-8") + "&sig=" + sig + "&allow_source&allow_audio_only";
		
		Pair<String, Integer> out2 = Utils.makeUrlGetRequest(new URL(infoUrl), headers, true);
		
		if (out2.getValue() != 200) {
			List<IChannel> channels = g.getChannelsByName(Constants.textChatCommands);
			if (channels == null || channels.isEmpty()) {
				return;
			}
			channels.get(0).sendMessage("Something went wrong with comunicationg with Twitch API. Check the logs for more information.");
			return;
		}
		
		String[] split = out2.getKey().split("\\r?\\n");
		String format = split[0];
		
		Logger.debug("Format: " + format);
		
		String audioOnly = "";
		
		for (int i = 1; i < split.length; i += 3) {
			String f = split[i - 1];
			if (f.contains("audio_only")) {
				Logger.debug("audio only: " + split[i]);
				audioOnly = split[i];
			}
		}
		ffmpeg = Runtime.getRuntime().exec("/usr/bin/ffmpeg -threads 4 -i " + audioOnly + " -f mp3 pipe:1");
		
		Logger.log("Starting FFMPEG.");
		
		Runnable ffmpegOut = new Runnable() {
			
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()));
					String line;
					while ((line = br.readLine()) != null) {
						Logger.debug("[FFMPEG]: " + line);
					}	
					int code = ffmpeg.waitFor();
					Logger.log("FFMPEG exit code: " + code);
					List<IChannel> channels = g.getChannelsByName(Constants.textChatCommands);
					if (channels == null || channels.isEmpty()) {
						return;
					}
					if (code == 0) {
						RequestBuffer.request(() -> {
							channels.get(0).sendMessage("The stream ended.");
						});
					} else {
						RequestBuffer.request(() -> {
							channels.get(0).sendMessage("FFMPEG returned code: " + code + " most likely an internal issue, try submitting the twitch stream again, if that does not work, contact Bot Admin.");
						});
					}
					g.getClient().getDispatcher().dispatch(new TwitchStreamEndEvent(g, code));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		ffmpegThead = new Thread(ffmpegOut);
		ffmpegThead.setName("FFMPEG output thread");
		ffmpegThead.start();
		
		AudioInputStream originalStream = AudioSystem.getAudioInputStream(ffmpeg.getInputStream());
		
		AudioFormat baseFormat = originalStream.getFormat();
		
		AudioFormat toPCM = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
				baseFormat.getSampleSizeInBits() != -1 ? baseFormat.getSampleSizeInBits() : 16,
				baseFormat.getChannels(),
				baseFormat.getFrameSize() != -1 ? baseFormat.getFrameSize() : 2 * baseFormat.getChannels(),
				baseFormat.getFrameRate() != -1 ? baseFormat.getFrameRate() : baseFormat.getSampleRate(),
				baseFormat.isBigEndian());
		
		AudioInputStream pcmStream = AudioSystem.getAudioInputStream(toPCM, originalStream);
		
		AudioFormat audioFormat = new AudioFormat(toPCM.getEncoding(), 48000, //48000 = Opus Frame Rate
				toPCM.getSampleSizeInBits(), toPCM.getChannels(), toPCM.getFrameSize(), toPCM.getFrameRate(), true);
		
		audioStream = AudioSystem.getAudioInputStream(audioFormat, pcmStream);
	}
	
	@Override
	public boolean isReady() {
		try {
			return audioStream.available() > -1 && !closed;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public byte[] provide() {// 960 = OPUS FRAME SIZE
		byte[] audio = new byte[960 * audioStream.getFormat().getFrameSize()];

		try {
			int amountRead = audioStream.read(audio, 0, audio.length);
			
			if (amountRead > 1) {
				return audio;
			} else {
				closed = true;
				audioStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new byte[0];
	}

	@Override
	public void close() {
		closed = true;
		try {
			audioStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ffmpegThead.interrupt();
		ffmpeg.destroy();
	}
}

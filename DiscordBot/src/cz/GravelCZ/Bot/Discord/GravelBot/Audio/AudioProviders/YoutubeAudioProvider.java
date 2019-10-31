package cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.entities.Guild;

public class YoutubeAudioProvider implements IPlayerProvider, IAudioProvider {

	private AudioInputStream audioStream;
	
	private boolean closed = false;
	
	private Process ffmpeg, youtubedl;
	private Thread ytToFfmpeg, ffmpegLogT; 
	
	private Guild g;
	
	public YoutubeAudioProvider(String url, Guild g) throws IOException, UnsupportedAudioFileException {
		Logger.log("URL To Request: " + url);
		this.g = g;
		youtubedl = Runtime.getRuntime().exec("youtube-dl -f 251 " + url + " -o -");
		InputStream ytLogIn = youtubedl.getErrorStream();
		InputStream ytData = youtubedl.getInputStream();
		
		Runnable yrdlcheck = new Runnable() {
			
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(ytLogIn));
					String line;
					while ((line = br.readLine()) != null) {
						Logger.log("[YT-DL]: " + line);
					}
					Logger.log("youtube-dl: exit code: " + youtubedl.waitFor());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread yrdlcheckT = new Thread(yrdlcheck);
		yrdlcheckT.start();
		
		ffmpeg = Runtime.getRuntime().exec("/usr/bin/ffmpeg -threads 1 -i pipe:0 -f mp3 pipe:1");
		InputStream ffmpegLog = ffmpeg.getErrorStream();
		InputStream ffmpegData = ffmpeg.getInputStream();
		OutputStream os = ffmpeg.getOutputStream();
		
		Runnable ffmpegLogR = new Runnable() {
			
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(ffmpegLog));
					String line;
					while ((line = br.readLine()) != null) {
						Logger.log("[FFMPEG]: " + line);
					}
					int code = ffmpeg.waitFor();
					Logger.log("FFMPEG exit code: " + code);
				} catch (Exception e) {}
			}
		};
		ffmpegLogT = new Thread(ffmpegLogR);
		ffmpegLogT.start();
		
		ytToFfmpeg = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					byte[] arr = new byte[Short.MAX_VALUE];
					int len;
					while ((len = ytData.read(arr)) != -1) {
						os.write(arr, 0, len);
					}
					os.flush();
					os.close();
					Logger.debug("[YT-DL to FFMPEG Thread]: Stopped");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		ytToFfmpeg.start();
		
		AudioInputStream originalStream = AudioSystem.getAudioInputStream(ffmpegData);
		
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
	public byte[] provide() {
		byte[] audio = new byte[960 * audioStream.getFormat().getFrameSize()];
		
		try {
			int amountRead = audioStream.read(audio, 0, audio.length);
			
			if (amountRead != -1) {
				return audio;
			} else {
				close();
				((GAudioProcessor) g.getAudioManager().getSendingHandler()).setAudioProvider(new HttpAudioProvider(new URL(Constants.local_audio)));
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
		
		ffmpegLogT.interrupt();
		
		ffmpeg.destroy();
	}

}

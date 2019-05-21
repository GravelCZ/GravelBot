package cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.lang3.tuple.Pair;

import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.audio.IAudioProvider;

public class HttpAudioProvider implements IAudioProvider, IPlayerProvider {

	private AudioInputStream audioStream;
	
	private boolean closed = false;

	public HttpAudioProvider(URL url) {
		AudioInputStream originalStream = null;
		
		try {
			Pair<InputStream, Integer> resp = Utils.getInputStreamFromURL(url, new HashMap<String, String>());
			if (resp.getValue() != 200) {
				return;
			}
			originalStream = AudioSystem.getAudioInputStream(resp.getKey());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
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
		
		audioStream = AudioSystem.getAudioInputStream(audioFormat, pcmStream); // this returns PCM format.
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
	}
	
}

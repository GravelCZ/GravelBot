package cz.GravelCZLP.Bot.Discord.GravelBot.Audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.jna.ptr.PointerByReference;

import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.SubProcessors.CompressionAudioSubProcessor;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.SubProcessors.IAudioSubProcessor;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.SubProcessors.LimiterAudioSubProcessor;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.SubProcessors.NormalisationAudioSubProcessor;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.SubProcessors.VolumeAudioProcessor;
import sx.blah.discord.api.internal.Opus;
import sx.blah.discord.handle.audio.AudioEncodingType;
import sx.blah.discord.handle.audio.IAudioProcessor;
import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.audio.impl.DefaultProvider;

public class GAudioProcessor implements IAudioProcessor {
	
	private IAudioProvider provider = new DefaultProvider();
	
	private ArrayList<IAudioSubProcessor> subProcessors = new ArrayList<>();
	
	private int volumeId = -1;
	
	private PointerByReference stereo, mono;
	
	public GAudioProcessor() {
		addSubProcessor(new CompressionAudioSubProcessor());
		addSubProcessor(new NormalisationAudioSubProcessor());
		addSubProcessor(new LimiterAudioSubProcessor());
		addSubProcessor(new VolumeAudioProcessor());
		
		//48000 = OPUS_SAMPLE_RATE, 2049 = OPUS_APPLICATION_AUDIO (native declaration : opus_defines.h), 2 = channels
		mono = Opus.INSTANCE.opus_encoder_create(48000, 1, 2049, IntBuffer.allocate(4));
		stereo = Opus.INSTANCE.opus_encoder_create(48000, 2, 2049, IntBuffer.allocate(4));
	}
	
	@Override
	public boolean isReady() {
		return provider.isReady();
	}

	@Override
	public byte[] provide() {
		byte[] pcmProvided = provider.provide();
		
		Iterator<IAudioSubProcessor> iter = subProcessors.iterator();
		while (iter.hasNext()) {
			IAudioSubProcessor sp = iter.next();
			pcmProvided = sp.process(pcmProvided);
		}
		
		ShortBuffer nonEncodedBuffer = ShortBuffer.allocate(pcmProvided.length / 2);
		ByteBuffer encodedBuffer = ByteBuffer.allocate(4096);
		
		for (int i = 0; i < pcmProvided.length; i += 2) {
			int firstByte = (0x000000FF & pcmProvided[i]);
			int secByte = (0x000000FF & pcmProvided[i + 1]);
			
			short combined = (short) ((firstByte << 8) | secByte);
			nonEncodedBuffer.put(combined);
		}
		
		nonEncodedBuffer.flip();
		
		int channels = provider.getChannels();
		
		int res = Opus.INSTANCE.opus_encode(channels == 1 ? mono : stereo, nonEncodedBuffer, 960, encodedBuffer, encodedBuffer.capacity()); //Native library encode. 960 = OPUS_FRAME_SIZE
		byte[] encoded = new byte[res];
		encodedBuffer.get(encoded);
		
		
		
		return encoded;
	}
	
	public VolumeAudioProcessor getAudioProcessor() {
		if (volumeId == -1) {
			return null;
		}
		return (VolumeAudioProcessor) subProcessors.get(volumeId);
	}
	
	public void addSubProcessor(IAudioSubProcessor sub) {
		subProcessors.add(sub);
		if (sub instanceof VolumeAudioProcessor) {
			volumeId = subProcessors.size() - 1;
		}
	}
	
	@Override
	public AudioEncodingType getAudioEncodingType() {
		return AudioEncodingType.OPUS;
	}
	
	@Override
	public boolean setProvider(IAudioProvider provider) {
		this.provider = provider;
		return true;
	}

}

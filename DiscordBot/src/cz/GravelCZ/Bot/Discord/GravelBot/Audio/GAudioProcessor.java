package cz.GravelCZ.Bot.Discord.GravelBot.Audio;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IAudioProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.SubProcessors.CompressionAudioSubProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.SubProcessors.IAudioSubProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.SubProcessors.LimiterAudioSubProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.SubProcessors.NormalisationAudioSubProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.SubProcessors.VolumeAudioProcessor;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public class GAudioProcessor implements AudioSendHandler, AudioReceiveHandler {
	
	private IAudioProvider provider = new DefaultProvider();
	
	private ArrayList<IAudioSubProcessor> subProcessors = new ArrayList<>();
	
	private int volumeId = -1;
	
	public GAudioProcessor() {
		addSubProcessor(new CompressionAudioSubProcessor());
		addSubProcessor(new NormalisationAudioSubProcessor());
		addSubProcessor(new LimiterAudioSubProcessor());
		addSubProcessor(new VolumeAudioProcessor());
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

	public void setAudioProvider(IAudioProvider provider)
	{
		this.provider = provider;
	}
	
	@Override
	public boolean canProvide() {
		return provider.isReady();
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		byte[] provided = provider.provide();
		
		Iterator<IAudioSubProcessor> iter = subProcessors.iterator();
		while (iter.hasNext())
		{
			IAudioSubProcessor sp = iter.next();
			provided = sp.process(provided);
		}
		
		return ByteBuffer.wrap(provided);
	}

	@Override
	public boolean isOpus() {
		return false;
	}

	public IAudioProvider getAudioProvider() {
		return provider;
	}
}

package cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders;

import sx.blah.discord.handle.audio.IAudioProvider;

public class SoundCloudAudioProvider implements IAudioProvider, IPlayerProvider {

	public SoundCloudAudioProvider() {
		
	}
	
	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public byte[] provide() {
		return null;
	}

	@Override
	public void close() {
		
	}
	
}

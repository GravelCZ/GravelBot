package cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders;

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

package cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders;

import sx.blah.discord.handle.audio.IAudioProvider;

public class YoutubeAudioProvider implements IPlayerProvider, IAudioProvider {

	public YoutubeAudioProvider(String url) {

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

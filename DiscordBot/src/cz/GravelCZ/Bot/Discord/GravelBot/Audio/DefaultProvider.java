package cz.GravelCZ.Bot.Discord.GravelBot.Audio;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IAudioProvider;

public class DefaultProvider implements IAudioProvider {

	@Override
	public byte[] provide() {
		return new byte[0];
	}

	@Override
	public boolean isReady() {
		return true;
	}

}

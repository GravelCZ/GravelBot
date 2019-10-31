package cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders;

public interface IAudioProvider {

	public byte[] provide();
	
	public boolean isReady();
	
}

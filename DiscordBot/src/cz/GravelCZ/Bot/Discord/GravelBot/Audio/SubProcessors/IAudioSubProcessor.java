package cz.GravelCZ.Bot.Discord.GravelBot.Audio.SubProcessors;

public interface IAudioSubProcessor {

	public byte[] process(byte[] inputFromPrevious);
	
}

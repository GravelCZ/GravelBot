package cz.GravelCZLP.Bot.Discord.GravelBot.Audio.SubProcessors;

public class LimiterAudioSubProcessor implements IAudioSubProcessor {

	@Override
	public byte[] process(byte[] inputFromPrevious) {
		return inputFromPrevious;
	}

}

package cz.GravelCZLP.Bot.Discord.GravelBot.Audio.SubProcessors;

public class NormalisationAudioSubProcessor implements IAudioSubProcessor {

	@Override
	public byte[] process(byte[] inputFromPrevious) {
		return inputFromPrevious;
	}

}

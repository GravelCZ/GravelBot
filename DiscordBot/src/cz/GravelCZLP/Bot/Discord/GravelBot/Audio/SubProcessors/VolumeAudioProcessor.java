package cz.GravelCZLP.Bot.Discord.GravelBot.Audio.SubProcessors;

public class VolumeAudioProcessor implements IAudioSubProcessor {

	//Volume
	private double volume = 0.1d;
	
	@Override
	public byte[] process(byte[] inputFromPrevious) {
		byte[] toReturn = new byte[inputFromPrevious.length];
		
		for (int i = 0; i < inputFromPrevious.length; i += 2) {
			short s = (short) (((inputFromPrevious[i] & 0xFF) << 8) | (inputFromPrevious[i + 1] & 0xFF));
			
			s = (short) (s * volume);
			
			toReturn[i] = (byte) ((s >> 8) & 0xFF);
			toReturn[i + 1] = (byte) (s & 0xFF);
		}
		return toReturn;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}
}

package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class VolumeCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length == 0) {
			sendMessage(channel, "You need to set the volume");
			return;
		}
		int numberInput = 0;
		try {
			numberInput = Integer.valueOf(args[0]);
		} catch (NumberFormatException e) {
			
		}
		if (numberInput > 105) {
			sendMessage(channel, "**FUCK OFF, Forget about THAT YOU ABSOLUTE PIECE OF SHIT!!!**");
			return;
		}
		double volume = (numberInput / 100.0D);
		((GAudioProcessor) guild.getAudioManager().getAudioProcessor()).getAudioProcessor().setVolume(volume);
		sendMessage(channel, "Volume set to: **" + numberInput + "%**");
		return;
	}
	
	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, false, true);
	}
}

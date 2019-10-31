package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class VolumeCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
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
		((GAudioProcessor) g.getAudioManager().getSendingHandler()).getAudioProcessor().setVolume(volume);
		sendMessage(channel, "Volume set to: **" + numberInput + "%**");
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, false, true);
	}
}

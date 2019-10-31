package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class StopCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		GAudioProcessor processor = (GAudioProcessor) g.getAudioManager().getSendingHandler();
		if (processor.getAudioProvider() instanceof IPlayerProvider) {
			((IPlayerProvider) processor.getAudioProvider()).close();
		}
		sendMessage(channel, "Stopped playing (Stream Closed)");
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, false, true);
	}
	
}

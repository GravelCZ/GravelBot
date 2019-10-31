package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class LeaveCommand implements IServerCommand {
	
	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		if (g.getAudioManager().getConnectedChannel() != null) {
			GAudioProcessor provider = (GAudioProcessor) g.getAudioManager().getSendingHandler();
			if (provider.getAudioProvider() instanceof IPlayerProvider) {
				((IPlayerProvider) provider.getAudioProvider()).close();
			}
			g.getAudioManager().closeAudioConnection();
			
			sendMessage(channel, "I left, i guess you dont like me ;(");
		} else {
			sendMessage(channel, "I am not in a voice channel... BAKA!!");
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, false, true);
	}
}

package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import cz.GravelCZ.Bot.Discord.GravelBot.Audio.GAudioProcessor;
import cz.GravelCZ.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class LeaveCommand implements IServerCommand {
	
	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		AudioManager am = g.getAudioManager();
		if (am.getConnectedChannel() != null) {
			GAudioProcessor provider = (GAudioProcessor) am.getSendingHandler();
			if (provider != null && provider.getAudioProvider() instanceof IPlayerProvider) {
				((IPlayerProvider) provider.getAudioProvider()).close();
			}
			am.closeAudioConnection();
			
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

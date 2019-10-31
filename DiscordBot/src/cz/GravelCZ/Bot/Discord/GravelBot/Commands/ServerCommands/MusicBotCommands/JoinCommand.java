package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import java.util.EnumSet;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;


public class JoinCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		if (!sender.getVoiceState().inVoiceChannel()) {
			sendMessage(channel, "You are not in a voice channel -_-");
		} else {
			VoiceChannel voice = sender.getVoiceState().getChannel();
			g.getAudioManager().openAudioConnection(voice);
			
			EnumSet<Permission> p = g.getSelfMember().getPermissions(voice);
			if (p.contains(Permission.VOICE_CONNECT) && p.contains(Permission.VOICE_SPEAK)) {
				sendMessage(channel, ":white_check_mark: Connected to: **" + voice.getName() + "**.");	
			} else {
				sendMessage(channel, ":x: I don't have permissions to join.");	
			}
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return PermissionsService.canExecute(user, false, true);
	}

}

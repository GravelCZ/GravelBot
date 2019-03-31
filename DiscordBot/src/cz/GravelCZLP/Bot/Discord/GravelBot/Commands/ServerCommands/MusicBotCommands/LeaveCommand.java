package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

public class LeaveCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (guild.getConnectedVoiceChannel() != null) {
			IAudioProvider provider = guild.getAudioManager().getAudioProvider();
			if (provider instanceof IPlayerProvider) {
				((IPlayerProvider) provider).close();
			}
			RequestBuffer.request(() -> {
				guild.getConnectedVoiceChannel().leave();
			});
			
			sendMessage(channel, "I left, i guess you dont like me ;(");
		} else {
			sendMessage(channel, "I am not in a voice channel... BAKA!!");
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, false, true);
	}
}

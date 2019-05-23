package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class JoinCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (sender.getVoiceStates().size() == 0) {
			sendMessage(channel, "You are not in a voice channel -_-");
		} else {
			IVoiceChannel voice = sender.getVoiceStateForGuild(guild).getChannel();
			if (voice == null) {
				sendMessage(channel, "You are not in a voice channel -_-");
				return;
			}
			try {
				RequestBuffer.request(() -> {
					try {
						voice.join();
						sendMessage(channel, ":white_check_mark: Connected to: **" + voice.getName() + "**.");
					} catch (MissingPermissionsException e) {
						sendMessage(channel, e.getMessage());
					}
				});
			} catch (MissingPermissionsException e1) {
				sendMessage(channel, "I dont have permissions. **" + e1.getClass().getName() + ": " + e1.getMessage() + "**");
			}
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, false, true);
	}

}

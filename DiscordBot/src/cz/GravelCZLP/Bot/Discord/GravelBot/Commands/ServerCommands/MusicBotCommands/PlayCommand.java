package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands;

import java.net.MalformedURLException;
import java.net.URL;

import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.HttpAudioProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Audio.AudioProviders.IPlayerProvider;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class PlayCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		IAudioProvider provider = guild.getAudioManager().getAudioProvider();
		if (provider != null) {
			if (provider instanceof IPlayerProvider) {
				((IPlayerProvider) provider).close();
			}
		}
		if (args.length == 0) {
			try {
				guild.getAudioManager().setAudioProvider(new HttpAudioProvider(new URL("http://192.168.1.7:8080/")));
			} catch (Exception e1) {
				channel.sendMessage(e1.getMessage());
				e1.printStackTrace();
			}
		} else {
			String address = args[0];
			URL url = null;
			try {
				url = new URL(address);
			} catch (MalformedURLException e) {
				sendMessage(channel, "Not a valid URL: " + e.getMessage());
			}
			if (url == null) {
				return;
			}
			guild.getAudioManager().setAudioProvider(new HttpAudioProvider(url));
			sendMessage(channel, "Now playing: " + address);
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, false, true);
	}
 
}

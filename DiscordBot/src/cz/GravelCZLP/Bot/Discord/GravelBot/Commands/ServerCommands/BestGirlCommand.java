package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.io.File;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class BestGirlCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		File[] bgf = new File("./BotDataFolder/BestGirl/").listFiles();
		File randomImage = bgf[Utils.getRandom().nextInt(bgf.length)];
		sendFile(channel, randomImage);
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}

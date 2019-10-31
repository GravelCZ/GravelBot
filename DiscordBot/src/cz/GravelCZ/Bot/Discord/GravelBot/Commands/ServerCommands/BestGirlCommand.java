package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.io.File;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class BestGirlCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		File[] bgf = new File("./BotDataFolder/BestGirl/").listFiles();
		File randomImage = bgf[Utils.getRandom().nextInt(bgf.length)];
		sendFile(channel, randomImage);
	}

	@Override
	public boolean canExecute(Member user) {
		return true;
	}

}

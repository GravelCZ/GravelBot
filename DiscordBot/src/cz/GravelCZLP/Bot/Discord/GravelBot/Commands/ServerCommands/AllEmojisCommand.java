package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.util.Collection;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class AllEmojisCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		Collection<Emoji> emojis = EmojiManager.getAll();
		String[] strs = new String[3];
		for (Emoji e : emojis) {
			String uc = " " + e.getUnicode();
			int len = uc.length();
			if (strs[0].length() + len >= 2000) {
				if (strs[1].length() + len >= 2000) {
					if (strs[2].length() + len >= 2000) {
						System.out.println("LOL.");
					} else {
						strs[2] += uc;
					}
				} else {
					strs[1] += uc;
				}
			} else {
				strs[0] += uc;
			}
		}
		if (!strs[0].isEmpty()) {

		}
		if (!strs[1].isEmpty()) {

		}
		if (!strs[2].isEmpty()) {

		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}

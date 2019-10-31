package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.util.EnumSet;
import java.util.Optional;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ReactOOFCommand implements IServerCommand {

	@Override
	public void execute(Message msg, TextChannel channel, Guild g, Member sender, String content, String[] args) {
		EnumSet<Permission> perms = g.getSelfMember().getPermissions();
		if (!perms.contains(Permission.MESSAGE_ADD_REACTION) || !perms.contains(Permission.MESSAGE_MANAGE)) {
			Optional<TextChannel> c = getBotSpamChannel(g);
			if (c.isPresent()) {
				sendMessage(c.get(), "Missing permissions: MESSAGE_ADD_REACTION, MESSAGE_MANAGE");
			} else {
				Logger.error(
						"Guild: " + g.getName() + ":" + g.getId() + " does not have a #bot-spam channel");
			}
			return;
		}
		if (args.length == 0) {
			Message tmsg = channel.sendMessage("This command will react :o2::regional_indicator_o::regional_indicator_f: to this message. "
					+ "Or set the message id as an argument and i will react to that message. You probably need dev. mode for that.").complete();
			tmsg.addReaction("U+1F1F4").complete();
			tmsg.addReaction("U+1F17E").complete();
			tmsg.addReaction("U+1F1EB").complete();
		} else if (args.length >= 1) {
			long i = 0;
			try {
				Long l = Long.parseLong(args[0]);
				i = l.longValue();
			} catch (Exception e) {
				sendMessage(channel, e.getClass().getName() + ": " + e.getMessage());
				return;
			}
			Message targetMsg = channel.retrieveMessageById(i).complete();
			if (targetMsg != null) {
				targetMsg.addReaction("U+1F1F4").complete();
				targetMsg.addReaction("U+1F17E").complete();
				targetMsg.addReaction("U+1F1EB").complete();
			} else {
				sendMessage(channel, "Did not find message with id: " + String.valueOf(i) + " in this channel.");
			}
		}
	}

	@Override
	public boolean canExecute(Member user) {
		return true;
	}

	@Override
	public boolean bypassCMDChannel() {
		return true;
	}
	
}

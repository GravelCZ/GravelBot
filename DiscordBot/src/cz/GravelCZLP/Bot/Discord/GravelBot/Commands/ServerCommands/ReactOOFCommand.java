package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.util.EnumSet;
import java.util.Optional;

import com.vdurmont.emoji.EmojiManager;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.RequestResponse;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuffer.IRequest;
import sx.blah.discord.util.RequestBuffer.RequestFuture;

public class ReactOOFCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		EnumSet<Permissions> perms = channel.getModifiedPermissions(guild.getClient().getOurUser());
		if (!perms.contains(Permissions.ADD_REACTIONS) || !perms.contains(Permissions.MANAGE_MESSAGES)) {
			Optional<IChannel> c = getBotSpamChannel(guild);
			if (c.isPresent()) {
				sendMessage(c.get(), "Missing permissions: MANAGE_MESSAGES, ADD_REACTIONS");
			} else {
				Logger.error(
						"Guild: " + guild.getName() + ":" + guild.getStringID() + " does not have a #bot-spam channel");
			}
			return;
		}
		if (args.length == 0) {

			RequestFuture<IMessage> future = RequestBuffer.request(new IRequest<IMessage>() {

				@Override
				public IMessage request() {
					return channel.sendMessage(
							"This command will react :o2::regional_indicator_o::regional_indicator_f: to this message. "
									+ "Or set the message id as an argument and i will react to that message. You probably need dev. mode for that.");
				}

			});
			IMessage msg1 = future.get();
			reactOff(msg1);
		} else if (args.length >= 1) {
			long i = 0;
			try {
				Long l = Long.parseLong(args[0]);
				i = l.longValue();
			} catch (Exception e) {
				sendMessage(channel, e.getClass().getName() + ": " + e.getMessage());
				return;
			}
			IMessage chIMessage = channel.getMessageByID(i);
			IMessage chMessageFetch = null;

			if (chIMessage == null) {
				RequestFuture<IMessage> msgFuture = RequestBuffer.request(new RequestResponse<IMessage, Long>(i) {

					@Override
					public IMessage request() {
						return channel.fetchMessage(this.s);
					}

				});
				chMessageFetch = msgFuture.get();
			}

			IMessage msg1 = chIMessage == null ? chMessageFetch : chIMessage;
			if (msg1 == null) {
				sendMessage(channel, "Did not find message with id: " + String.valueOf(i) + " in this channel.");
				return;
			}

			reactOff(msg1);
		}
	}

	private void reactOff(IMessage msg) {
		requestVoid(() -> {
			msg.removeAllReactions();
		});
		requestVoid(() -> {

			msg.addReaction(EmojiManager.getForAlias("o2"));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			msg.addReaction(EmojiManager.getForAlias("regional_indicator_symbol_o"));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			msg.addReaction(EmojiManager.getForAlias("regional_indicator_symbol_f"));

		});
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

	@Override
	public boolean bypassCMDChannel() {
		return true;
	}
}

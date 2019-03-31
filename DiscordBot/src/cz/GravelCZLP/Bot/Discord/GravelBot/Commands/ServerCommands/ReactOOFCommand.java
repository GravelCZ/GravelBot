package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.vdurmont.emoji.EmojiManager;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.IRequestArgs;
import cz.GravelCZLP.Bot.Utils.Logger;
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
				Logger.error("Guild: " + guild.getName() + ":" + guild.getStringID() + " does not have a #bot-spam channel");
			}
			return;
		}
		if (args.length == 0) {

			RequestFuture<IMessage> future = RequestBuffer.request(new IRequest<IMessage>() {

				@Override
				public IMessage request() {
					return channel.sendMessage(
							"This command will react :o2::regional_indicator_o::regional_indicator_f:  to this message. "
									+ "Or set the message id as an argument and i will react to that message. You probably need dev. mode for that.");
				}

			});
			IMessage msg1 = future.get();
			Runnable rar = new Runnable() {

				@Override
				public void run() {
					RequestBuffer.request(() -> {
						msg1.removeAllReactions();
					});
				}
			};

			Runnable e1 = new Runnable() {

				@Override
				public void run() {
					RequestBuffer.request(() -> {
						msg1.addReaction(EmojiManager.getForAlias("o2"));
					});
				}
			};

			Runnable e2 = new Runnable() {

				@Override
				public void run() {
					RequestBuffer.request(() -> {
						msg1.addReaction(EmojiManager.getForAlias("regional_indicator_symbol_o"));
					});
				}
			};

			Runnable e3 = new Runnable() {

				@Override
				public void run() {
					RequestBuffer.request(() -> {
						msg1.addReaction(EmojiManager.getForAlias("regional_indicator_symbol_f"));
					});
				}
			};
			
			ScheduledExecutorService e = Executors.newScheduledThreadPool(4);
			e.schedule(rar, 1, TimeUnit.SECONDS);
			e.schedule(e1, 2, TimeUnit.SECONDS);
			e.schedule(e2, 3, TimeUnit.SECONDS);
			e.schedule(e3, 4, TimeUnit.SECONDS);
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
				RequestFuture<IMessage> msgFuture = RequestBuffer.request(new IRequestArgs<IMessage, Long>(i) {
					
					@Override
					public IMessage request() {
						return channel.fetchMessage(this.s);
					}
					
				});
				chMessageFetch = msgFuture.get();
			}
			
			IMessage msgid = chIMessage == null ? chMessageFetch : chIMessage;
			if (msgid == null) {
				sendMessage(channel, "Did not find message with id: " + String.valueOf(i) + " in this channel.");
				return;
			}
			
			Runnable rar = new Runnable() {

				@Override
				public void run() {
					RequestBuffer.request(() -> {
						msgid.removeAllReactions();
					});
				}
			};

			Runnable e1 = new Runnable() {

				@Override
				public void run() {
					RequestBuffer.request(() -> {
						msgid.addReaction(EmojiManager.getForAlias("o2"));
					});
				}
			};

			Runnable e2 = new Runnable() {

				@Override
				public void run() {
					RequestBuffer.request(() -> {
						msgid.addReaction(EmojiManager.getForAlias("regional_indicator_symbol_o"));
					});
				}
			};

			Runnable e3 = new Runnable() {

				@Override
				public void run() {
					RequestBuffer.request(() -> {
						msgid.addReaction(EmojiManager.getForAlias("regional_indicator_symbol_f"));
					});
				}
			};
			
			ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();
			e.schedule(rar, 1, TimeUnit.SECONDS);
			e.schedule(e1, 2, TimeUnit.SECONDS);
			e.schedule(e2, 3, TimeUnit.SECONDS);
			e.schedule(e3, 4, TimeUnit.SECONDS);
		}
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

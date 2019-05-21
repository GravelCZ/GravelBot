package cz.GravelCZLP.Bot.Discord.GravelBot.Commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import cz.GravelCZLP.Bot.Main.Constants;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuffer.IRequest;
import sx.blah.discord.util.RequestBuffer.IVoidRequest;
import sx.blah.discord.util.RequestBuffer.RequestFuture;

public interface ICommand {

	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args);
	
	public boolean canExecute(IUser user, IGuild guild);
	
	default boolean bypassCMDChannel() {
		return false;
	}
	
	default Optional<IChannel> getBotSpamChannel(IGuild g) {
		return g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
	}
	
	default void requestVoid(IVoidRequest req) {
		RequestBuffer.request(req);
	}
	
	default <T> RequestFuture<T> requestResponse(IRequest<T> req) {
		return RequestBuffer.request(req);
	}
	
	default void sendMessage(IChannel c, String text) {
		RequestBuffer.request(() -> {
			c.sendMessage(text);
		});
	}
	
	default void sendMessage(IChannel c, EmbedObject e) {
		RequestBuffer.request(() -> {
			c.sendMessage(e);
		});
	}
	
	default void sendFile(IChannel c, File f) {
		RequestBuffer.request(() -> {
			try {
				c.sendFile(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}
	
	default void sendFile(IChannel c, File f, String text) {
		RequestBuffer.request(() -> {
			try {
				c.sendFile(text, f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}
}

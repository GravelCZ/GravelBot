package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands;

import java.util.EnumSet;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

public class DeleteAllCommandServer implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (hasPermissionDelete(msg.getClient().getOurUser(), guild, channel)) {
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					MessageHistory h = channel.getFullMessageHistory();
					for (int i = 0; i < h.size(); i++) {
						requestDelete(h.get(i));
					}	
				}
			};
			Thread t = new Thread(r);
			t.setName("Message deleter");
			t.start();
			sendMessage(sender.getOrCreatePMChannel(), "All messages will be deleted soon, depends on the amount");
		} else {
			sendMessage(sender.getOrCreatePMChannel(), "I cannot clean this mess up, i cannot delete other ppls messages.");
		}
	}

	private void requestDelete(IMessage msg) {
		RequestBuffer.request(() -> {
			msg.delete();
		});
	}
	
	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return PermissionsService.canExecute(user, guild, true, false);
	}
	
	public boolean hasPermissionDelete(IUser u, IGuild g, IChannel ch) {
		EnumSet<Permissions> permissions = u.getPermissionsForGuild(g);
		for (Permissions p : permissions) {
			if (p == Permissions.MANAGE_MESSAGES) {
				return true;
			}
		}
		if (ch != null) {
			EnumSet<Permissions> channelPermissions = ch.getModifiedPermissions(u);
			for (Permissions p : channelPermissions) {
				if (p == Permissions.MANAGE_MESSAGES) {
					return true;
				}
			}
		}
		return false;
	}
}

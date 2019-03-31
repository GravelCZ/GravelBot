package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PrivateCommands;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

public class DeleteAllCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser user, IGuild guild, String content, String[] args) {
		Logger.log(user.getName() + " nechce být pervert :D");
		MessageHistory history = channel.getFullMessageHistory();
		if (!history.isEmpty()) {
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					for (int i = 0; i < history.size(); i++) {
						IMessage m = history.get(i);
						if (m.getAuthor().getStringID().equals(m.getClient().getOurUser().getStringID())) {
							RequestBuffer.request(() -> {
								m.delete();
							});
						}
					}	
				}
			};
			Thread t = new Thread(r);
			t.setName("Private message deleter");
			t.start();
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}
	
}

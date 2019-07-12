package cz.GravelCZLP.Bot.Discord.GravelBot.Runnables;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cz.GravelCZLP.Bot.Main.Constants;
import cz.GravelCZLP.Bot.Utils.RequestWithParameters;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.RequestBuffer;

public class DateCheckerRunnable implements Runnable {

	private IDiscordClient client;
	private HashMap<String, Boolean> dates = new HashMap<>();
	private int year = 0;
	
	public DateCheckerRunnable(IDiscordClient client) {
		this.client = client;
		year = Calendar.getInstance().get(Calendar.YEAR);
		dates.put("christmas", Boolean.FALSE);
		dates.put("april", Boolean.FALSE);
		dates.put("jindrabday", Boolean.FALSE);
		dates.put("spooktober", Boolean.FALSE);
		dates.put("matbday", Boolean.FALSE);
	}
	
	@Override
	public void run() {
		int yearNow = Calendar.getInstance().get(Calendar.YEAR);
		if (year != yearNow) { // New year!!
			for (IGuild g : client.getGuilds()) {
				Optional<IChannel> channel = g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
				if (channel.isPresent()) {
					IChannel c = channel.get();
					String suffix = " everyone!!!";
					if (c.getModifiedPermissions(g.getClient().getOurUser()).contains(Permissions.MENTION_EVERYONE)) {
						suffix = " " + g.getEveryoneRole().mention() + " !!!";
					}
					RequestBuffer.request(new RequestWithParameters<IMessage, String>(suffix) {

						@Override
						public IMessage request() {
							return channel.get().sendMessage("Happy new year " + yearNow + this.s);
						}
					});
				} else {
					Logger.error("Guild: " + g.getName() + ":" + g.getStringID() + " does not have a #" + Constants.textChatCommands);
				}
			}
			year = yearNow;
		}
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (month == 12 - 1) { // 12 month
			if (day == 24) {
				if (hours == 15) {
					Boolean value = dates.get("christmas");
					if (!value.booleanValue()) {
						for (IGuild g : client.getGuilds()) {
							Optional<IChannel> channel = g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
							if (channel.isPresent()) {
								IChannel c = channel.get();
								String suffix = " everyone!!!";
								if (c.getModifiedPermissions(g.getClient().getOurUser()).contains(Permissions.MENTION_EVERYONE)) {
									suffix = " " + g.getEveryoneRole().mention() + " !!!";
								}
								RequestBuffer.request(new RequestWithParameters<IMessage, String>(suffix) {

									@Override
									public IMessage request() {
										return channel.get().sendMessage("Merry Christmas " + yearNow + this.s);
									}
								});
							} else {
								Logger.error("Guild: " + g.getName() + ":" + g.getStringID() + " does not have a #" + Constants.textChatCommands);
							}
						}
						dates.put("christmas", Boolean.TRUE);
					}
				}
			}
		}
		if (month == 4 - 1) {
			if (day == 1) {
				if (hours == 12) {
					Boolean value = dates.get("april");
					if (!value.booleanValue()) {
						for (IGuild g : client.getGuilds()) {
							
							Optional<IChannel> channel = g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
							if (channel.isPresent()) {
								RequestBuffer.request(() -> {
									channel.get().sendMessage("I will delete this server in 5 minutes !!!");
								});
								Runnable r = new Runnable() {
									
									@Override
									public void run() {
										RequestBuffer.request(() -> {
											channel.get().sendMessage("That is a joke, i would never do that. ;)");
										});
									}
								};
								Executors.newSingleThreadScheduledExecutor().schedule(r, 5, TimeUnit.MINUTES);
							} else {
								Logger.error("Guild: " + g.getName() + ":" + g.getStringID() + " does not have a #" + Constants.textChatCommands);
							}
						}
						dates.put("april", Boolean.TRUE);
					}
				}
			}
			if (day == 22) {
				if (hours == 12) {
					Boolean value = dates.get("jindrabday");
					if (!value.booleanValue()) {
						for (IGuild g : client.getGuilds()) {
							Optional<IChannel> channel = g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
							if (channel.isPresent()) {
								IChannel c = channel.get();
								Optional<IUser> gravel = Optional.ofNullable(g.getUserByID(331866918023725056L));
								if (gravel.isPresent()) {
									IUser gr = gravel.get();
									String prefix = "Everyone";
									if (c.getModifiedPermissions(g.getClient().getOurUser()).contains(Permissions.MENTION_EVERYONE)) {
										prefix = g.getEveryoneRole().mention();
									}
									RequestBuffer.request(new RequestWithParameters<IMessage, String>(prefix) {

										@Override
										public IMessage request() {
											return channel.get().sendMessage(this.s + " it is Gravel´s birthday today. Lets wish him a happy birthday.");
										}
									});
									RequestBuffer.request(new RequestWithParameters<IMessage, String>(gr.mention()) {

										@Override
										public IMessage request() {
											return channel.get().sendMessage("Happy birthday " + this.s);
										}
									});
								}
							}
						}
						dates.put("jindrabday", Boolean.TRUE);
					}
				}
			}
		}
		if (month == 10 - 1) {
			if (day == 31) {
				if (hours == 13) {
					Boolean value = dates.get("spooktober");
					if (!value.booleanValue()) {
						for (IGuild g : client.getGuilds()) {
							Optional<IChannel> channel = g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
							if (channel.isPresent()) {
								RequestBuffer.request(() -> {
									channel.get().sendMessage("Ohhhh.... It is spooktober!!");
								});
							} else {
								Logger.error("Guild: " + g.getName() + ":" + g.getStringID() + " does not have a #" + Constants.textChatCommands);
							}
						}
						dates.put("spooktober", Boolean.TRUE);
					}
				}
			}
		}
		if (month == 5 - 1) {
			if (day == 24) {
				if (hours == 13) {
					Boolean value = dates.get("matbday");
					if (!value.booleanValue()) {
						for (IGuild g : client.getGuilds()) {
							Optional<IChannel> channel = g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
							if (channel.isPresent()) {
								IChannel c = channel.get();
								Optional<IUser> mat = Optional.ofNullable(g.getUserByID(249213548524208129L));
								if (mat.isPresent()) {
									IUser gr = mat.get();
									String prefix = "Everyone";
									if (c.getModifiedPermissions(g.getClient().getOurUser()).contains(Permissions.MENTION_EVERYONE)) {
										prefix = g.getEveryoneRole().mention();
									}
									RequestBuffer.request(new RequestWithParameters<IMessage, String>(prefix) {

										@Override
										public IMessage request() {
											return channel.get().sendMessage(this.s + " it is Mat628CZ´s birthday today. Lets wish him a happy birthday.");
										}
									});
									RequestBuffer.request(new RequestWithParameters<IMessage, String>(gr.mention()) {

										@Override
										public IMessage request() {
											return channel.get().sendMessage("Happy birthday " + this.s);
										}
									});
								}
							}
						}
						dates.put("matbday", Boolean.TRUE);
					}
				}
			}
		}
	}

}

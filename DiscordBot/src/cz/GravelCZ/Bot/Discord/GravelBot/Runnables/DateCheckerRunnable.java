package cz.GravelCZ.Bot.Discord.GravelBot.Runnables;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;

public class DateCheckerRunnable implements Runnable {

	private JDA client;
	private HashMap<String, Boolean> dates = new HashMap<>();
	private int year = 0;
	
	public DateCheckerRunnable(JDA client) {
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
			for (Guild g : client.getGuilds()) {
				Optional<TextChannel> channel = g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
				if (channel.isPresent()) {
					TextChannel c = channel.get();
					String suffix = " everyone!!!";
					PermissionOverride pr = c.getPermissionOverride(g.getSelfMember());
					boolean flag = pr.getAllowed().stream().anyMatch(p -> p == Permission.MESSAGE_MENTION_EVERYONE);
					if (flag) {
						suffix = " @everyone !!!";
					}
					
					
					c.sendMessage("Happy new year " + yearNow + suffix).queue();
				} else {
					Logger.error("Guild: " + g.getName() + ":" + g.getId() + " does not have a #" + Constants.textChatCommands);
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
						for (Guild g : client.getGuilds()) {
							Optional<TextChannel> channel = g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
							if (channel.isPresent()) {
								TextChannel c = channel.get();
								String suffix = " everyone!!!";
								PermissionOverride pr = c.getPermissionOverride(g.getSelfMember());
								boolean flag = pr.getAllowed().stream().anyMatch(p -> p == Permission.MESSAGE_MENTION_EVERYONE);
								if (flag) {
									suffix = " @everyone !!!";
								}
								c.sendMessage("Merry Christmas " + yearNow + suffix).queue();
							} else {
								Logger.error("Guild: " + g.getName() + ":" + g.getId() + " does not have a #" + Constants.textChatCommands);
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
						for (Guild g : client.getGuilds()) {
							
							Optional<TextChannel> channel = g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
							if (channel.isPresent()) {
								channel.get().sendMessage("I will delete this server in 5 minutes !!!").queue();
								Runnable r = new Runnable() {
									
									@Override
									public void run() {
										channel.get().sendMessage("That is a joke, i would never do that. ;)").queue();
									}
								};
								Executors.newSingleThreadScheduledExecutor().schedule(r, 5, TimeUnit.MINUTES);
							} else {
								Logger.error("Guild: " + g.getName() + ":" + g.getId() + " does not have a #" + Constants.textChatCommands);
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
						for (Guild g : client.getGuilds()) {
							Optional<TextChannel> channel = g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
							if (channel.isPresent()) {
								TextChannel c = channel.get();
								Optional<Member> gravel = Optional.ofNullable(g.getMemberById(331866918023725056L));
								if (gravel.isPresent()) {
									Member gr = gravel.get();
									String prefix = "Everyone";
									PermissionOverride pr = c.getPermissionOverride(g.getSelfMember());
									boolean flag = pr.getAllowed().stream().anyMatch(p -> p == Permission.MESSAGE_MENTION_EVERYONE);
									if (flag) {
										prefix = " @everyone ";
									}
									channel.get().sendMessage(prefix + " it is Gravel´s birthday today. Lets wish him a happy birthday.").queue();
									channel.get().sendMessage("Happy birthday " + gr.getAsMention()).queue();
								} else {
									Logger.error("Guild: " + g.getName() + ":" + g.getId() + " does not have a #" + Constants.textChatCommands);
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
						for (Guild g : client.getGuilds()) {
							Optional<TextChannel> channel = g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
							if (channel.isPresent()) {
								channel.get().sendMessage("Ohhhh.... It is spooktober!! **S P O O K Y**").queue();
							} else {
								Logger.error("Guild: " + g.getName() + ":" + g.getId() + " does not have a #" + Constants.textChatCommands);
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
						for (Guild g : client.getGuilds()) {
							Optional<TextChannel> channel = g.getTextChannelsByName(Constants.textChatCommands, false).stream().findFirst();
							if (channel.isPresent()) {
								TextChannel c = channel.get();
								Optional<Member> m= Optional.ofNullable(g.getMemberById(249213548524208129L));
								if (m.isPresent()) {
									Member mat = m.get();
									String prefix = "Everyone";
									PermissionOverride pr = c.getPermissionOverride(g.getSelfMember());
									boolean flag = pr.getAllowed().stream().anyMatch(p -> p == Permission.MESSAGE_MENTION_EVERYONE);
									if (flag) {
										prefix = " @everyone ";
									}
									channel.get().sendMessage(prefix + "it is Mat628CZ´s birthday today. Lets wish him a happy birthday.").queue();
									channel.get().sendMessage("Happy birthday " + mat.getAsMention()).queue();
								} else {
									Logger.error("Guild: " + g.getName() + ":" + g.getId() + " does not have a #" + Constants.textChatCommands);
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

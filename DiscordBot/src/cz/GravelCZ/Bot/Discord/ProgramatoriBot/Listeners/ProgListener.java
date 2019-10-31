package cz.GravelCZ.Bot.Discord.ProgramatoriBot.Listeners;

import java.awt.Color;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cz.GravelCZ.Bot.Discord.ProgramatoriBot.ProgramatoriBot;
import cz.GravelCZ.Bot.Main.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
public class ProgListener  extends ListenerAdapter {

	private ProgramatoriBot bot;

	public ProgListener(ProgramatoriBot bot) {
		this.bot = bot;
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		Guild g = e.getGuild();
		Member m = e.getMember();
		TextChannel c = e.getChannel();
		Message msg = e.getMessage();
		String content = msg.getContentRaw();

		String[] params = content.split("\\ +");
		String alias = params[0].replaceFirst(Constants.commandPrefixpBot, "");
		String[] args = Arrays.copyOfRange(params, 1, params.length);

		if (!content.startsWith(Constants.commandPrefixpBot)) {
			return;
		}
		Runnable r = new Runnable() {
			public void run() {
				if (content.startsWith(Constants.commandPrefixpBot)) {
					boolean notSet = bot.getChannelNameAndID() == null;
					String[] split = null;
					if (!notSet) {
						split = bot.getChannelNameAndID().split(",");
					}
					if (!notSet || (c.getName().equalsIgnoreCase(split[0]) && c.getId().equals(split[1]))) {
						if (alias.equalsIgnoreCase("addlang")) {
							if (args.length == 0) {
								c.sendMessage(m.getAsMention() + "Musíš mi říct jaké jazyky ti mám přidělit, postupně nebo je odděl pomocí \";\" dám ti ty role, které budu moct.").queue();
								return;
							}
							if (args[0].contains(";")) {
								StringBuffer b = new StringBuffer();
								b.append(m.getAsMention() + "\n");
								
								List<String> rolesFound = Collections.emptyList();
								Arrays.asList(args[0].split(";")).stream().forEach(role -> {
									rolesFound.add(role.toLowerCase());
								});
								List<Role> roles = g.getRoles().stream().filter(r -> rolesFound.contains(r.getName().toLowerCase())).collect(Collectors.toList());
								
								if (roles.isEmpty()) {
									c.sendMessage(m.getAsMention() + " žádné z rolí které jsi mi dal neexistují.").queue();
									return;
								}
								
								roles.forEach(role -> {
									if (role.hasPermission(Permission.ADMINISTRATOR)) {
										c.sendMessage(m.getAsMention() + " tak takhle blbej fakt nejsem :D admina ti dávat nebudu.").queue();
										return;
									}
									g.addRoleToMember(m, role);
									b.append("Dal jsem ti roli: " + role.getName() + "\n");
								});
								
								c.sendMessage(b.toString()).queue();
							} else {
								Optional<Role> roleop = g.getRoles().stream()
										.filter(r -> r.getName().equalsIgnoreCase(args[0])).findFirst();
								if (roleop.isPresent()) {
									if (isAdminRole(roleop.get())) {
										c.sendMessage(m.getAsMention() + " tak takhle blbej fakt nejsem :D").queue();
										return;
									}
									g.addRoleToMember(m, roleop.get());
									c.sendMessage(m.getAsMention() + " Dal jsem ti roli: " + roleop.get().getName()).queue();
								} else {
									c.sendMessage(m.getAsMention() + " Tato role/programovací jazyk neexistuje. Pokud ano, kontaktuj adminy aby jej přidali.").queue();
								}
							}
						} else if (alias.equalsIgnoreCase("remlang")) {
							if (args.length == 0) {
								c.sendMessage(m.getAsMention() + " Musíš mi říct jaké jazyky ti mám odebrat.").queue();
								return;
							}
							List<Role> role = m.getRoles().stream()
									.filter(r -> r.getName().equalsIgnoreCase(args[0])).collect(Collectors.toList());
							if (role.isEmpty()) {
								c.sendMessage(m.getAsMention() + " tuto roli nemáš.").queue();
								return;
							}
							if (isAdminRole(role.get(0))) {
								c.sendMessage(m.getAsMention() + " tak takhle blbej fakt nejsem :D").queue();
								return;
							}
							g.removeRoleFromMember(m, role.get(0));
							c.sendMessage(m.getAsMention() + " Odebral jsem ti roli: " + role.get(0).getName()).queue();
						} else if (alias.equalsIgnoreCase("setchannel")) {
							
							if (m.getRoles().stream().anyMatch(r -> r.getPermissions().contains(Permission.ADMINISTRATOR))) {
								if (args.length == 0) {
									c.sendMessage(m.getAsMention() + " Řekni mi, jakej kanál mám použít.").queue();
									return;
								}
								Optional<TextChannel> cop = g.getTextChannelsByName(args[0], false).stream().findFirst();
								if (cop.isPresent()) {
									TextChannel cl = cop.get();
									String nameid = cl.getName() + "," + cl.getId();
									bot.setRoleChannel(nameid);
									c.sendMessage(m.getAsMention() + " Nastaveno na: " + cl.getName() + " ("
											+ cl.getId() + ")").queue();
								} else {
									c.sendMessage(m.getAsMention() + " ten kanál neexistuje.").queue();
								}
							} else {
								c.sendMessage(m.getAsMention() + " nejsi admin.").queue();
							}
						} else if (alias.equalsIgnoreCase("help")) {
							EmbedBuilder b = new EmbedBuilder();
							b.setTitle("Navod pro ProgramatoriBot " + bot.getVersion());
							b.setColor(new Color(75, 115, 216));
							b.setAuthor("ProgramatořiBot od GravelCZ", null, "https://i.imgur.com/qn9FWeA.png");
							b.addField(".addlang [jazyk]/[jazyk1;jazyk2]", "Přidá ti roli programovacího jazyku.",
									false);
							b.addField(".remlang [jazyk]", "Odebere ti roli programovacího jazyku.", false);
							b.addField(".setchannel", "Nastaví kanál na kterém má bot poslouchat, pouze pro adminy.",
									false);
							b.setFooter("Autor: GravelCZ; Bot napsán v Javě; Použitá library je JDA; v"
									+ bot.getVersion(), "https://i.imgur.com/MraElzj.png");
							c.sendMessage(b.build()).queue();
						}
					}
				}
			}
		};
		Thread t = new Thread(r);
		t.setName("Command Executor");
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				c.sendMessage("Exception occured in thread: " + t.getName() + "(" + t.getId() + ")" + " " + e.getClass().getName() + ": " + e.getMessage() + "\n Check console for errors.").queue();
				e.printStackTrace();
			}
		});
		t.start();
	}

	public boolean isAdminRole(Role r) {
		return r.getPermissions().contains(Permission.ADMINISTRATOR);
	}

}

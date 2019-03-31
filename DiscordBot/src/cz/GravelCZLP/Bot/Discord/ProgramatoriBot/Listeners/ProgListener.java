package cz.GravelCZLP.Bot.Discord.ProgramatoriBot.Listeners;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cz.GravelCZLP.Bot.Discord.ProgramatoriBot.ProgramatoriBot;
import cz.GravelCZLP.Bot.Main.Constants;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class ProgListener {

	private ProgramatoriBot bot;

	public ProgListener(ProgramatoriBot bot) {
		this.bot = bot;
	}

	@EventSubscriber
	public void onChat(MessageReceivedEvent e) {
		IGuild g = e.getGuild();
		IUser user = e.getAuthor();
		IChannel c = e.getChannel();
		IMessage msg = e.getMessage();
		String content = msg.getContent();

		String[] params = content.split("\\ +");
		String alias = params[0].replaceFirst(Constants.commandPrefixpBot, "");
		String[] args = Arrays.copyOfRange(params, 1, params.length);

		/*if (c.isPrivate()) {
			if (!content.equals("test")) {
				return;
			}
			Optional<IGuild> og = e.getClient().getGuilds().stream().filter(gu -> gu.getName().contains("Programátoři")).findFirst();
			if (!og.isPresent()) {
				return;
			}
			IGuild gu = og.get();
			List<IRole> roles = gu.getRolesForUser(e.getClient().getOurUser());
			Optional<IRole> or = roles.stream().filter(role -> role.getName().contains("Bot")).findFirst();
			if (!or.isPresent()) {
				return;
			}
			IRole role = or.get();
			StringBuffer sb = new StringBuffer();
			for (Permissions p : role.getPermissions()) {
				sb.append(p.name() + " ");
			}
			c.sendMessage(sb.toString());
			return;
		}*/
		if (!content.startsWith(Constants.commandPrefixpBot)) {
			return;
		}
		Runnable r = new Runnable() {
			public void run() {
				if (content.startsWith(Constants.commandPrefixpBot)) {
					boolean notSet = bot.getChannelNameAndID() == null;
					String[] split = new String[] { "test", "test" };
					if (!notSet) {
						split = bot.getChannelNameAndID().split(",");
					}
					if ((c.getName().equalsIgnoreCase(split[0]) && c.getStringID().equals(split[1])) || notSet) {
						if (alias.equalsIgnoreCase("addlang")) {
							if (args.length == 0) {
								sendMessage(c, user.mention()
										+ "Musíš mi říct jaké jazyky ti mám přidělit, postupně nebo je odděl pomocí \";\" dám ti ty role, které budu moct.");
								return;
							}
							if (args[0].contains(";")) {
								String[] split2 = args[0].split(";");
								StringBuffer b = new StringBuffer();
								b.append(user.mention() + "\n");
								boolean givenRoles = false;
								for (int i = 0; i < split2.length; i++) {
									String role = split2[i];
									List<IRole> roles = g.getRoles().stream()
											.filter(r -> r.getName().equalsIgnoreCase(role))
											.collect(Collectors.toList());
									if (roles.isEmpty()) {
										sendMessage(c, user.mention() + " žádné z rolí které jsi mi dal neexistují.");
										return;
									} else {
										for (IRole ro : roles) {
											if (isAdminRole(ro)) {
												sendMessage(c, user.mention()
														+ " tak takhle blbej fakt nejsem :D admina ti dávat nebudu.");
												return;
											}
											user.addRole(ro);
											givenRoles = true;
											b.append("Dal jsem ti roli: " + ro.getName() + "\n");
										}
									}
								}
								if (givenRoles) {
									sendMessage(c, b.toString());
								}
							} else {
								Optional<IRole> roleop = g.getRoles().stream()
										.filter(r -> r.getName().equalsIgnoreCase(args[0])).findFirst();
								if (roleop.isPresent()) {
									if (isAdminRole(roleop.get())) {
										sendMessage(c, user.mention() + " tak takhle blbej fakt nejsem :D");
										return;
									}
									user.addRole(roleop.get());
									sendMessage(c, user.mention() + " Dal jsem ti roli: " + roleop.get().getName());
								} else {
									sendMessage(c, user.mention()
											+ " Tato role/programovací jazyk neexistuje. Pokud ano, kontaktuj adminy aby jej přidali.");
								}
							}
						} else if (alias.equalsIgnoreCase("remlang")) {
							if (args.length == 0) {
								sendMessage(c, user.mention() + " Musíš mi říct jaké jazyky ti mám odebrat.");
								return;
							}
							List<IRole> role = user.getRolesForGuild(g).stream()
									.filter(r -> r.getName().equalsIgnoreCase(args[0])).collect(Collectors.toList());
							if (role.isEmpty()) {
								sendMessage(c, user.mention() + " tuto roli nemáš.");
								return;
							}
							if (isAdminRole(role.get(0))) {
								sendMessage(c, user.mention() + " tak takhle blbej fakt nejsem :D");
								return;
							}
							user.removeRole(role.get(0));
							sendMessage(c, user.mention() + " Odebral jsem ti roli: " + role.get(0).getName());
						} else if (alias.equalsIgnoreCase("setchannel")) {
							if (user.getPermissionsForGuild(g).contains(Permissions.ADMINISTRATOR)) {
								if (args.length == 0) {
									sendMessage(c, user.mention() + " Řekni mi, jakej kanál mám použít.");
									return;
								}
								Optional<IChannel> cop = g.getChannelsByName(args[0]).stream().findFirst();
								if (cop.isPresent()) {
									IChannel cl = cop.get();
									String nameid = cl.getName() + "," + cl.getStringID();
									bot.setRoleChannel(nameid);
									sendMessage(c, user.mention() + " Nastaveno na: " + cl.getName() + " ("
											+ cl.getStringID() + ")");
								} else {
									sendMessage(c, user.mention() + " ten kanál neexistuje.");
								}
							} else {
								sendMessage(c, user.mention() + " nejsi admin.");
							}
						} else if (alias.equalsIgnoreCase("help")) {
							EmbedBuilder b = new EmbedBuilder();
							b.withTitle("Navod pro ProgramatoriBot " + bot.getVersion());
							b.withColor(75, 115, 216);
							b.withAuthorName("ProgramatořiBot od GravelCZLP");
							b.withAuthorIcon("https://i.imgur.com/qn9FWeA.png");
							b.appendField(".addlang [jazyk]/[jazyk1;jazyk2]", "Přidá ti roli programovacího jazyku.",
									false);
							b.appendField(".remlang [jazyk]", "Odebere ti roli programovacího jazyku.", false);
							b.appendField(".setchannel", "Nastaví kanál na kterém má bot poslouchat, pouze pro adminy.",
									false);
							b.withFooterText("Autor: GravelCZLP; Bot napsán v Javě; Použitá library je Discord4J; v"
									+ bot.getVersion());
							b.withFooterIcon("https://i.imgur.com/MraElzj.png");
							RequestBuffer.request(() -> {
								c.sendMessage(b.build());
							});
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
				sendMessage(c, "Exception occured in thread: " + t.getName() + "(" + t.getId() + ")" + " " + e.getClass().getName() + ": " + e.getMessage() + "\n Check console for errors.");
				e.printStackTrace();
			}
		});
		t.start();
	}

	public boolean isAdminRole(IRole r) {
		return r.getPermissions().contains(Permissions.ADMINISTRATOR);
	}

	private void sendMessage(IChannel c, String string) {
		RequestBuffer.request(() -> {
			c.sendMessage(string);
		});
	}

}

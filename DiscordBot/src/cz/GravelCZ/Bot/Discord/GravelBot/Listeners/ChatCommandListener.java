package cz.GravelCZ.Bot.Discord.GravelBot.Listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IPrivateCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.IServerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.MainCommandManager;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.EmbedUtils;
import cz.GravelCZ.Bot.Utils.Logger;
import cz.GravelCZ.Bot.Utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatCommandListener extends ListenerAdapter {

	private List<String> keywords = new ArrayList<>(
			Arrays.asList("boi", "nibba", "emoji", "thonk", "thinking", "thonking", "oh that is hot"));

	private List<String> pyroKeywords = new ArrayList<>(Arrays.asList("so", "guys", "we", "did", "it", "reached"));

	private List<String> defaultDances = new ArrayList<>(Arrays.asList(
			"https://media.tenor.com/images/dd492862f2a07898534dcd2bdd2807e9/tenor.gif",
			"https://media.giphy.com/media/8scSaaxBspqRG/giphy.gif",
			"https://lh3.googlemcontent.com/-YTQ_8wGhsmw/W3K0p53Ve8I/AAAAAAAAy6A/ez0rsS1UBTAG_EA5tMPIdLbqsPp623b1gCJoC/w424-h318-n/gplus-274915484.gif",
			"https://tenor.com/view/fortnite-dance-fortnite-dance-gif-12585046"));

	private HashMap<String, Long> lastCommand = new HashMap<>();

	private MainCommandManager commandManager;

	public ChatCommandListener() {
		commandManager = new MainCommandManager();
		commandManager.init();
	}

	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		Message msg = e.getMessage();
		PrivateChannel channel = e.getChannel();
		User user = e.getAuthor();
		
		if (channel.getJDA().getSelfUser().getId().equals(user.getId())) {
			return;
		}
		
		String content = msg.getContentRaw();

		String[] params = content.split("\\ +");
		String alias = params[0].replaceFirst(Constants.commandPrefix, "");
		String[] args = Arrays.copyOfRange(params, 1, params.length);

		if (content.startsWith(Constants.commandPrefix)) {
			IPrivateCommand command = commandManager.getPrivateCommand(alias.toLowerCase());
			if (command == null) {
				channel.sendMessage("I don't know about that command boi").queue();
				return;
			}
			if (command.canExecute(user)) {
				command.execute(msg, channel, user, content, args);
			} else {
				channel.sendMessage("You are not an admin -_-").queue();
			}
		} else {
			Logger.log("Private Message from " + user.getName() + ": " + content);
			EmbedBuilder b = EmbedUtils.newBuilder();
			b.setDescription("What do you want ?");
			b.addField("Who made/programmed me ?", "It was the devil himself. GravelCZ", false);
			b.addField("What music is playing ?", "I dont know.", false);

			channel.sendMessage(b.build());
		}
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		Message msg = e.getMessage();
		TextChannel channel = e.getChannel();
		Member m = e.getMember();
		Guild g = e.getGuild();
		String content = msg.getContentRaw();

		if (g.getSelfMember().equals(m)) {
			return;
		}
		
		String[] params = content.split("\\ +");
		String alias = params[0].replaceFirst(Constants.commandPrefix, "");
		String[] args = Arrays.copyOfRange(params, 1, params.length);

		if (content.startsWith(Constants.commandPrefix)) {
			IServerCommand command = commandManager.getServerCommand(alias.toLowerCase());
			boolean canUse = false;
			if (PermissionsService.isAdmin(m)) {
				canUse = true;
			}
			if (lastCommand.containsKey(m.getId())) {
				Long lastUse = lastCommand.get(m.getId());
				if ((System.currentTimeMillis() - lastUse.longValue()) > 2500) {
					canUse = true;
				}
			} else {
				canUse = true;
			}
			if (canUse) {
				if (command == null) {
					if (channel.getName().equals(Constants.textChatCommands)) {
						channel.sendMessage("That command does not exist.").queue();
						;
					} else {
						m.getUser().openPrivateChannel().queue(c -> {
							c.sendMessage("Server commands only in #bot-spam, also that command does not exist.")
									.queue();
						});
					}
					return;
				} else {
					if (!channel.getName().equals(Constants.textChatCommands) && !command.bypassCMDChannel()) {
						m.getUser().openPrivateChannel().queue(c -> {
							c.sendMessage("Server commands only in #bot-spam.").queue();
						});
						return;
					}
					if (command.canExecute(m)) {
						command.execute(msg, channel, g, m, content, args);
					} else {
						channel.sendMessage("You are not an admin -_-").queue();
						;
					}
					lastCommand.put(m.getId(), System.currentTimeMillis());
				}
			}
		} else if (!ShitpostHandler.isBlocked(g)) {
			for (String key : keywords) {
				if (key.equalsIgnoreCase(content)) {
					ArrayList<Emote> emojis = new ArrayList<>(e.getGuild().getEmotes());
					if (emojis.isEmpty()) {
						return;
					}
					emojis.removeIf(emj -> emj.isManaged());
					int random = Utils.getRandom().nextInt(emojis.size());
					Emote emj = emojis.get(random);
					String name = emj.getName();
					long id = emj.getIdLong();
					if (emj.isAnimated()) {
						channel.sendMessage("<a:" + name + ":" + id + ">").queue();
						;
					} else {
						channel.sendMessage("<:" + name + ":" + id + ">").queue();
						;
					}

					return;
				}
			}
			String[] splitBySpace = content.split(" ");
			int referneced = 0;

			ArrayList<String> pyroKeywordsCopy = new ArrayList<>(pyroKeywords);

			for (String split : splitBySpace) {
				Iterator<String> iterator = pyroKeywordsCopy.iterator();

				while (iterator.hasNext()) {
					String key = iterator.next();
					if (split.equalsIgnoreCase(key)) {
						iterator.remove();
						Logger.debug(key + " matches: " + split);
						referneced++;
					}
				}
			}

			if (referneced >= 3) {
				try {
					String script = Utils.loadFile(new File("./BotDataFolder/soguyswedidit.txt"));
					channel.sendMessage("**" + script + "**").queue();
					;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}

			if (content.equalsIgnoreCase("fms")) {
				try {
					String fms = Utils.loadFile(new File("./BotDataFolder/fuckmyass.txt"));
					channel.sendMessage(fms).queue();
					;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}

			if (content.equalsIgnoreCase("lenny")) {
				channel.sendMessage("( ͡° ͜ʖ ͡°)").queue();
				return;
			}

			if (content.equalsIgnoreCase("nigga")) {
				channel.sendMessage("THAT IS RACIST YOU CAN'T SAY THE N WORD!!!").queue();
				;
				return;
			}

			if (content.equalsIgnoreCase("take the l")) {
				channel.sendMessage(
						"https://cdn.discordapp.com/attachments/521546183529857025/521551062491070465/1.gif\n"
								+ "https://cdn.discordapp.com/attachments/521546183529857025/521551066295304193/2.gif\n"
								+ "https://cdn.discordapp.com/attachments/521546183529857025/521551069004824576/3.gif\n"
								+ "https://cdn.discordapp.com/attachments/521546183529857025/521551072494616580/4.gif\n"
								+ "https://cdn.discordapp.com/attachments/521546183529857025/521551074830843904/5.gif")
						.queue();
				return;
			}

			if (content.equalsIgnoreCase("default dance")) {
				int randomUrl = Utils.getRandom().nextInt(defaultDances.size());
				channel.sendMessage(defaultDances.get(randomUrl)).queue();
				;
				return;
			}

			if (content.equalsIgnoreCase("Bewwwbbsss")) {
				channel.sendFile(new File("./BotDataFolder/Bewwwbbsss.gif")).queue();
			}
		}
	}

}

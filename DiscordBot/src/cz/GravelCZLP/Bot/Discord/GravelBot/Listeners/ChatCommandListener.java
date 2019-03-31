package cz.GravelCZLP.Bot.Discord.GravelBot.Listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.MainCommandManager;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PermissionsService;
import cz.GravelCZLP.Bot.Main.Constants;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class ChatCommandListener {

	private List<String> keywords = new ArrayList<>(
			Arrays.asList("boi", "nibba", "emoji", "thonk", "thinking", "thonking", "oh that is hot", "meme review"));

	private List<String> pyroKeywords = new ArrayList<>(Arrays.asList("so", "guys", "we", "did", "it", "reached"));

	private List<String> defaultDances = new ArrayList<>(Arrays.asList(
			"https://media.tenor.com/images/dd492862f2a07898534dcd2bdd2807e9/tenor.gif",
			"https://media.giphy.com/media/8scSaaxBspqRG/giphy.gif",
			"https://lh3.googleusercontent.com/-YTQ_8wGhsmw/W3K0p53Ve8I/AAAAAAAAy6A/ez0rsS1UBTAG_EA5tMPIdLbqsPp623b1gCJoC/w424-h318-n/gplus-274915484.gif",
			"https://tenor.com/view/fortnite-dance-fortnite-dance-gif-12585046"));

	private HashMap<String, Long> lastCommand = new HashMap<>();

	private MainCommandManager commandManager;
	
	public ChatCommandListener() {
		commandManager = new MainCommandManager();
		commandManager.init();
	}
	
	@EventSubscriber
	public void onMessage(MessageReceivedEvent e) {
		IMessage msg = e.getMessage();
		IChannel channel = e.getChannel();
		IUser user = e.getAuthor();
		IGuild g = e.getGuild();
		String content = msg.getContent();

		String[] params = content.split("\\ +");
		String alias = params[0].replaceFirst(Constants.commandPrefix, "");
		String[] args = Arrays.copyOfRange(params, 1, params.length);

		if (content.startsWith(Constants.commandPrefix)) {
			if (channel.isPrivate()) {
				ICommand command = commandManager.getPrivateCommand(alias.toLowerCase());
				if (command == null) {
					RequestBuffer.request(() -> {
						channel.sendMessage("I don't know about that command man.");
					});
					return;
				}
				if (command.canExecute(user, g)) {
					command.execute(msg, channel, user, g, content, args);
				} else {
					RequestBuffer.request(() -> {
						channel.sendMessage("You are not an admin -_-");
					});
				}
				return;
			} else {
				ICommand command = commandManager.getServerCommand(alias.toLowerCase());
				boolean canUse = false;
				if (PermissionsService.isAdmin(user, g)) {
					canUse = true;
				}
				if (lastCommand.containsKey(user.getStringID())) {
					Long lastUse = lastCommand.get(user.getStringID());
					if ((System.currentTimeMillis() - lastUse.longValue()) > 2500) {
						canUse = true;
					}
				} else {
					canUse = true;
				}
				if (canUse) {
					if (command == null) {
						if (channel.getName().equals(Constants.textChatCommands)) {
							RequestBuffer.request(() -> {
								channel.sendMessage("That command does not exist.");
							});	
						} else {
							RequestBuffer.request(() -> {
								user.getOrCreatePMChannel().sendMessage("Server commands only in #bot-spam, also that command does not exist.");
							});
						}
						return;
					} else {
						if (!channel.getName().equals(Constants.textChatCommands) && !command.bypassCMDChannel()) {
							RequestBuffer.request(() -> {
								user.getOrCreatePMChannel().sendMessage("Server commands only in #bot-spam.");
							});
							return;
						}
						if (command.canExecute(user, g)) {
							command.execute(msg, channel, user, g, content, args);
						} else {
							RequestBuffer.request(() -> {
								channel.sendMessage("You are not an admin -_-");
							});
						}
						lastCommand.put(user.getStringID(), System.currentTimeMillis());
					}
				}
			}
		}
		
		if (channel.isPrivate()) {
			Logger.log("Private Message from " + user.getName() + ": " + content);
			EmbedBuilder b = new EmbedBuilder();
			b.withColor(0, 255, 0);
			b.withTitle("Hello o/");
			b.withAuthorName("GravelBot");
			b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
			b.withDescription("What do you want ?");
			b.appendField("Who made/programmed me ?", "It was the devil himself. GravelCZLP", false);
			b.appendField("What music is playing ?", "I dont know.", false);
			b.withFooterText("Command prefix: !/ - Help command: !/help");
			EmbedObject obj = b.build();
			RequestBuffer.request(() -> {
				channel.sendMessage(obj);
			});
			return;
		}
		// END PM
		
		if (!ShitpostHandler.isBlocked(g)) {
			// START SHITPOST ANY CHANNEL
			for (String key : keywords) {
				if (key.equalsIgnoreCase(content)) {
					ArrayList<IEmoji> emojis = new ArrayList<>(e.getGuild().getEmojis());
					emojis.removeIf(emj -> emj.isManaged());
					int random = Utils.getRandom().nextInt(emojis.size());
					IEmoji emj = emojis.get(random);
					String name = emj.getName();
					long id = emj.getLongID();
					RequestBuffer.request(() -> {
						if (emj.isAnimated()) {
							channel.sendMessage("<a:" + name + ":" + id + ">");
						} else {
							channel.sendMessage("<:" + name + ":" + id + ">");
						}
					});

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
					RequestBuffer.request(() -> {
						channel.sendMessage("**" + script + "**");
					});
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}

			if (content.equalsIgnoreCase("fms")) {
				try {
					String fms = Utils.loadFile(new File("./BotDataFolder/fuckmyass.txt"));
					RequestBuffer.request(() -> {
						channel.sendMessage(fms);
					});
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}

			if (content.equalsIgnoreCase("lenny")) {
				RequestBuffer.request(() -> {
					channel.sendMessage("( ͡° ͜ʖ ͡°)");
				});
				return;
			}

			if (content.equalsIgnoreCase("nigga")) {
				RequestBuffer.request(() -> {
					channel.sendMessage("THAT IS RACIST YOU CANT SAY THE N WORD!!!");
					channel.sendMessage("https://www.youtube.com/watch?v=okXgRLF-HEs");
				});
				return;
			}
			
			if (content.equalsIgnoreCase("take the l")) {
				RequestBuffer.request(() -> {
					channel.sendMessage(
							"https://cdn.discordapp.com/attachments/521546183529857025/521551062491070465/1.gif\n"
									+ "https://cdn.discordapp.com/attachments/521546183529857025/521551066295304193/2.gif\n"
									+ "https://cdn.discordapp.com/attachments/521546183529857025/521551069004824576/3.gif\n"
									+ "https://cdn.discordapp.com/attachments/521546183529857025/521551072494616580/4.gif\n"
									+ "https://cdn.discordapp.com/attachments/521546183529857025/521551074830843904/5.gif");
				});
				return;
			}

			if (content.equalsIgnoreCase("default dance")) {
				int randomUrl = Utils.getRandom().nextInt(defaultDances.size());
				RequestBuffer.request(() -> {
					channel.sendMessage(defaultDances.get(randomUrl));
				});
				return;
			}

			if (content.equalsIgnoreCase("Bewwwbbsss")) {
				RequestBuffer.request(() -> {
					try {
						channel.sendFile(new File("./BotDataFolder/Bewwwbbsss.gif"));
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				});
			}	
		}
	}
}

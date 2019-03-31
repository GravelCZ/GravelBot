package cz.GravelCZLP.Bot.Discord.GravelBot.Timers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cz.GravelCZLP.Bot.APIs.Reddit.RedditAPI;
import cz.GravelCZLP.Bot.APIs.Reddit.RedditAPI.RedditPost;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class RedditShitpostRunnable implements Runnable {

	private RedditAPI reddit;
	private IDiscordClient client;

	public RedditShitpostRunnable(IDiscordClient client) {
		reddit = new RedditAPI();
		this.client = client;
	}

	@Override
	public void run() {
		if (reddit.isTokenExpired()) {
			reddit.auth();
		}

		List<RedditPost> posts = reddit.getPosts();

		for (IGuild g : client.getGuilds()) {
			for (RedditPost post : posts) {
				List<IChannel> ch = new ArrayList<>(g.getChannels()).stream()
						.filter(c -> c.getName().contains("shitpost") && c.getName().contains("bot"))
						.collect(Collectors.toList());
				List<IChannel> channels = ch.stream()
						.filter(c -> c.getModifiedPermissions(client.getOurUser()).contains(Permissions.SEND_MESSAGES))
						.collect(Collectors.toList());

				if (ch.isEmpty()) {
					Logger.log("Guild: " + g.getName() + " does not have a shitpost channel.");
					break;
				}

				if (!ch.isEmpty() && channels.isEmpty()) {
					Logger.log("Guild: " + g.getName() + " has bot-shitpost channel, but bot cant send messages to it.");
					break;
				}

				EmbedBuilder b = new EmbedBuilder();

				b.withColor(255, 69, 0);
				b.withTitle(post.getTitle());
				b.withAuthorName(post.getUser());
				b.withAuthorUrl("https://www.reddit.com" + post.getPermalinkURL());
				b.withImage(post.getImageURL());
				b.withFooterText("SubReddit: " + post.getSubReddit());

				boolean send = true;

				if (post.isNSFW()) {
					if (!channels.get(0).isNSFW()) {
						send = false;
					}
				}

				if (post.isNSFW()) {
					b.withDescription("NSFW: " + (post.isNSFW() ? "yes" : "no") + " \n" + "Spoiler: "
							+ (post.isSpoiler() ? "yes" : "no")
							+ "The users name is a permalink to the post on the subreddit.");
					b.withFooterText("SubReddit: " + post.getSubReddit());
				} else {
					b.withDescription("Spoiler: " + post.isSpoiler()
							+ "\n The users name is a permalink to the post on the subreddit.");
				}
				if (send) {
					if (!client.isReady()) {
						Logger.error("We are not ready to send a message. Waiting 5 sec..");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					RequestBuffer.request(() -> {
						channels.get(0).sendMessage(b.build());
					});
				} else {
					Logger.log("Skipping reddit post because it is NSFW!");
				}
			}
		}

		Logger.debug("Shitposting done. next in 30 mins");
	}

}

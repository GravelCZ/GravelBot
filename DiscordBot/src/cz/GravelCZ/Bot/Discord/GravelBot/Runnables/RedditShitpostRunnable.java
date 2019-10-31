package cz.GravelCZ.Bot.Discord.GravelBot.Runnables;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cz.GravelCZ.Bot.APIs.Reddit.RedditAPI;
import cz.GravelCZ.Bot.APIs.Reddit.RedditAPI.RedditPost;
import cz.GravelCZ.Bot.Utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;

public class RedditShitpostRunnable implements Runnable {

	private RedditAPI reddit;
	private JDA client;

	public RedditShitpostRunnable(JDA client) {
		reddit = new RedditAPI();
		this.client = client;
	}

	@Override
	public void run() {
		if (reddit.isTokenExpired()) {
			reddit.auth();	
		}

		List<RedditPost> posts = reddit.getPosts();

		for (Guild g : client.getGuilds()) {
			for (RedditPost post : posts) {
				List<TextChannel> ch = new ArrayList<>(g.getTextChannels()).stream()
						.filter(c -> c.getName().contains("shitpost") && c.getName().contains("bot"))
						.filter(c -> {
							PermissionOverride pr = c.getPermissionOverride(g.getSelfMember());
							return pr.getAllowed().stream().anyMatch(p -> p == Permission.MESSAGE_MENTION_EVERYONE);
						})
						.collect(Collectors.toList());

				if (ch.isEmpty()) {
					break;
				}

				EmbedBuilder b = new EmbedBuilder();

				b.setColor(new Color(255, 69, 0));
				b.setTitle(post.getTitle());
				b.setAuthor(post.getUser(), "https://www.reddit.com" + post.getPermalinkURL());
				b.setImage(post.getImageURL());
				b.setFooter("SubReddit: " + post.getSubReddit());

				boolean send = true;

				if (post.isNSFW()) {
					if (!ch.get(0).isNSFW()) {
						send = false;
					}
				}

				if (post.isNSFW()) {
					b.setDescription("NSFW: " + (post.isNSFW() ? "yes" : "no") + " \n" + "Spoiler: "
							+ (post.isSpoiler() ? "yes" : "no")
							+ "The users name is a permalink to the post on the subreddit.");
					b.setFooter("SubReddit: " + post.getSubReddit());
				} else {
					b.setDescription("Spoiler: " + post.isSpoiler()
							+ "\n The users name is a permalink to the post on the subreddit.");
				}
				if (send) {
					ch.get(0).sendMessage(b.build()).queue();
				} else {
					Logger.log("Skipping reddit post because it is NSFW!");
				}
			}
		}

		Logger.debug("Shitposting done. next in 30 mins");
	}

}

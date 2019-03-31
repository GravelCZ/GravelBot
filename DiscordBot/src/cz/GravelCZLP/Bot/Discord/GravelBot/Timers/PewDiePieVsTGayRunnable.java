package cz.GravelCZLP.Bot.Discord.GravelBot.Timers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZLP.Bot.Main.Constants;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class PewDiePieVsTGayRunnable implements Runnable {

	private IDiscordClient client;
	
	private String tSeriesChannelId = "UCq-Fj5jknLsUf-MWSy4_brA";
	private String pewdiepieChennelId = "UC-lHJZR3Gqxm24_Vd_AJ5Yw";
	private String apiRequestUrl = "https://www.googleapis.com/youtube/v3/channels?part=statistics&id=%CHID%&key=%KEY%";
	
	private boolean broadcastedLost = false;
	private List<String> broadcastedValues = new ArrayList<>();
	
	private static int subDiffernce = 0;
	private static boolean hasPdpLost = false;
	
	public PewDiePieVsTGayRunnable(IDiscordClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		int currentPdpSubs = getSubscibers(pewdiepieChennelId);
		int currentTGaySubs = getSubscibers(tSeriesChannelId);
		
		if (currentPdpSubs == -1 || currentTGaySubs == -1) {
			System.err.println("Failed to get subscribers");
			return;
		}
		
		if (currentTGaySubs > currentPdpSubs) {
			hasPdpLost = true;
		}
		
		subDiffernce = Math.abs(currentPdpSubs - currentTGaySubs);
		
		if (hasPdpLost) {
			if (!broadcastedLost) {
				List<IGuild> guilds = client.getGuilds();
				for (IGuild g : guilds) {
					Optional<IChannel> botSpamChannel = g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
					if (!botSpamChannel.isPresent()) {
						continue;
					}
					IChannel c = botSpamChannel.get();
					RequestBuffer.request(() -> {
						c.sendMessage("We have lost bois... RIP!!!!");
					});
				}
				broadcastedLost = true;
			} else {
				return;
			}
		}
		
		String message = "";
		
		if (subDiffernce > 1000) {
			broadcastedValues.remove("1k");
		} else if (subDiffernce > 10000) {
			broadcastedValues.remove("10k");
		} else if (subDiffernce > 50000) {
			broadcastedValues.remove("50k");
		} else if (subDiffernce > 10000) {
			broadcastedValues.remove("100k");
		} else if (subDiffernce > 500000) {
			broadcastedValues.remove("500k");
		} else if (subDiffernce > 1000000) {
			broadcastedValues.remove("1m");
		}
		
		if (subDiffernce < 50) {
			message = "They are less then 50 subs away bois..";
		} else if (subDiffernce < 1000) {
			if (broadcastedValues.contains("1k")) {
				return;
			}
			broadcastedValues.add("1k");
			message = "They are less then 1000 subs away.";
		} else if (subDiffernce < 10000) {
			if (broadcastedValues.contains("10k")) {
				return;
			}
			broadcastedValues.add("10k");
			message = "They are less then 10k subs away.";
		} else if (subDiffernce < 50000) {
			if (broadcastedValues.contains("50k")) {
				return;
			}
			broadcastedValues.add("50k");
			message = "They are less then 50k subs away.";
		} else if (subDiffernce < 100000) {
			if (broadcastedValues.contains("100k")) {
				return;
			}
			broadcastedValues.add("100k");
			message = "They are less then 100k subs away.";
		} else if (subDiffernce < 500000) {
			if (broadcastedValues.contains("500k")) {
				return;
			}
			broadcastedValues.add("500k");
			message = "They are less then 500k subs away.";
		} else if (subDiffernce < 1000000) {
			if (broadcastedValues.contains("1m")) {
				return;
			}
			broadcastedValues.add("1m");
			message = "They are less then 1M subs away.";
		}
		EmbedBuilder b = new EmbedBuilder();
		b.withTitle("PewDiePie VS T-Series");
		b.withColor(255, 0, 0);
		b.withDescription("Fight of the century\n" + message);
		b.withImage("https://i.imgur.com/s2TznU6.jpg");
		b.appendField("Subsciber Difference", String.valueOf(subDiffernce), true);
		b.appendField("Has PewDiePie Lost?", hasPdpLost ? "**Yes**" : "No", true);
		b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J; v" + Utils.getVersion());
		b.withFooterIcon("https://i.imgur.com/MraElzj.png");
		List<IGuild> guilds = client.getGuilds();
		for (IGuild g : guilds) {
			Optional<IChannel> botSpamChannel = g.getChannelsByName(Constants.textChatCommands).stream().findFirst();
			if (!botSpamChannel.isPresent()) {
				continue;
			}
			IChannel c = botSpamChannel.get();
			RequestBuffer.request(() -> {
				c.sendMessage(b.build());
			});
		}
	}
	
	public static boolean hasPdpLost() {
		return hasPdpLost;
	}
	
	public static int getSubDifference() {
		return subDiffernce;
	}
	
	private int getSubscibers(String channel) {
		try {
			URL url = new URL(apiRequestUrl.replaceFirst("%CHID%", channel).replaceFirst("%KEY%", Constants.youtubeApiKey));
			String response = Utils.makeUrlGetRequest(url, new HashMap<>(), false);
			
			JSONObject responseObject = new JSONObject(response);
			JSONArray items = responseObject.getJSONArray("items");
			return items.getJSONObject(0).getJSONObject("statistics").getInt("subscriberCount");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
}

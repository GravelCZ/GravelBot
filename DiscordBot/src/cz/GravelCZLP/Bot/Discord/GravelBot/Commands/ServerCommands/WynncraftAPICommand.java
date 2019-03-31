package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ServerCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Utils;
import cz.GravelCZLP.WynncraftAPI.GuildStats;
import cz.GravelCZLP.WynncraftAPI.GuildStats.Member;
import cz.GravelCZLP.WynncraftAPI.LeaderboardType;
import cz.GravelCZLP.WynncraftAPI.PlayerStats;
import cz.GravelCZLP.WynncraftAPI.PlayerStats.ClassStats;
import cz.GravelCZLP.WynncraftAPI.TimeFrame;
import cz.GravelCZLP.WynncraftAPI.WynncraftAPI;
import cz.GravelCZLP.WynncraftAPI.Stats.GuildLeaderboardInfo;
import cz.GravelCZLP.WynncraftAPI.Stats.LeaderboardInfo;
import cz.GravelCZLP.WynncraftAPI.Stats.PlayerLeaderboardInfo;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class WynncraftAPICommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser sender, IGuild guild, String content, String[] args) {
		if (args.length == 0) {
			// TODO: send help
			return;
		}
		EmbedBuilder b = new EmbedBuilder();
		b.withColor(0, 255, 0);
		b.withAuthorName("GravelBot by GravelCZLP");
		b.withAuthorIcon("https://i.imgur.com/6KakBeM.png");
		b.withFooterText("GravelCZLP - Author; Bot writren in Java; API is Discord4J; v" + Utils.getVersion());
		b.withFooterIcon("https://i.imgur.com/MraElzj.png");
		if (args[0].equalsIgnoreCase("guildStats")) {
			if (args.length < 1) {
				sendMessage(channel, "You need to set the guild name.");
				return;
			}
			String name = args[1];
			for (int i = 1; i < args.length; i++) {
				if (name == null) {
					name = args[i];
				} else {
					name += args[i];
				}
			}
			GuildStats gs = WynncraftAPI.getGuildStats(name);
			if (gs.isError()) {
				b.withTitle("Error");
				b.appendDescription("Error: " + gs.getName());
			} else {
				b.withTitle(gs.getName());
				b.appendField("Prefix:", gs.getPrefix(), true);
				b.appendField("XP:", String.valueOf(gs.getXp()), true);
				b.appendField("Level:", String.valueOf(gs.getLevel()), true);
				b.appendField("Territories", String.valueOf(gs.getTerritories()), true);
				b.appendField("Number of members:", String.valueOf(gs.getMembers().length), true);
				Member owner = null;
				for (Member m : gs.getMembers()) {
					if (m.getRank().equalsIgnoreCase("OWNER")) {
						owner = m;
					}
				}
				if (owner != null) {
					b.appendField("Owner:", owner.getName(), true);
				}
				sendMessage(channel, b.build());
			}

		} else if (args[0].equalsIgnoreCase("playerInfo")) {
			if (args.length < 1) {
				sendMessage(channel, "You need to set the player name.");
				return;
			}
			if (args.length >= 1) {
				PlayerStats ps = WynncraftAPI.getPlayerStats(args[1]);
				if (args.length == 2) {
					b.withTitle(ps.getUsername());
					b.withDescription("Info about player: " + ps.getUsername());
					b.appendField("UUID:", ps.getUuid(), false);
					b.appendField("Rank", ps.getRank(), true);
					b.appendField("Tag", ps.getTag().isEmpty() ? "None" : ps.getTag(), true);
					b.appendField("Veteran", ps.isVeteran() ? "Yes" : "No", true);
					if (ps.getCurrentServer() != null && !ps.getCurrentServer().isEmpty()) {
						b.appendField("Current server", ps.getCurrentServer(), false);
					}
					b.appendField("Guild:", ps.getGuild().getName(), true);
					b.appendField("Guild Rank:", ps.getGuild().getRank(), true);
					sendMessage(channel, b.build());
					return;
				} else if (args.length >= 3) {
					if (args[2].equalsIgnoreCase("global")) {
						b.withDescription("Global stats");
						b.appendField("Mobs Killed", String.valueOf(ps.getGlobalPlayerStats().getMobsKilled()), true);
						b.appendField("PVP Kills", String.valueOf(ps.getGlobalPlayerStats().getPvpKills()), true);
						b.appendField("PVP Deaths", String.valueOf(ps.getGlobalPlayerStats().getPvpDeaths()), true);
						b.appendField("Chests found", String.valueOf(ps.getGlobalPlayerStats().getChestsFound()), true);
						b.appendField("Blocks walked", String.valueOf(ps.getGlobalPlayerStats().getBlocksWalked()),
								true);
						b.appendField("Logins", String.valueOf(ps.getGlobalPlayerStats().getLogins()), true);
						b.appendField("Deaths", String.valueOf(ps.getGlobalPlayerStats().getDeaths()), true);
						b.appendField("Total level", String.valueOf(ps.getGlobalPlayerStats().getTotalLevel()), true);
						sendMessage(channel, b.build());
						return;
					} else if (args[2].equalsIgnoreCase("class")) {
						if (args.length <= 2) {
							return;
						}
						List<ClassStats> cs = ps.getPlayerClassStats();
						if (args[3].equalsIgnoreCase("archer")) {
							ClassStats csss = null;
							for (ClassStats css : cs) {
								if (css.getName().equals("archer")) {
									csss = css;
									break;
								}
							}
							if (csss != null) {
								b.withDescription("Archer stats");
								b.appendField("Level", String.valueOf(csss.getLevel()), true);
								b.appendField("XP", String.valueOf(csss.getXp()), true);
								b.appendField("Mobs Killed", String.valueOf(csss.getMobsKilled()), true);
								b.appendField("PVP Kills", String.valueOf(csss.getPvpKills()), true);
								b.appendField("PVP Deaths", String.valueOf(csss.getPvpDeaths()), true);
								b.appendField("Chests found", String.valueOf(csss.getChestsFound()), true);
								b.appendField("Blocks walked", String.valueOf(csss.getBlocksWaled()), true);
								b.appendField("Logins", String.valueOf(csss.getBlocksWaled()), true);
								b.appendField("Deaths", String.valueOf(csss.getDeaths()), true);
								b.appendField("Total level", String.valueOf(ps.getGlobalPlayerStats().getTotalLevel()),
										true);
							}
						} else if (args[3].equalsIgnoreCase("mage")) {
							ClassStats csss = null;
							for (ClassStats css : cs) {
								if (css.getName().equals("mage")) {
									csss = css;
									break;
								}
							}
							if (csss != null) {
								b.withDescription("Archer stats");
								b.appendField("Level", String.valueOf(csss.getLevel()), true);
								b.appendField("XP", String.valueOf(csss.getXp()), true);
								b.appendField("Mobs Killed", String.valueOf(csss.getMobsKilled()), true);
								b.appendField("PVP Kills", String.valueOf(csss.getPvpKills()), true);
								b.appendField("PVP Deaths", String.valueOf(csss.getPvpDeaths()), true);
								b.appendField("Chests found", String.valueOf(csss.getChestsFound()), true);
								b.appendField("Blocks walked", String.valueOf(csss.getBlocksWaled()), true);
								b.appendField("Logins", String.valueOf(csss.getBlocksWaled()), true);
								b.appendField("Deaths", String.valueOf(csss.getDeaths()), true);
								b.appendField("Total level", String.valueOf(ps.getGlobalPlayerStats().getTotalLevel()),
										true);
							}
						} else if (args[3].equalsIgnoreCase("warrior")) {
							ClassStats csss = null;
							for (ClassStats css : cs) {
								if (css.getName().equals("warrior")) {
									csss = css;
									break;
								}
							}
							if (csss != null) {
								b.withDescription("Archer stats");
								b.appendField("Level", String.valueOf(csss.getLevel()), true);
								b.appendField("XP", String.valueOf(csss.getXp()), true);
								b.appendField("Mobs Killed", String.valueOf(csss.getMobsKilled()), true);
								b.appendField("PVP Kills", String.valueOf(csss.getPvpKills()), true);
								b.appendField("PVP Deaths", String.valueOf(csss.getPvpDeaths()), true);
								b.appendField("Chests found", String.valueOf(csss.getChestsFound()), true);
								b.appendField("Blocks walked", String.valueOf(csss.getBlocksWaled()), true);
								b.appendField("Logins", String.valueOf(csss.getBlocksWaled()), true);
								b.appendField("Deaths", String.valueOf(csss.getDeaths()), true);
								b.appendField("Total level", String.valueOf(ps.getGlobalPlayerStats().getTotalLevel()),
										true);
							}
						} else if (args[3].equalsIgnoreCase("assassin")) {
							ClassStats csss = null;
							for (ClassStats css : cs) {
								if (css.getName().equals("assassin")) {
									csss = css;
									break;
								}
							}
							if (csss != null) {
								b.withDescription("Archer stats");
								b.appendField("Level", String.valueOf(csss.getLevel()), true);
								b.appendField("XP", String.valueOf(csss.getXp()), true);
								b.appendField("Mobs Killed", String.valueOf(csss.getMobsKilled()), true);
								b.appendField("PVP Kills", String.valueOf(csss.getPvpKills()), true);
								b.appendField("PVP Deaths", String.valueOf(csss.getPvpDeaths()), true);
								b.appendField("Chests found", String.valueOf(csss.getChestsFound()), true);
								b.appendField("Blocks walked", String.valueOf(csss.getBlocksWaled()), true);
								b.appendField("Logins", String.valueOf(csss.getBlocksWaled()), true);
								b.appendField("Deaths", String.valueOf(csss.getDeaths()), true);
								b.appendField("Total level", String.valueOf(ps.getGlobalPlayerStats().getTotalLevel()),
										true);
							}
						} else {
							sendMessage(channel, "Class not found");
						}
						return;
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("playersOnline")) {
			HashMap<String, List<String>> players = WynncraftAPI.getOnlinePlayers();
			Iterator<String> s = players.keySet().iterator();
			while (s.hasNext()) {
				String key = s.next();
				if (players.get(key) == null || players.get(key).isEmpty()) {
					continue;
				}
				b.appendField(key, String.valueOf(players.get(key).size()), false);
			}
			sendMessage(channel, b.build());
		} else if (args[0].equalsIgnoreCase("playersOnlineSum")) {
			sendMessage(channel, "Players online: " + WynncraftAPI.getOnlinePlayerSum());
		} else if (args[0].equalsIgnoreCase("stats")) {
			if (args.length >= 2) {
				LeaderboardType type = null;
				TimeFrame tf = null;
				try {
					type = LeaderboardType.valueOf(new String(args[1]));
				} catch (Exception e) {
					sendMessage(channel, "Invalid leaderboard type: GUILD, PLAYER, PVP");
				}
				try {
					tf = TimeFrame.valueOf(new String(args[2]));
				} catch (Exception e) {
					sendMessage(channel, "Invalid time frae type: ALLTIME, WEEK");
				}
				if (type != LeaderboardType.PVP && tf == TimeFrame.WEEK) {
					sendMessage(channel, "Time frame week only valid if Leaderboard type is PVP");
					return;
				}
				List<LeaderboardInfo> info = WynncraftAPI.queryScoreboard(LeaderboardType.valueOf(content),
						TimeFrame.WEEK);
				if (type == LeaderboardType.GUILD) {
					List<GuildLeaderboardInfo> newInfo = new ArrayList<>();
					for (LeaderboardInfo li : info) {
						GuildLeaderboardInfo gli = (GuildLeaderboardInfo) li;
						if (gli.getNumerOnLeaderboard() == 1 || gli.getNumerOnLeaderboard() == 2 || gli.getNumerOnLeaderboard() == 3) {
							newInfo.add(gli);
						}
					}
					for (int i = 0; i < 3; i++) {
						GuildLeaderboardInfo gli = newInfo.get(i);
						b.withTitle("Top 3 guilds");
						b.appendField(gli.getName() + ": " + gli.getNumerOnLeaderboard(), "Level: " + gli.getLevel() + " / XP: " + gli.getXp(), false);
					}
				} else if (type == LeaderboardType.PLAYER || type == LeaderboardType.PVP) {
					List<PlayerLeaderboardInfo> newInfo = new ArrayList<>();
					for (LeaderboardInfo li : info) {
						PlayerLeaderboardInfo gli = (PlayerLeaderboardInfo) li;
						if (gli.getNumerOnLeaderboard() == 1 || gli.getNumerOnLeaderboard() == 2 || gli.getNumerOnLeaderboard() == 3) {
							newInfo.add(gli);
						}
					}
					for (int i = 0; i < 3; i++) {
						PlayerLeaderboardInfo gli = newInfo.get(i);
						b.withTitle("Top 3 Players");
						b.appendField(gli.getName() + ": " + gli.getNumerOnLeaderboard(), "Level: " + gli.getLevel() + " / XP: " + gli.getXp(), false);
					}
				}
				sendMessage(channel, b.build());
			} else {
				sendMessage(channel, "Please specify Leaderboard type and time frame");
			}
		}
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}
}

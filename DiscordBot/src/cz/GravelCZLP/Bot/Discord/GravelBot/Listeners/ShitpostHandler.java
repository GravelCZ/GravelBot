package cz.GravelCZLP.Bot.Discord.GravelBot.Listeners;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import cz.GravelCZLP.Bot.Main.Main;
import cz.GravelCZLP.Bot.Utils.Logger;
import sx.blah.discord.handle.obj.IGuild;

public class ShitpostHandler {

	private static List<String> blockedGuilds = new ArrayList<>();
	
	public static boolean isBlocked(IGuild g) {
		return blockedGuilds.contains(g.getName() + ":" + g.getStringID());
	}
	
	public static void unblock(IGuild g) {
		blockedGuilds.remove(g.getName() + ":" + g.getStringID());
	}
	
	public static void block(IGuild g) {
		blockedGuilds.add(g.getName() + ":" + g.getStringID());
	}
	
	public static void load() throws Exception {
		PreparedStatement ps = Main.getDBManager().getSource().getConnection().prepareStatement("SELECT * FROM blockedGuilds");
		ResultSet rs = ps.executeQuery();
		int size = 0;
		while (rs.next()) {
			size++;
			blockedGuilds.add(rs.getString("guildName") + ":" + rs.getInt("guildId"));
		}
		Logger.log("Shitpost blocked guilds loaded: " + size);
	}
	
	public static void save() throws Exception {
		Main.getDBManager().getSource().getConnection().createStatement().execute("truncate table blockedGuilds;");
		for (String s : blockedGuilds) {
			String[] sp = s.split(":");
			PreparedStatement ps = Main.getDBManager().getSource().getConnection().prepareStatement("INSERT INTO blockedGuilds(guildName, guildId) VALUES(?,?);");
			ps.setString(1, sp[0]);
			ps.setInt(2, Integer.valueOf(sp[1]));
			ps.execute();
		}
		Logger.log("Saved shitpost blocked Guilds.");
	}
}

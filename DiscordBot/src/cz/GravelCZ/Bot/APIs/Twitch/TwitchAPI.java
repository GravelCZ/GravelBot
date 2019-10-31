package cz.GravelCZ.Bot.APIs.Twitch;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

import cz.GravelCZ.Bot.APIs.IAPI;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.Utils;

public class TwitchAPI implements IAPI {

	private static String token;

	private static int expiresIn;
	private static long lastLogin;

	public static String getUserInfo(String name) {
		if (System.currentTimeMillis() > (expiresIn * 1000 + lastLogin)) {
			auth();
		}
		HashMap<String, String> headers = new HashMap<>();

		headers.put("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0");
		headers.put("Client-ID", Constants.twitchId);
		headers.put("Authorization", "Bearer " + token);
		headers.put("Accept", "application/vnd.twitchtv.v5+json");

		try {
			return Utils.makeUrlGetRequest(new URL("https://api.twitch.tv/helix/users?login=" + name), headers, false).getKey();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUserStream(String name) {
		HashMap<String, String> headers = new HashMap<>();

		headers.put("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0");
		headers.put("Client-ID", Constants.twitchId);
		try {
			return Utils.makeUrlGetRequest(new URL("https://api.twitch.tv/kraken/streams/" + name), headers, false).getKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void auth() {
		lastLogin = System.currentTimeMillis();

		HashMap<String, String> headers = new HashMap<>();

		headers.put("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0");

		try {
			String out = Utils
					.makeUrlPostRequest(
							new URL("https://id.twitch.tv/oauth2/token?client_id=" + Constants.twitchId
									+ "&client_secret=" + Constants.twitchSecret + "&grant_type=client_credentials"),
							headers, null).getKey();

			JSONObject obj = new JSONObject(out);
			token = obj.getString("access_token");
			expiresIn = obj.getInt("expires_in");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Twitch";
	}

}

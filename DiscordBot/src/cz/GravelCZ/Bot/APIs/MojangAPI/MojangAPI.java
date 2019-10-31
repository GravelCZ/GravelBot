package cz.GravelCZ.Bot.APIs.MojangAPI;

import java.net.URL;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZ.Bot.APIs.IAPI;
import cz.GravelCZ.Bot.Utils.Pair;
import cz.GravelCZ.Bot.Utils.Utils;

public class MojangAPI implements IAPI {

	private static String statusUrl = "https://status.mojang.com/check";
	private static String nameToUUID = "https://api.mojang.com/users/profiles/minecraft/%NAME%";
	private static String profileInfo = "https://sessionserver.mojang.com/session/minecraft/profile/%UUID%";
	private static String nameHistory = "https://api.mojang.com/user/profiles/%UUID%/names";
	
	public static EnumMap<MojangService, Status> getStatus() {
		EnumMap<MojangService, Status> map = new EnumMap<>(MojangService.class);
		try {
			String out = Utils.makeUrlGetRequest(new URL(statusUrl), new HashMap<>(), false).getKey();
			JSONArray array = new JSONArray(out);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Iterator<String> keys = obj.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					String value = obj.getString(key);
					MojangService ms = MojangService.fromKey(key);
					Status s = Status.valueOf(value.toUpperCase());
					map.put(ms, s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static Pair<String[], Integer> getPlayerNameHistory(String uuid) {
		try {
			Pair<String, Integer> r = Utils.makeUrlGetRequest(new URL(nameHistory.replaceFirst("%UUID%", uuid)), new HashMap<>(), false);
			if (r.getValue() != 200) {
				return Pair.of(null, r.getValue());
			}
			String response = r.getKey();
			JSONArray list = new JSONArray(response);
			String[] names = new String[list.length()];
			for (int i = 0; i < list.length(); i++) {
				JSONObject obj = list.getJSONObject(i);
				names[i] = obj.getString("name");
			}
			return Pair.of(names, r.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Pair<String, Integer> getSkinUrl(String uuid) {
		try {
			Pair<String, Integer> r = Utils.makeUrlGetRequest(new URL(profileInfo.replaceFirst("%UUID%", uuid)), new HashMap<>(), false);
			if (r.getValue() != 200) {
				return Pair.of("", r.getValue());
			}
			String resp = r.getKey();
			JSONObject obj = new JSONObject(resp);
			JSONArray props = obj.getJSONArray("properties");
			for (int i = 0; i < props.length(); i++) {
				JSONObject aobj = props.getJSONObject(i);
				if (aobj.getString("name").equals("textures")) {
					String value = aobj.getString("value");
					JSONObject decoded = new JSONObject(new String(Base64.getDecoder().decode(value.getBytes("UTF-8"))));
					return Pair.of(decoded.getJSONObject("textures").getJSONObject("SKIN").getString("url"), r.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] nameToUUID(String name) {
		try {
			HttpsURLConnection conn = Utils.rawConnection("GET", new URL(nameToUUID.replaceFirst("%NAME%", name)), new HashMap<>());
			if (conn.getResponseCode() == 204) {
				return new String[] { "1", "null" };
			}
			String read = Utils.readInputStream(conn.getInputStream(), false);
			JSONObject obj = new JSONObject(read);
			return new String[] { obj.getString("id"), obj.getString("name") };
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getName() {
		return "Mojang";
	}
	
	public static enum Status {
		GREEN, YELLOW, RED;
	}
	
	public static enum MojangService {
		
		MINECRAFT_NET("minecraft.net", "Minecraft"),
		MINECRAFT_SESSION("session.minecraft.net", "Minecraft Session Server"),
		MOJANG_ACCOUNT("account.mojang.com", "Mojang account Service"),
		MOJANG_AUTHSERVER("authserver.mojang.com", "Mojang Authentication Service"),
		MOJANG_SESSIONSERVER("sessionserver.mojang.com", "Mojang Session Server"),
		MOJANG_API("api.mojang.com", "Mojang API"),
		MINECRAFT_TEXTURES("textures.minecraft.net", "Minecraft Textures"),
		MOJANG("mojang.com", "Mojang");
		
		public String name;
		public String key;
		
		private MojangService(String key, String name) {
			this.key = key;
			this.name = name;
		}
		
		public static MojangService fromKey(String key) {
			for (MojangService ms : MojangService.values()) {
				if (ms.key == key) {
					return ms;
				}
			}
			return null;
		}
	}
}

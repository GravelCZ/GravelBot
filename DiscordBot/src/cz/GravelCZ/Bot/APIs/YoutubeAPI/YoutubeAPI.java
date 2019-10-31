package cz.GravelCZ.Bot.APIs.YoutubeAPI;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZ.Bot.APIs.IAPI;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.Pair;
import cz.GravelCZ.Bot.Utils.Utils;

public class YoutubeAPI implements IAPI {

	private static final String videoInfoUrl = "https://www.googleapis.com/youtube/v3/videos?part=id%2C+snippet&id=%ID%&key=%KEY%";
	private static final String videoSearchUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=%Q%&key=%KEY%";
	private static final String playlistIinfoUrl = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&id=%ID%&key=%KEY%";
	
	public static Pair<Optional<String>, Integer> search(String input) throws Exception {
		String inputEnc = URLEncoder.encode(input, "UTF-8");
		
		URL url = new URL(videoSearchUrl.replaceFirst("%Q%", inputEnc).replaceFirst("%KEY%", Constants.youtubeApiKey));
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		
		Pair<String, Integer> response = Utils.makeUrlGetRequest(url, headers, true);
		if (response.getValue() != 200) {
			return Pair.of(Optional.empty(), response.getValue());
		}
		
		JSONObject responseObj = new JSONObject(response.getKey());
		JSONArray items = responseObj.getJSONArray("items");
		String videoId = "";
		for (int i = 0; i < items.length(); i++) {
			JSONObject obj = items.getJSONObject(i);
			JSONObject idObj = obj.getJSONObject("id");
			if (idObj.getString("kind").equals("youtube#video")) {
				videoId = idObj.getString("videoId");
				break;
			}
		}
		return Pair.of(Optional.of(videoId), response.getValue());
	}
	
	public static Info getPlaylistInfo(String id) throws Exception {
		URL url = new URL(playlistIinfoUrl.replaceFirst("%ID%", id).replaceFirst("%KEY%", Constants.youtubeApiKey));
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		
		Pair<String, Integer> response = Utils.makeUrlGetRequest(url, headers, true);
		if (response.getValue() != 200) {
			return null;
		}
		
		JSONObject responseObj = new JSONObject(response.getKey());
		JSONObject snippet = responseObj.getJSONArray("items").getJSONObject(0).getJSONObject("snippet");
		
		Info info = new Info();
		info.title = snippet.getString("title");
		info.thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
		info.channelName = snippet.getString("channelTitle");
		
		return info;
	}
	
	public static Info getVideoInfo(String id) throws Exception {
		URL url = new URL(videoInfoUrl.replaceFirst("%ID%", id).replaceFirst("%KEY%", Constants.youtubeApiKey));
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		
		Pair<String, Integer> response = Utils.makeUrlGetRequest(url, headers, true);
		if (response.getValue() != 200) {
			return null;
		}
		
		JSONObject responseObj = new JSONObject(response.getKey());
		JSONObject snippet = responseObj.getJSONArray("items").getJSONObject(0).getJSONObject("snippet");
		
		Info info = new Info();
		info.title = snippet.getString("title");
		info.thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
		info.channelName = snippet.getString("channelTitle");
		return info;
	}
	
	@Override
	public String getName() {
		return "YouTube";
	}
	
	public static class Info {
		
		public String title;
		public String thumbnail;
		public String channelName;
		
	}
}

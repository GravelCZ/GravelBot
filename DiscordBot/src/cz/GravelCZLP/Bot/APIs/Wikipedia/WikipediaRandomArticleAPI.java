package cz.GravelCZLP.Bot.APIs.Wikipedia;

import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZLP.Bot.APIs.IAPI;
import cz.GravelCZLP.Bot.Utils.Utils;

public class WikipediaRandomArticleAPI implements IAPI {

	private static String randomArticleUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=random&rnnamespace=0&rnlimit=1";
	private static String pageIdToUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=info&pageids=%ID%&inprop=url&format=json";
	
	public static String getRandomArticleUrl() {
		try {
			String returnedString = Utils.makeUrlGetRequest(new URL(randomArticleUrl), new HashMap<String, String>(), false).getKey();
			
			JSONObject returnedObject = new JSONObject(returnedString);
			
			JSONObject query = returnedObject.getJSONObject("query");
			JSONArray randomList = query.getJSONArray("random");
			JSONObject firstRandom = randomList.getJSONObject(0);
			
			int id = firstRandom.getInt("id");
			
			String queryObjectString = Utils.makeUrlGetRequest(new URL(pageIdToUrl.replaceAll("%ID%", String.valueOf(id))), new HashMap<String, String>(), false).getKey();
			JSONObject urlQueryObject = new JSONObject(queryObjectString); 
			JSONObject urlQuery = urlQueryObject.getJSONObject("query");
			JSONObject pages = urlQuery.getJSONObject("pages");
			JSONObject page = pages.getJSONObject(String.valueOf(id));
			return page.getString("fullurl");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getName() {
		return "Wikipedia";
	}
	
}

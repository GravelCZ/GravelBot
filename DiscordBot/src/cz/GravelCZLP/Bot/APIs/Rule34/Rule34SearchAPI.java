package cz.GravelCZLP.Bot.APIs.Rule34;

import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.XML;

import cz.GravelCZLP.Bot.APIs.IAPI;
import cz.GravelCZLP.Bot.Utils.Utils;

public class Rule34SearchAPI implements IAPI {

	private static String queryUrl = "https://rule34.xxx/index.php?page=dapi&s=post&q=index&limit=100&tags=%TAGS%&rating:explicit";

	public static String query(String tags) {
		tags = tags.replaceAll(" ", "+");

		try {
			String rawXML = Utils.makeUrlGetRequest(new URL(queryUrl.replaceAll("%TAGS%", tags)), new HashMap<String, String>(), false);
			JSONObject obj = XML.toJSONObject(rawXML);
			return obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getName() {
		return "Rule34";
	}

}

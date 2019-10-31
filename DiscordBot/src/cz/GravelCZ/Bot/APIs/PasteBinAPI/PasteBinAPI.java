package cz.GravelCZ.Bot.APIs.PasteBinAPI;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import cz.GravelCZ.Bot.APIs.IAPI;
import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.Utils;

public class PasteBinAPI implements IAPI {

	public static String paste(String name, String content, boolean guest) {
		String userKey = null;
		if (!guest) {
			userKey = authPastebin();
		}
		try {
			String url = "https://pastebin.com/api/api_post.php";
			String data = "api_option=paste" 
					+ (userKey != null ? "&api_user_key=" + userKey : "")
					+ "&api_paste_private=0"
					+ "&api_paste_name=" + URLEncoder.encode(name, "UTF-8")
					+ "&api_paste_expire_date=1H"
					+ "&api_paste_format=text"
					+ "&api_dev_key=" + Constants.pastebinKey
					+ "&api_paste_code=" + URLEncoder.encode(content, "UTF-8");
			HashMap<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			return Utils.makeUrlPostRequest(new URL(url), headers, data).getKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String authPastebin() {
		try {
			String url = "https://pastebin.com/api/api_login.php";
			String data = "api_dev_key=" + Constants.pastebinKey
					+ "&api_user_name=" + URLEncoder.encode(Constants.pastebinName, "UTF-8")
					+ "&api_user_password=" + URLEncoder.encode(Constants.pastebinPassword, "UTF-8");
			HashMap<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			return Utils.makeUrlPostRequest(new URL(url), headers, data).getKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getName() {
		return "Pastebin";
	}
	
}

package cz.GravelCZ.Bot.APIs.Imgur;

import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import cz.GravelCZ.Bot.Main.Constants;
import cz.GravelCZ.Bot.Utils.Logger;
import cz.GravelCZ.Bot.Utils.Pair;
import cz.GravelCZ.Bot.Utils.Utils;

public class ImgurAPI {
	
	public static String[] upload(byte[] base64imagedata, String title) {
		try {
			HashMap<String, String> headers = new HashMap<>();
			
			headers.put("Authorization", "Client-ID " + Constants.imgurClientId);
			headers.put("Content-Type", "application/x-www-form-urlencoded");
			
			String randomWebFormBound = "------WebKitFormBoundary" + Utils.getRandomString(15);
			
			StringBuffer finalData = new StringBuffer();
			
			finalData.append(randomWebFormBound + "\r\n");
			finalData.append("Content-Disposition: form-data; name=\"image\"\r\n\r\n");
			finalData.append(Base64.getEncoder().encodeToString(base64imagedata) + "\r\n");
			finalData.append(randomWebFormBound + "\r\n");
			finalData.append("Content-Disposition: form-data; name=\"title\"\r\n\r\n");
			finalData.append(title + "\r\n");
			finalData.append(randomWebFormBound + "--");
			
			Pair<String, Integer> response = Utils.makeUrlPostRequest(new URL("https://api.imgur.com/3/image?type=base64"), headers, Base64.getEncoder().encodeToString(base64imagedata));
			
			if (response.getValue() != 200) {
				throw new Exception("Got response code: " + response.getValue());
			}
			
			JSONObject obj = new JSONObject(response.getKey());
			JSONObject dataObj = obj.getJSONObject("data");
			String[] returnData = new String[5];
			returnData[0] = dataObj.getString("id");
			returnData[1] = dataObj.getString("deletehash");
			returnData[2] = dataObj.getString("link");
			returnData[3] = dataObj.getString("type");
			
			return returnData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void delete(String delHash) {
		try {
			HashMap<String, String> headers = new HashMap<>();
			
			headers.put("Authorization", "Client-ID " + Constants.imgurClientId);
			
			HttpsURLConnection conn = Utils.rawConnection("DELETE", new URL("https://api.imgur.com/3/image/" + delHash), headers);
			
			if (conn.getResponseCode() != 200) {
				Logger.log("Delete hash: " + delHash + " redturned: " + conn.getResponseCode());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

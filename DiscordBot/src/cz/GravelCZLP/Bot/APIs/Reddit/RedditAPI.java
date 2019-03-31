package cz.GravelCZLP.Bot.APIs.Reddit;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZLP.Bot.APIs.IAPI;
import cz.GravelCZLP.Bot.Main.Constants;
import cz.GravelCZLP.Bot.Utils.Utils;

public class RedditAPI implements IAPI {

	private long expiredAfter = 0;

	private String token = "";

	private final String userAgent = "GravelBot v" + Utils.getVersion() + " (Linux x68_64; by /u/GravelCZLP)";
	
	public boolean isTokenExpired() {
		return System.currentTimeMillis() > expiredAfter;
	}

	public String getToken() {
		return token;
	}

	public List<RedditPost> getPosts() {
		if (isTokenExpired()) {
			auth();
		}
		try {
			URL urlDankmemes = new URL("https://oauth.reddit.com/r/dankmemes/new?limit=15");
			URL urlDfm = new URL("https://oauth.reddit.com/r/deepfriedmemes/hot?limit=5");
			
			List<RedditPost> dankmemes = getPostsfromUrl(urlDankmemes);
			List<RedditPost> dfm = getPostsfromUrl(urlDfm);
			
			dankmemes.add(dfm.get(Utils.getRandom().nextInt(dfm.size())));
			
			return dankmemes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private List<RedditPost> getPostsfromUrl(URL url) {
		try {
			List<RedditPost> posts = new ArrayList<>();

			String out = makeRedditRequest(url);
			
			JSONObject obj = new JSONObject(out);
			JSONObject data = obj.getJSONObject("data");
			
			JSONArray children = data.getJSONArray("children");

			int lenght = children.length();
			if (lenght > 15) {
				lenght = 15;
			}
			
			for (int i = 0; i < lenght; i++) {
				JSONObject post = children.getJSONObject(i);

				if (!post.getString("kind").equals("t3")) {
					continue;
				}

				JSONObject postData = post.getJSONObject("data");
				if (postData.getBoolean("is_video")) {
					continue;
				}
				if (postData.has("post_hint")) {
					if (!postData.getString("post_hint").equals("image")) {
						continue;
					}
				} else {
					continue;
				}

				String subreddit = postData.getString("subreddit_name_prefixed");
				String title = postData.getString("title");
				String author = postData.getString("author");
				String permalink = postData.getString("permalink");
				String imageUrl = postData.getString("url");
				boolean nsfw = postData.getBoolean("over_18");
				boolean spoiler = postData.getBoolean("spoiler");

				int code = Utils.getResponseCode(new URL(imageUrl), new HashMap<>());
				
				if (code == 200) {
					posts.add(new RedditPost(author, nsfw, spoiler, title, imageUrl, permalink, subreddit));	
				}
			}
			return posts;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String makeRedditRequest(URL url) {
		try {

			HashMap<String, String> headers = new HashMap<>();
			
			headers.put("User-Agent", userAgent);
			headers.put("Authorization", "bearer " + getToken());
			
			return Utils.makeUrlGetRequest(url, headers, false);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void auth() {
		try {
			String data = "grant_type=password&username=" + Constants.redditUsername + "&password=" + Constants.redditPassword; // DO NOT URL ENCODE IT DOES NOT LIKE THAT !
			String login = Constants.redditAppId + ":" + Constants.redditAppSecret;
			
			String encodedLogin = Base64.getEncoder().encodeToString(login.getBytes("UTF-8"));
			
			HashMap<String, String> headers = new HashMap<>();
			
			headers.put("User-Agent", userAgent);
			headers.put("Content-Type", "application/x-www-form-urlencoded");
			headers.put("Content-Length", String.valueOf(data.length()));
			headers.put("Authorization", "Basic " + encodedLogin);
			
			String out = Utils.makeUrlPostRequest(new URL("https://www.reddit.com/api/v1/access_token"), headers, data);

			JSONObject obj = new JSONObject(out);

			String token = obj.getString("access_token");

			int expires = obj.getInt("expires_in");
			expiredAfter = System.currentTimeMillis() + (expires - 500) * 1000;

			this.token = token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	@Override
	public String getName() {
		return "Reddit";
	}
	
	public class RedditPost {

		private String user;
		private boolean nsfw;
		private boolean spoiler;
		private String title;
		private String imageUrl;
		private String permalinkUrl;
		private String subreddit;

		public RedditPost(String user, boolean nsfw, boolean spoiler, String title, String imageUrl,
				String permalinkUrl, String subreddit) {
			this.user = user;
			this.nsfw = nsfw;
			this.spoiler = spoiler;
			this.title = title;
			this.imageUrl = imageUrl;
			this.permalinkUrl = permalinkUrl;
			this.subreddit = subreddit;
		}

		public String getUser() {
			return user;
		}

		public boolean isNSFW() {
			return nsfw;
		}

		public boolean isSpoiler() {
			return spoiler;
		}

		public String getTitle() {
			return title;
		}

		public String getImageURL() {
			return imageUrl;
		}

		public String getPermalinkURL() {
			return permalinkUrl;
		}

		public String getSubReddit() {
			return subreddit;
		}
	}

}

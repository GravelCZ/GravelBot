package cz.GravelCZLP.Bot.Discord.GravelBot.Commands.PrivateCommands;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.GravelCZLP.Bot.APIs.Rule34.Rule34SearchAPI;
import cz.GravelCZLP.Bot.Discord.GravelBot.Commands.ICommand;
import cz.GravelCZLP.Bot.Utils.Logger;
import cz.GravelCZLP.Bot.Utils.Utils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class Rule34SearchCommand implements ICommand {

	@Override
	public void execute(IMessage msg, IChannel channel, IUser user, IGuild guild, String content, String[] args) {
		if (args.length == 0) {
			sendMessage(channel, "Usage: !/rule34 [tags]");
			return;
		}
		String tags = "";
		for (int i = 0; i < args.length; i++) {
			if (tags.isEmpty()) {
				tags += args[i];
			} else {
				tags += "+" + args[i];
			}
		}

		Logger.rule34log("Rule34 Query: User: " + msg.getAuthor().getName() + " Tags: " + tags);

		String response = Rule34SearchAPI.query(tags); // query the XML and convert it to JSON text
		if (response == null) {
			sendMessage(channel, "An Internal error occured!");
			return;
		}
		JSONObject obj = new JSONObject(response); // Parse the text as JSON

		if (!obj.isNull("response")) {
			JSONObject jsonResponse = obj.getJSONObject("response"); // get the response in case of an error
			String reason = jsonResponse.getString("reason"); // get the reason
			sendMessage(channel, "I am sorry uwu i faweld... (Reason: " + reason + ")");
		}
		
		// We did NOT fail YEEEY !!
		JSONObject posts = obj.getJSONObject("posts"); // get the posts object
		if (posts.getInt("count") == 0) { // check if we dont have results
			sendMessage(channel, "No results found... not fap for you today my boy");
			return;
		}

		JSONArray postsArray = posts.getJSONArray("post"); // get the posts
		
		JSONObject randomPost = postsArray.getJSONObject(Utils.getRandom().nextInt(postsArray.length())); // pick a random one
		
		String url = randomPost.getString("file_url"); // get the url
		String tagsJson = randomPost.getString("tags"); //get the tags

		Logger.rule34log("Sending: " + url + " to user: " + user.getName() + " with tags: " + tagsJson);

		sendMessage(channel, "You fucking pervert. Here you go... \n" + url + " \n Tags: " + tagsJson);
	}

	@Override
	public boolean canExecute(IUser user, IGuild guild) {
		return true;
	}

}

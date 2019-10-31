package cz.GravelCZ.Bot.Utils;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;

public class EmbedUtils {

	public static EmbedBuilder newBuilder()
	{
		EmbedBuilder b = new EmbedBuilder();
		b.setColor(new Color(0, 255, 0));
		b.setTitle("Hello o/");
		b.setAuthor("GravelBot by GravelCZ", "https://gravel.wetian.eu", "https://i.imgur.com/ZLbudIA.jpg");
		b.setFooter("GravelCZ - Author | Bot writren in Java | API is Discord4J | v" + Utils.getVersion(), "https://i.imgur.com/MraElzj.png");
		
		return b;
	}
	
}

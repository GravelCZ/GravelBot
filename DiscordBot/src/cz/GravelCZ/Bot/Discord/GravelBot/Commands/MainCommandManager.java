package cz.GravelCZ.Bot.Discord.GravelBot.Commands;

import java.util.HashMap;

import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands.DeleteAllCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands.GetMyIDCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands.PrivateHelpCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands.Rule34SearchCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands.Admin.DeleteCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.PrivateCommands.Admin.SetPFPCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.BestGirlCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.DeepFryerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.FlipACoinCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.LoveCalculatorCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.McInfoCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MojangAPICommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.RandomWikipedaArticle;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.ReactOOFCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.ServerHelpCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.UptimeCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands.LeaveGuildCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands.NoShitPostCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.AdminCommands.ThanosSnapCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands.JoinCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands.LeaveCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands.PlayCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands.StopCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands.TwitchCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands.VolumeCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.MusicBotCommands.YoutubeCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer.AnswerCommand;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer.QAHandler;
import cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer.QuestionCommand;

public class MainCommandManager {

	private HashMap<String, IServerCommand> serverCommands = new HashMap<>(); 
	private HashMap<String, IPrivateCommand> privateCommands = new HashMap<>();

	public IPrivateCommand getPrivateCommand(String name) {
		return privateCommands.get(name);
	}
	
	public IServerCommand getServerCommand(String name) {
		return serverCommands.get(name);
	}
	
	public void init() {
		QAHandler handler = new QAHandler();
		handler.init();
		
		//commands for PM
		privateCommands.put("deleteall", new DeleteAllCommand());
		privateCommands.put("getmyid", new GetMyIDCommand());
		privateCommands.put("help", new PrivateHelpCommand());
		privateCommands.put("rule34", new Rule34SearchCommand());
		privateCommands.put("setpfp", new SetPFPCommand());
		privateCommands.put("deletestuff", new DeleteCommand());
		
		//commands for everyone
		serverCommands.put("flipacoin", new FlipACoinCommand());
		serverCommands.put("randomwikiarticle", new RandomWikipedaArticle());
		serverCommands.put("bestgirl", new BestGirlCommand());
		serverCommands.put("deepfry", new DeepFryerCommand());
		serverCommands.put("help", new ServerHelpCommand());
		
		serverCommands.put("mcinfo", new McInfoCommand());
		serverCommands.put("mojangapi", new MojangAPICommand());
		serverCommands.put("lovecalculator", new LoveCalculatorCommand());
		serverCommands.put("uptime", new UptimeCommand());
		serverCommands.put("oof", new ReactOOFCommand());
		//serverCommands.put("allemojis", new AllEmojisCommand());
		
		serverCommands.put("question", new QuestionCommand(handler));
		serverCommands.put("answer", new AnswerCommand(handler));
		
		//Admin commands
		//serverCommands.put("deleteall", new DeleteAllCommandServer());
		serverCommands.put("shitpost", new NoShitPostCommand());
		serverCommands.put("thanosnap", new ThanosSnapCommand());
		serverCommands.put("leaveguild", new LeaveGuildCommand());
		
		//MusicBot commands
		serverCommands.put("join", new JoinCommand());
		serverCommands.put("leave", new LeaveCommand());
		serverCommands.put("play", new PlayCommand());
		serverCommands.put("stop", new StopCommand());
		serverCommands.put("volume", new VolumeCommand());
		serverCommands.put("twitch", new TwitchCommand());
		serverCommands.put("youtube", new YoutubeCommand());
	}
	
}

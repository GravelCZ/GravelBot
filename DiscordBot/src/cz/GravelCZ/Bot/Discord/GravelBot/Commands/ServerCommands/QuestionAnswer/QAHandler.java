package cz.GravelCZ.Bot.Discord.GravelBot.Commands.ServerCommands.QuestionAnswer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import cz.GravelCZ.Bot.Utils.Logger;
import cz.GravelCZ.Bot.Utils.Utils;
import net.dv8tion.jda.api.entities.Member;

public class QAHandler {

	private List<QuestionAnswerPair> questions = new ArrayList<>();
	
	private HashMap<String, QuestionAnswerPair> userQA = new HashMap<>();
	
	public void init() {
		String[] qa = new String[0];
		try {
			qa = Utils.getLinesFromFile(new File("./BotDataFolder/questions.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < qa.length; i++) {
			String[] split = qa[i].split("`");
			String question = split[0];
			String[] answers = Arrays.copyOfRange(split, 1, split.length);
			questions.add(new QuestionAnswerPair(question, answers));
		}
		Logger.log("Loaded all questions! Avaiable questions: " + questions.size());
		Utils.setLoadedQuestions(questions.size());
	}
	
	public boolean isUserAnswering(Member user) {
		return userQA.get(user.getId()) != null ? true : false;
	}
	
	public void removeUser(Member user) {
		userQA.remove(user.getId());
	}
	
	public void setUserQA(Member user, QuestionAnswerPair qap) {
		userQA.put(user.getId(), qap);
	}
	
	public String[] getAnswers(Member user) {
		return userQA.get(user.getId()).getPossibleAnswers();
	}
	
	public Optional<Boolean> isAnswerRight(Member user, String answer) {
		QuestionAnswerPair pair = userQA.get(user.getId());
		if (pair == null) {
			return Optional.empty();
		}
		String[] possibleAnswers = pair.getPossibleAnswers();
		for (int i = 0; i < possibleAnswers.length; i++) {
			if (possibleAnswers[i].equalsIgnoreCase(answer)) {
				return Optional.of(Boolean.TRUE);
			}
		}
		return Optional.of(Boolean.FALSE);
	}
	
	public QuestionAnswerPair getRandomQuestion() {
		return questions.get(Utils.getRandom().nextInt(questions.size()));
	}
	
	public class QuestionAnswerPair {
		
		private String question;
		private String[] possibleAnswers;
		
		public QuestionAnswerPair(String question, String[] possibleAnswers) {
			this.question = question;
			this.possibleAnswers = possibleAnswers;
		}

		public String getQuestion() {
			return question;
		}

		public String[] getPossibleAnswers() {
			return possibleAnswers;
		}
	}
	
}

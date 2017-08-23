package cim.techmania.speecreco;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataHelper {
	public static Map<Question, String> qas = new HashMap<>();
	public static Map<String, Set> keywordToQ = new HashMap<>();

	public void prepareData() {
		qas.put(getQ("What is your name", 1), "My name is siri");
		qas.put(getQ("what is your age", 2), "My age is 25");
		qas.put(getQ("what time is it", 3), "3'o clock");
		qas.put(getQ("can you speak tamil", 4), "no only english");
		qas.put(getQ("are you married", 5), "Nope, I am single");
		qas.put(getQ("what do you do in your free time", 6), "I don not have any free time");
		qas.put(getQ("how do you do", 7), "Pleased to meet you");
		qas.put(getQ("how are you felling", 8), "Really awful");
		qas.put(getQ("how was your day", 9), "Really good");
		qas.put(getQ("can you give me a hand", 10), "Sorry I am a bit busy at the moment");
		qas.put(getQ("is everthing ok", 11), "Yes");
		qas.put(getQ("Are you happy today", 12), "Yes, I am");
		qas.put(getQ("Where are you from", 13), "I am from Seattle");
		qas.put(getQ("What is your phone number", 14), "209-786-9845");
		qas.put(getQ("Where were you born", 15), "I was born in 1995");
		qas.put(getQ("What is your job", 16), "I am a worker");
		qas.put(getQ("Can I try it on", 17), "sure");
		qas.put(getQ("May I open the window", 18), "no");
		qas.put(getQ("What is the weather like", 19), "It is raining at the moment");
		qas.put(getQ("Would you like some coffee", 20), "yes");
		qas.put(getQ("How was it", 21), "It was very interesting");
		qas.put(getQ("What shall we do this evening", 22), "Let's go see a film");
		qas.put(getQ("How are you", 23), "Fine, thanks. And you");
		qas.put(getQ("Can I try it on", 24), "sure");
		qas.put(getQ("How much does it cost", 25), "It is $45");
		qas.put(getQ("How would you like to pay", 26), "By cash");
		qas.put(getQ("Can I pay by cards ", 27), "We accept all the cades");
		qas.put(getQ("What do you like", 28), "I like foods");
		qas.put(getQ("hello",29), "hey sir");

	}

	private Question getQ(String question, int id) {
		return new Question(id, question);
	}

	public void prepareTagsToQ() {
		for (Map.Entry<Question, String> qas : qas.entrySet()) {
			String[] tags = qas.getKey().question.split(" ");
			for (String kw : tags) {
				Set matchedNodes = keywordToQ.get(kw.toUpperCase());
				if (matchedNodes == null) {
					matchedNodes = new HashSet<>();
				}
				matchedNodes.add(Integer.valueOf(qas.getKey().id));
				keywordToQ.put(kw.toUpperCase(), matchedNodes);
			}
		}

	}

	class Question {
		private int id;
		private String question;

		public Question(int id, String question) {
			this.id = id;
			this.question = question;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getQuestion() {
			return question;
		}

		public void setQuestion(String question) {
			this.question = question;
		}
	}
	static String findAnswer(Integer id) {
		for (Map.Entry<Question, String> qa : qas.entrySet()) {
			if(id.equals(qa.getKey().id)) {
				return qa.getValue();
			}
		}

		return null;
	}
}

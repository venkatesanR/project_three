package cim.techmania.speecreco;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QuestionFinder {
	private static Map<Integer, Long> scoreMap = new HashMap<>();

	public static void main(String[] args) {
		String question = "was hello";
		DataHelper helper = new DataHelper();
		helper.prepareData();
		helper.prepareTagsToQ();
		prepareScoreMap(question);
		System.out.println(helper.findAnswer(findQuestionId()));
	}

	private static void prepareScoreMap(String question) {
		String[] inputTags = question.split(" ");
		for (String iTag : inputTags) {
			Set<Integer> matchedSet = DataHelper.keywordToQ.get(iTag.toUpperCase());
			if (matchedSet != null && !matchedSet.isEmpty()) {
				for (Integer ques : matchedSet) {
					Long score = scoreMap.get(ques);
					if (score == null) {
						score = (long) 1;
					} else {
						score = score + 1;
					}
					scoreMap.put(ques, score);
				}
			}

		}
	}

	private static Integer findQuestionId() {
		Integer maxQID = 0;
		Long pscore = (long) 0;
		for (Map.Entry<Integer, Long> qa : scoreMap.entrySet()) {
			Long score = qa.getValue();
			if (score.longValue() > pscore) {
				maxQID = qa.getKey();
				pscore = score.longValue();
			}
		}

		return maxQID;
	}
}

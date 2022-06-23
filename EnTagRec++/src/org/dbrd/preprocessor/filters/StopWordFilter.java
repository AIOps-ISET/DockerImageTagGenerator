package org.dbrd.preprocessor.filters;


public class StopWordFilter extends AbstractWordFilter {

	private StopWordsSet stopWords = new StopWordsSet();
	
	@Override
	protected String[] filter(String word) {
		if (stopWords.isStopWord(word)) {
			return null;
		} else {
			return new String[] {word};
		}
	}

}

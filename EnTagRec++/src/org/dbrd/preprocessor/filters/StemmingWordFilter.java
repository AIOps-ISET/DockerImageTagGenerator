package org.dbrd.preprocessor.filters;



public class StemmingWordFilter extends AbstractWordFilter {

	@Override
	protected String[] filter(String word) {
		Stemmer stemmer = new Stemmer();
		stemmer.add(word.toCharArray(), word.length());
		stemmer.stem();
		return new String[] {stemmer.toString()};
	}

}

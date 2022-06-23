package org.dbrd.preprocessor.filters;

public class HTMLSymbolRemover extends AbstractWordFilter {

	@Override
	protected String[] filter(String word) {
		word = word.replaceAll("((&quot)|(&gt)|(&lt)|(&amp)|(&nbsp)|(&copy)|(&reg))+", " ");
		return new String[] {word};
	}

}

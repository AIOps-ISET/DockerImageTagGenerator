package org.dbrd.preprocessor.filters;

public class NonAlphabetOrNumberSymbolsRemover extends AbstractWordFilter {

	@Override
	protected String[] filter(String word) {
		return new String[] { word.replaceAll("\\W+", " ") };
	}

}

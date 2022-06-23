package org.dbrd.preprocessor.filters;

public class LowerCaseWordFilter extends AbstractWordFilter {

	@Override
	protected String[] filter(String word) {
		return new String[] { word.toLowerCase() };
	}

}

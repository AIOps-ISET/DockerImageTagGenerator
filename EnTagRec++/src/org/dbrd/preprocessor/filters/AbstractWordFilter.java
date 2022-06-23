package org.dbrd.preprocessor.filters;

public abstract class AbstractWordFilter {

	public final String[] process(String word) {
		String[] result = this.filter(word);
		return result == null ? new String[0] : result;
	}
	
	protected abstract String[] filter(String word);
}

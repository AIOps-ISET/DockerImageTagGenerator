package org.dbrd.preprocessor.filters;

public class TokenizerWordFilter extends AbstractWordFilter {

	//"(&)|" + 
	@Override
	protected String[] filter(String word) {
		return word.split("(`)|" + "(\\^)|" + "(\\*)|" + "(=)|" + "(\\+)|" + "(\\|)|" + "(\\\\)|" + "(\\[)|" + "(\\])|" + "(\\})|"
				+ "(\\{)|" + "(,)|" + "(!)|" + "(\\?)|" + "(#)|" + "(\\()|" + "(\\))|" + "(\")|" + "(\\>)|" + "(\\<)|" + "(~)|"
				+ "(;)|" + "(:)|" + "(\\.)|" +"(/)|"+"(-)|" + "(/)|" + "(\\s+)");
	}

}

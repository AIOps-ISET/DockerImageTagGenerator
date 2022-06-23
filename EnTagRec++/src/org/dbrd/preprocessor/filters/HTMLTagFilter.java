package org.dbrd.preprocessor.filters;

public class HTMLTagFilter extends AbstractWordFilter {

	@Override
	protected String[] filter(String word) {
		word = word.replaceAll("\\<\\s*a.*?\\>.*?\\<\\s*/\\s*a\\s*\\>", " ");
		word = word.replaceAll("(\\<\\s*pre\\s*\\>)", " ");

		word = word.replaceAll("[\\<p\\>|\\</p\\>]", " ");
		word = word.replaceAll("(\\<\\s*/\\s*code\\s*\\>)", " ");
		return new String[] {word};
	}

	public static void main(String[] args) {
		String s = "Str Function in VBA for Excel adds a character to the string <p>I am converting an integer to string using the str() function</p> <p>However, I noticed that the str() function would return an extra character to the string.</p>"

     + "<p>For example, <code>MsgBox(Len(str(1)))</code> would return 2.</p>" 
     + "<p>What is the extra character being appended?</p>"
	 +	"System.out.println(new HTMLTagFilter().filter(s)[0])";
		HTMLTagFilter filter = new HTMLTagFilter();
		System.out.println(filter.filter(s));
	}
}

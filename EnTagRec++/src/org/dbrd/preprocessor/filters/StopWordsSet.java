package org.dbrd.preprocessor.filters;

import java.util.HashSet;
import java.util.Set;

public class StopWordsSet {
	
	private final Set<String> stopWords = new HashSet<String>();

	public StopWordsSet() {
		stopWords.add("a");
		stopWords.add("ah");
		stopWords.add("am");
		stopWords.add("an");
		stopWords.add("and");
		stopWords.add("are");
		stopWords.add("as");
		stopWords.add("at");
		stopWords.add("b");
		stopWords.add("be");
		stopWords.add("been");
		stopWords.add("but");
		stopWords.add("by");
		stopWords.add("c");
		stopWords.add("d");
		stopWords.add("did");
		stopWords.add("do");
		stopWords.add("does");
		stopWords.add("down");
		stopWords.add("e");
		stopWords.add("en");
		stopWords.add("er");
		stopWords.add("f");
		stopWords.add("for");
		stopWords.add("from");
		stopWords.add("h");
		stopWords.add("ha");
		stopWords.add("had");
		stopWords.add("has");
		stopWords.add("have");
		stopWords.add("he");
		
		stopWords.add("he's");
		stopWords.add("he'd");
		stopWords.add("he'll");

		stopWords.add("here");
		stopWords.add("her");
		stopWords.add("herself");
		stopWords.add("him");
		stopWords.add("himself");
		stopWords.add("his");
		stopWords.add("how");
		stopWords.add("i");
		
		stopWords.add("i'm");
		stopWords.add("i'd");
		
		stopWords.add("if");
		stopWords.add("in");
		stopWords.add("is");
		stopWords.add("it");
		
		stopWords.add("it's");
		stopWords.add("it'd");
		
		stopWords.add("its");
		stopWords.add("itself");
		stopWords.add("j");
		stopWords.add("k");
		stopWords.add("l");
		stopWords.add("m");
		stopWords.add("me");
		stopWords.add("my");
		stopWords.add("myself");
		stopWords.add("n");
		stopWords.add("no");
		stopWords.add("not");
		stopWords.add("isn't");
		stopWords.add("aren't");
		stopWords.add("wasn't");
		stopWords.add("weren't");
		stopWords.add("hasn't");
		stopWords.add("haven't");
		stopWords.add("don't");
		stopWords.add("doesn't");
		stopWords.add("cannot");
		stopWords.add("can't");
		stopWords.add("wouldn't");
		
		
		stopWords.add("o");
		stopWords.add("of");
		stopWords.add("oh");
		stopWords.add("on");
		stopWords.add("or");
		stopWords.add("our");
		stopWords.add("ours");
		stopWords.add("ourselves");
		stopWords.add("over");
		stopWords.add("p");
		stopWords.add("q");
		stopWords.add("quot");
		stopWords.add("r");
		stopWords.add("s");
		stopWords.add("shall");
		stopWords.add("she");
		
		stopWords.add("she's");
		stopWords.add("she'd");
		stopWords.add("she'll");
		
		stopWords.add("t");
		stopWords.add("that");
		
		stopWords.add("that's");
		stopWords.add("that'll");
		
		stopWords.add("the");
		stopWords.add("their");
		stopWords.add("theirselves");
		stopWords.add("them");
		stopWords.add("themselves");
		stopWords.add("then");
		stopWords.add("there");
		stopWords.add("these");
		stopWords.add("they");
		
		stopWords.add("they've");
		stopWords.add("they're");
		stopWords.add("they'd");
		stopWords.add("they'll");
		
		stopWords.add("this");
		stopWords.add("those");
		stopWords.add("though");
		stopWords.add("too");
		stopWords.add("to");
		stopWords.add("u");
		stopWords.add("un");
		stopWords.add("und");
		stopWords.add("under");
		stopWords.add("up");
		stopWords.add("v");
		stopWords.add("var");
		stopWords.add("w");
		stopWords.add("was");
		stopWords.add("we");
		stopWords.add("were");
		stopWords.add("what");
		
		stopWords.add("what's");
		stopWords.add("what'll");
		
		stopWords.add("when");
		stopWords.add("where");
		stopWords.add("which");
		stopWords.add("who");
		stopWords.add("who's");
		stopWords.add("who'll");
		
		stopWords.add("whom");
		stopWords.add("whose");
		stopWords.add("will");
		stopWords.add("with");
		stopWords.add("x");
		stopWords.add("y");
		stopWords.add("yes");
		stopWords.add("you");
		stopWords.add("your're");
		
		stopWords.add("you'll");
		stopWords.add("you'd");
		stopWords.add("you've");
		
		stopWords.add("your");
		stopWords.add("yours");
		stopWords.add("yourself");
		stopWords.add("yourselves");
		stopWords.add("z");
	}

	public boolean isStopWord(String word) {
		if (word == null || word.length() == 0) {
			return true;
		}
		return this.stopWords.contains(word.toLowerCase());
	}
	
	@Deprecated
	public boolean DoesNotContainWord(String word) {
		for (String s : stopWords) {
			if (s.equalsIgnoreCase(word.trim()))
				return false;
		}
		return true;
	}

	@Deprecated
	public static boolean DoesContainAlpabet(String Word) {
		char[] s = Word.toCharArray();
		for (int i = 0; i < s.length; i++)
			if (s[i] >= 'a' && s[i] <= 'z' || s[i] >= 'A' && s[i] <= 'Z')
				return true;
		return false;
	}
}
package org.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.dbrd.preprocessor.filters.AbstractWordFilter;
import org.dbrd.preprocessor.filters.HTMLSymbolRemover;
import org.dbrd.preprocessor.filters.HTMLTagFilter;
import org.dbrd.preprocessor.filters.LowerCaseWordFilter;
import org.dbrd.preprocessor.filters.NonAlphabetOrNumberSymbolsRemover;
import org.dbrd.preprocessor.filters.StemmingWordFilter;
import org.dbrd.preprocessor.filters.StopWordFilter;
import org.dbrd.preprocessor.filters.TokenizerWordFilter;

public class dataFilter {
	
	
	static String[] keywords = {"return ","while(","for(","switch(","if(","int ","char ","double","float","const",
        "switch{","case","void","{","}","\t","long","static",":","\\","-"};
	
	static public String filter_NL(String WordSet)
	{
		String[] CloneWS = WordSet.split(" +");
		List<AbstractWordFilter> filters = new ArrayList<AbstractWordFilter>();
		filters.add(new LowerCaseWordFilter());
		filters.add(new TokenizerWordFilter());
		filters.add(new HTMLSymbolRemover());
		filters.add(new HTMLTagFilter());
		filters.add(new StopWordFilter());
		filters.add(new StemmingWordFilter());
		filters.add(new NonAlphabetOrNumberSymbolsRemover());
		//filters.add(new NonAlphabetOrNumberSymbolsRemover());
		List<String> words = new LinkedList<String>();
		for(int i = 0 ; i < CloneWS.length; i++)
			words.add(CloneWS[i]);
		List<String> result = new ArrayList<String>();
		for(int i = 0; i < filters.size();i++ )
		{
			result.clear();
			for (String word : words) {
				result.addAll(Arrays.asList(filters.get(i).process(word)));
			}
			words.clear();
			words.addAll(result);
		}
		StringBuilder builder = new StringBuilder();
		for (String word : result) {
			builder.append(word).append(" ");
		}
		
		String s = builder.toString();
		s = s.replaceAll(" +", " ");
		if(s.startsWith(" "))
			s = s.substring(1);
		if(s.endsWith(" "))
			s = s.substring(0,s.length()-1);
		return s ;
	}
	
	static public  String filter_Code(String s){
		int i;
		String[] v = s.split("\n");
		
		//out.println(functionNumber+". "+k);
		String wordset ="";
		for( i=0; i< v.length;i++)
		{
			//System.out.println(v[i]);
			String filteredvertex = vertexFilter(v[i]);
			wordset = wordset + " "+filteredvertex;
			
			//System.out.println(filteredvertex+" ");
			//System.out.println("done");
		}
		//wordset = wordset + " "+ vertexFilter(MethodList.get(k).getComment());
		// remove first ,end and multiple whitespace 
		wordset = filter_NL(wordset);
		wordset = wordset.replaceAll(" +", " ");
		if(wordset.startsWith(" "))
			wordset = wordset.substring(1);
		if(wordset.endsWith(" "))
			wordset = wordset.substring(0,wordset.length()-1);
		
		
		return wordset;
	}
	
	static  String vertexFilter(String vertexName)
	{
		String s = vertexName;
		// filter \r
		s = s.replace("\r", "");
		//filter keywords
		for(int i = 0; i < keywords.length;i++)
		{
			if(s.contains(keywords[i]))
			{
				s = s.replace(keywords[i], " ");
			}
		}
		//split variable 
		if(s.contains("_"))
		{
			s = s.replace("_", " ");
		}
		ArrayList<Integer> pos = new ArrayList();
		
		
		//
		
		  for(int i =0; i < s.length();i++)
		{
			//filter signs e.g * &
			if(!(s.charAt(i) >= 'A' && s.charAt(i) <= 'Z')&&!(s.charAt(i) >= 'a' && s.charAt(i) <= 'z')
					&&s.charAt(i)!=' ')
			{
				String t = s.substring(i,i+1);
				s = s.replace(t, " ");
			}
			//filter function name
			if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z'
				&& (i+1)<s.length()&&s.charAt(i+1)>='a' &&s.charAt(i+1)<='z'
				&&(i-1)>=0&&!" ".equals(s.charAt(i-1)))
			{
				s = s.substring(0,i)+" "+s.substring(i);
				i=i+1;
			}
		}
		
		//remove additional white space
       
        String[] temp = s.split(" +");
        s = "";
        //filter the word whose length is less than 2
        for(int i =0; i < temp.length; i++)
        {
        	if(temp[i].length()<=2)
        		temp[i]=" ";
        	s = s + temp[i]+" ";
        }
         s = s.replaceAll(" +", " ");
		return s;
	}
	static public String filter_html(String WordSet)
	{
		String[] CloneWS = WordSet.split(" +");
		List<AbstractWordFilter> filters = new ArrayList<AbstractWordFilter>();

		filters.add(new HTMLSymbolRemover());
		filters.add(new HTMLTagFilter());
		//filters.add(new NonAlphabetOrNumberSymbolsRemover());
		List<String> words = new LinkedList<String>();
		for(int i = 0 ; i < CloneWS.length; i++)
			words.add(CloneWS[i]);
		List<String> result = new ArrayList<String>();
		for(int i = 0; i < filters.size();i++ )
		{
			result.clear();
			for (String word : words) {
				result.addAll(Arrays.asList(filters.get(i).process(word)));
			}
			words.clear();
			words.addAll(result);
		}
		StringBuilder builder = new StringBuilder();
		for (String word : result) {
			builder.append(word).append(" ");
		}
		
		String s = builder.toString();
		s = s.replaceAll(" +", " ");
		if(s.startsWith(" "))
			s = s.substring(1);
		if(s.endsWith(" "))
			s = s.substring(0,s.length()-1);
		return s ;
	}
}

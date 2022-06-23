package TermTagIndex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.preprocess.dataFilter;

import scala.actors.threadpool.Arrays;

public class TermTagIndexBuilder {

	/**
	 * @param args
	 */
	String root;
	static String[] tmp = { "NN", "NNP", "NNS", "NNPS" };
	static ArrayList<String> remainPOS = new ArrayList(Arrays.asList(tmp));
	static boolean preprocess = true;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String root = "O:/shaowei/folksonomy/freecode/";
		// String root =
		// "O:/shaowei/folksonomy/AlltheFourDataset/appleSource/";
		//String root = "G:/research/tag_recommendation/data_and_results/ChangeTagData/stackoverflow/";
		if(args.length < 1){
			System.out.println("usage [root path]");
			return ;
		}
		String root = args[0];
		
		String tag_docFile = root + "tag_doc_50.txt";
		// "test.txt";
		String dataDir = root + "posdata/";
		String dataDirAfterProcess = root + "posdata_preprocessed/";
		if (preprocess) {
			if (!new File(dataDirAfterProcess).isDirectory())
				new File(dataDirAfterProcess).mkdirs();
			preprocess(dataDir, dataDirAfterProcess);
		}
		System.out.println(System.currentTimeMillis());
		HashMap<String, HashMap<String, Integer>> term_tag_index = buildTermTagIndex(
				1, tag_docFile, dataDirAfterProcess);
		String outputPath = root + "term_tag_index.txt";
		output(term_tag_index, outputPath);
		System.out.println(System.currentTimeMillis());
	}
	
	

	public TermTagIndexBuilder(String root) {
		this.root = root;
	}

	// remove the tokens don't need to be consided and do stemmeing, stop words
	// removal
	public static void preprocess(String inputDir, String outputDir) {
		File[] fileList = new File(inputDir).listFiles();
		int count = 1;
		for (File f : fileList) {
			System.out.println(count++);
			String content = readContentIntoOneLine(f.getAbsolutePath());
			String outFile = outputDir + "/" + f.getName();
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
				String[] terms = content.split(" ");
				StringBuilder sb = new StringBuilder();
				for (String term : terms) {
					if (isReamined(term)) {
						String[] tmp = term.split("_");
						String token = tmp[0];
						sb.append(token + " ");
					}
				}
				String resultStr = dataFilter.filter_Code(sb.toString());
				bw.write(resultStr);
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void output(
			HashMap<String, HashMap<String, Integer>> term_tag_index,
			String outputPath) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
			for (String term : term_tag_index.keySet()) {
				bw.write(term + ":");
				HashMap<String, Integer> tag_occurence = term_tag_index
						.get(term);

				for (String tag : tag_occurence.keySet()) {
					int occur = tag_occurence.get(tag);
					bw.write(tag + "@" + occur + ",");
				}
				bw.newLine();
			}

			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HashMap<String, HashMap<String, Integer>> buildTermTagIndex(
			String path, String dataDir) {
		// token_ <tag, occurence>
		HashMap<String, HashMap<String, Integer>> TermTagIndex = new HashMap();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = br.readLine()) != null) {

				String[] sparts = line.split(":");
				String tag = sparts[0];
				System.out.println(tag);
				String[] docs = sparts[1].split(",");
				for (String docId : docs) {
					String inputPath = dataDir + docId;
					String content = readContentIntoOneLine(inputPath);
					String[] terms = content.split(" +");
					for (String term : terms) {
						// if(isReamined(term)){
						if (true) {
							String[] tmp = term.split("_");
							String token = tmp[0];
							if (token.equals(""))
								continue;
							// if contain this term
							if (TermTagIndex.containsKey(token)) {
								HashMap<String, Integer> tagMap = TermTagIndex
										.get(token);
								// count the occurnece of the tag for the term
								if (tagMap.containsKey(tag)) {
									int nextOccur = tagMap.get(tag) + 1;
									tagMap.put(tag, nextOccur++);
								} else {
									tagMap.put(tag, 1);
								}
							}
							// if don't contain this term
							else {
								HashMap<String, Integer> tagMap = new HashMap();
								tagMap.put(tag, 1);
								TermTagIndex.put(token, tagMap);
							}
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return TermTagIndex;
	}

	public static HashMap<String, HashMap<String, Integer>> buildTermTagIndex(
			int i, String path, String dataDir) {
		// token_ <tag, occurence>
		HashMap<String, HashMap<String, Integer>> TermTagIndex = new HashMap();
		HashMap<String, ArrayList<String>> doc_tags = new HashMap();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = br.readLine()) != null) {

				String[] sparts = line.split(":");
				String tag = sparts[0];
				// System.out.println(tag);
				String[] docs = sparts[1].split(",");
				for (String docId : docs) {
					if (doc_tags.containsKey(docId)) {
						doc_tags.get(docId).add(tag);
					} else {
						ArrayList<String> tags = new ArrayList();
						tags.add(tag);
						doc_tags.put(docId, tags);
					}
				}
			}
			br.close();
			File[] docs = new File(dataDir).listFiles();
			for (File docId : docs) {
				String inputPath = docId.getAbsolutePath();
				String content = readContentIntoOneLine(inputPath);
				String[] terms = content.split(" +");
				ArrayList<String> tags = doc_tags.get(docId.getName());
				if (tags == null)
					continue;
				for (String tag : tags) {
					for (String term : terms) {
						// if(isReamined(term)){
						if (true) {
							String[] tmp = term.split("_");
							String token = tmp[0];
							if (token.equals(""))
								continue;
							// if contain this term

							if (TermTagIndex.containsKey(token)) {
								HashMap<String, Integer> tagMap = TermTagIndex
										.get(token);
								// count the occurnece of the tag for the term

								if (tagMap.containsKey(tag)) {
									int nextOccur = tagMap.get(tag) + 1;
									tagMap.put(tag, nextOccur++);
								} else {
									tagMap.put(tag, 1);
								}

							}
							// if don't contain this term
							else {
								HashMap<String, Integer> tagMap = new HashMap();
								tagMap.put(tag, 1);
								TermTagIndex.put(token, tagMap);
							}
						}
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return TermTagIndex;
	}

	private static boolean isReamined(String term) {
		//
		String[] tmp = term.split("_");
		if (tmp.length < 2)
			System.out.print("bug " + term);
		String label = tmp[1];
		if (remainPOS.contains(label))
			return true;
		else
			return false;
	}

	public static String readContentIntoOneLine(String file) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + " ");
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}



	public static void runOnglobal(String root) {
		// TODO Auto-generated method stub
		String tag_docFile = root + "tag_doc_50.txt";
		// "test.txt";
		String dataDir = root + "posdata/";
		String dataDirAfterProcess = root + "posdata_preprocessed/";
		if (preprocess) {
			if (!new File(dataDirAfterProcess).isDirectory())
				new File(dataDirAfterProcess).mkdirs();
			preprocess(dataDir, dataDirAfterProcess);
		}
		System.out.println(System.currentTimeMillis());
		HashMap<String, HashMap<String, Integer>> term_tag_index = buildTermTagIndex(
				1, tag_docFile, dataDirAfterProcess);
		String outputPath = root + "term_tag_index.txt";
		output(term_tag_index, outputPath);
		System.out.println(System.currentTimeMillis());
	}

}

package tagRecommend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.tmt.model.llda.*;
import Network.Graph;
import Network.Node;
import Network.Tag;
import Network.generateNetwork;
import TermTagIndex.TermTagIndex;
import TermTagIndex.TermTagIndexBuilder;
import queryExpansion.Query;
import queryExpansion.run;
import topicinfer.*;

import edu.stanford.nlp.tmt.model.llda.CVB0LabeledLDA;

public class TestvaryingTrainingData {

	/**
	 * @param args
	 */
	static boolean generateCrossData = true;
	static boolean estimated = true;
	static boolean generateNetwork = true;
	// query expansion or not
	static boolean expansion = true;
	static boolean topicInfer = true;
	static boolean termTagIndex = true;
	static final int crossNumber = 10;
	static final int infer_topk = 70;
	static final int termTagIndex_topK = 70;
	static final int extend_topk = 10;
	// static String train = "trainDataset.csv";
	static String train = "trainDataset_distr.csv";
	static String test = "testDataset.csv";
	static String golden = "goldenSet.csv";
	static ArrayList<Tag> tag_doc = new ArrayList();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String root = "O:\\shaowei\\folksonomy\\freecode\\";
		//String root = "E:\\shaowei\\stackoverflow\\";
		//String root = "O:\\shaowei\\folksonomy\\AlltheFourDataset\\appleSource\\";
		String root ="W:\\research\\tag_recommendation\\data\\StackOverFlowForShaowei\\";
		//String root = 	 "O:\\shaowei\\folksonomy\\AlltheFourDataset\\askubuntuSource\\";
		String inputData = root + "dataset.csv";
		String TestDir = root + "testcaseForVaryTrainingData\\";
		String resultOutput = TestDir + "\\recall" + "_" + extend_topk + ".csv";
		String posPreprecessedDir = root + "posdata_preprocessed\\";
		if (generateCrossData) {
			generateTestData_Distribution(inputData, crossNumber, TestDir);
		}
		HashMap<String, Double> result = new HashMap();

		// perform cross validation
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(root
					+ "\\recall_varyTrainingData.csv"));
			bw.write("percentage,recall@10,recall@5\n");
			for (int i = 0; i < crossNumber-1; i++) {
				String trainingData = TestDir + i + "\\" + train;
				String testData = TestDir + i + "\\" + golden;
				String outputModelDir = TestDir + i + "\\model";
				// load golden set
				// BufferedWriter bw = new BufferedWriter(new
				// FileWriter(TestDir+i+"\\tagInferred_result.txt"));
				HashMap<String, Doc> goldenSet = loadGoldset(inputData);
				// estimate ladeled LDA
				if (estimated) {
					if (!new File(outputModelDir).isDirectory())
						topicEstimate.estimateTopic(outputModelDir,
								trainingData);
				}

				// load llda model

				CVB0LabeledLDA model = TopicInfer.getmodel(outputModelDir);
				String currentTestDir = TestDir + i;
				System.out.println("start inferring..........");
				// construct training query
				String inferTopicForTrainingPath = currentTestDir
						+ "\\query-training-out.csv";
				TopicInfer.getTopKTopic(model, outputModelDir, trainingData,
						inferTopicForTrainingPath, infer_topk);
				ArrayList<Query> queryListForTraining = run.loadQueryFromFile(
						inferTopicForTrainingPath, trainingData);

				// construct testing query

				String inferTopicPathForTesting = currentTestDir
						+ "\\query-testing-out.csv";
				TopicInfer.getTopKTopic(model, outputModelDir, testData,
						inferTopicPathForTesting, infer_topk);
				ArrayList<Query> queryListForTesting = run.loadQueryFromFile(
						inferTopicPathForTesting, testData);

				// load testing data text
				for (Query q : queryListForTesting) {
					String id = q.query_id;
					Doc d = goldenSet.get(id);
					String input = posPreprecessedDir + d.getName();
					String content = TermTagIndexBuilder
							.readContentIntoOneLine(input);
					q.setText(content);
					// q.query_id = d.getName();
				}

				// load training data text
				for (Query q : queryListForTraining) {
					String id = q.query_id;
					Doc d = goldenSet.get(id);
					String input = posPreprecessedDir + d.getName();
					String content = TermTagIndexBuilder
							.readContentIntoOneLine(input);
					q.setText(content);
					// q.query_id = d.getName();
				}

				// infer topic llda
				if (topicInfer) {
					run.loadInferredTopic(inferTopicPathForTesting,
							queryListForTesting);
					run.loadInferredTopic(inferTopicForTrainingPath,
							queryListForTraining);
				}

				// infer topic from term_tag_index
				if (termTagIndex) {
					TermTagIndex tti = new TermTagIndex();
					tti.LoadIndexFromFile(root + "term_tag_index.txt");
					for (Query q : queryListForTesting) {
						tti.AssignTags(q, TermTagIndex.INFER_NON_UNIQUE_TOKEN,
								termTagIndex_topK);
					}

					for (Query q : queryListForTraining) {
						tti.AssignTags(q, TermTagIndex.INFER_NON_UNIQUE_TOKEN,
								termTagIndex_topK);
					}

				}

				// spread in the network

				// "D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\mergedStackOverFlowIndex50\\resultGraph.txt";

				// outputQuery(currentTestDir+"\\before_testdata",queryListForTesting);
				// outputQuery(currentTestDir+"\\before_traindata",queryListForTraining);

				// query expansion
				int qid = 1;
				if (expansion) {
					// generate network
					String graphInputForTesting = currentTestDir
							+ "\\GraphresultForTesting.txt";
					String graphInputForTraining = currentTestDir
							+ "\\GraphresultForTraining.txt";
					if (generateNetwork) {
						// output for training
						generateNetwork gn = new generateNetwork(trainingData);
						// gn.load();
						gn.getNetwork();
						gn.output(graphInputForTraining);
						// output for testing
						gn = new generateNetwork(testData);
						// gn.load();
						gn.getNetwork();
						gn.output(graphInputForTesting);
					}
					// for training data
					Graph g = new Graph();
					g.createFromEdgeFile(graphInputForTraining);
					for (Query q : queryListForTraining) {
						//

						// System.out.println(qid++);
						q.extendTagWithTagGraph(g, 200);
						// System.out.println(q.print());

					}

					// for testing data
					g = new Graph();
					g.createFromEdgeFile(graphInputForTesting);
					for (Query q : queryListForTesting) {
						//

						// System.out.println(qid++);
						q.extendTagWithTagGraph(g, 200);
						// System.out.println(q.print());

					}
				}

				// int[] a = new int[2];

				// outputQuery(currentTestDir+"\\after_testdata",queryListForTesting);
				// outputQuery(currentTestDir+"\\after_traindata",queryListForTraining);

				// evaluate

				
				/*
				 * for (Query q : queryList) { String id = q.id;
				 * bw.write(id+"\t"); ArrayList<String> tags =
				 * goldenSet.get(id).getTaglist(); int count = 0; for (String
				 * tag : tags) {
				 * 
				 * if (q.getTags().contains(tag)) { count++; } } for(String tag
				 * : q.getTags()){ bw.write(tag+"@"+
				 * q.getInferedTags().get(tag).getWeight()+","); } bw.newLine();
				 * Double recall = (double) (count) / (double) tags.size();
				 * result.put(goldenSet.get(id).getName(), recall);
				 * bwLocal.write(goldenSet.get(id).getName() + "," + recall +
				 * "\n"); }
				 */
				

				// output trained result
				
				LinearCombinationTrainerAndTester crossValidation = new LinearCombinationTrainerAndTester(
						queryListForTesting, goldenSet, extend_topk);
				// HashMap<String, Double> recalls =
				// crossValidation.performCrossValidation(10);
				// train a and b, for the linear combination
				double[] paras = crossValidation.trainParameters(
						queryListForTraining, 2);

				HashMap<String, Double> recalls_5 = crossValidation
						.calculateRecalls(paras, queryListForTesting,5);
				HashMap<String, Double> recalls_10 = crossValidation
						.calculateRecalls(paras, queryListForTesting,10);
				
				int percentage= (9-i)*10;
				bw.write(percentage+"%"+","+calAverage(recalls_10)+","+calAverage(recalls_5)+"\n");
				// bw.close();

			}
			bw.close();
			/*BufferedWriter bw = new BufferedWriter(new FileWriter(resultOutput));
			for (String key : result.keySet()) {
				bw.write(key + "," + result.get(key) + "\n");
			}
			bw.close();*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public double calAverage(HashMap<String, Double> recalls){
		double sum = 0;
		for (String key : recalls.keySet()) {
		
			sum += recalls.get(key);

		}
		double averageRecall = sum / (double) (recalls.size());
		return averageRecall;
	}
	
	private static HashMap<String, Doc> loadGoldset(String path) {
		HashMap<String, Doc> result = new HashMap();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = null;
			// int id = 0;
			while ((line = br.readLine()) != null) {
				String[] sparts = line.split(",");
				Doc d = new Doc(sparts[0]);
				String[] tagstr = sparts[1].split(" ");
				ArrayList<String> tags = new ArrayList();
				for (int i = 0; i < tagstr.length; i++) {
					tags.add(tagstr[i]);
				}
				d.setTaglist(tags);
				result.put(d.getName(), d);
				// id++;
			}
			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	private static void generateTestData_Random(String dataset,
			int crossnumber, String TestDir) {
		// TODO Auto-generated method stub

		ArrayList<Doc> docList = loadDataset(dataset);
		ArrayList<Doc> remainingTest = (ArrayList<Doc>) docList.clone();
		int[] testCaseNumberPerCross = new int[10];
		int average = docList.size() / crossnumber;

		// get the number of each cross test
		for (int i = 0; i < crossnumber; i++) {
			if (i < crossnumber - 1) {
				testCaseNumberPerCross[i] = average;
			} else
				testCaseNumberPerCross[i] = docList.size() - average
						* (crossnumber - 1);
		}
		for (int i = 0; i < crossnumber; i++) {
			String subTestDir = TestDir + i;
			if (!new File(subTestDir).isDirectory())
				new File(subTestDir).mkdirs();
			// random pick test set
			ArrayList<Doc> currentTestset = new ArrayList();
			for (int j = 0; j < testCaseNumberPerCross[i]; j++) {
				int random = (int) (Math.random() * remainingTest.size());
				// System.out.println(i+":" + random);
				if (random == remainingTest.size())
					random--;
				currentTestset.add(remainingTest.get(random));
				// remove this in the doclist
				remainingTest.remove(random);
			}
			// get training set
			ArrayList<Doc> currentTrainset = new ArrayList();
			for (int j = 0; j < docList.size(); j++) {
				if (!currentTestset.contains(docList.get(j))) {
					currentTrainset.add(docList.get(j));
				}
			}

			// output test and train set
			output(subTestDir + "\\" + train, currentTrainset, true);
			output(subTestDir + "\\" + test, currentTestset, false);
			output(subTestDir + "\\" + golden, currentTestset, true);
		}
	}

	private static void generateTestData_Distribution(String dataset,
			int crossnumber, String TestDir) {
		// TODO Auto-generated method stub

		HashMap<String, Doc> docList = loadDatasetbasedOnTag(dataset);
		HashMap<String, Doc> remainingTest;
		int[] testCaseNumberPerCross = new int[crossnumber-1];
		int average = docList.size() / crossnumber;

		// get the number of each cross test
		for (int i = 1; i < crossnumber; i++) {
			testCaseNumberPerCross[i - 1] = average * i;
		}
		for (int i = 0; i < crossnumber-1; i++) {
			String subTestDir = TestDir + i;
			if (!new File(subTestDir).isDirectory())
				new File(subTestDir).mkdirs();
			docList = loadDatasetbasedOnTag(dataset);
			remainingTest = (HashMap<String, Doc>) docList.clone();
			ArrayList<Doc> currentTestset = new ArrayList();
			ArrayList<Doc> currentTrainset = new ArrayList();

			// pick test set from each tag

			int count = 0;

			// get training set
			boolean notFull = true;
			while (notFull) {
				for (int j = 0; j < tag_doc.size(); j++) {
					Tag tag = tag_doc.get(j);
					if (!tag.getDocMap().isEmpty()) {
						String d_id = tag.getDocMap().keySet().iterator()
								.next();
						Doc d = docList.get(d_id);
						if (!currentTestset.contains(d)) {
							currentTestset.add(d);
							count++;
							// remove this id in the doc_tag
							tag.getDocMap().remove(d_id);

							// remove in remainingTest
							remainingTest.remove(d_id);
						} else {
							System.out.print("duplica");
						}
						if (count == testCaseNumberPerCross[i]) {
							notFull = false;
							break;
						}
					}
				}
			}

			for (String key : docList.keySet()) {
				if (!currentTestset.contains(docList.get(key))) {
					currentTrainset.add(docList.get(key));
				}
			}
			tag_doc.clear();

			// output test and train set
			output(subTestDir + "\\" + train, currentTrainset, true);
			output(subTestDir + "\\" + test, currentTestset, false);
			output(subTestDir + "\\" + golden, currentTestset, true);
		}

	}

	private static HashMap<String, Doc> loadDatasetbasedOnTag(String dataset) {
		HashMap<String, Doc> list = new HashMap();
		HashMap<String, ArrayList<String>> tmp = new HashMap();
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataset));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] sparts = line.split(",");
				if (sparts.length < 3)
					System.out.println("bug");
				String id = sparts[0];
				String tags = sparts[1];
				String connent = sparts[2];
				Doc d = new Doc(id, tags, connent);
				list.put(id, d);
				// add tag doc
				String[] tagList = tags.split(" ");
				// take the first tag
				String tag = tagList[0];

				if (tmp.containsKey(tag)) {
					if (!tmp.get(tag).contains(id))
						tmp.get(tag).add(id);
					else
						System.out.println(tag + "," + id);
				} else {
					ArrayList<String> newlist = new ArrayList();
					newlist.add(id);
					tmp.put(tag, newlist);
				}

			}
			br.close();
			// dump to tag_doc
			for (String key : tmp.keySet()) {
				HashMap<String, String> docmap = new HashMap();
				for (String docId : tmp.get(key)) {
					docmap.put(docId, "1");
				}

				Tag t = new Tag(key, docmap);
				tag_doc.add(t);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private static void output(String path, ArrayList<Doc> dataset,
			boolean outputTag) {

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			for (Doc d : dataset) {
				String line = "";
				if (outputTag)
					line = d.getName() + "," + d.getTags() + ","
							+ d.getWordSet();
				else
					line = d.getName() + "," + d.getWordSet();
				bw.write(line);
				bw.write("\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ArrayList<Doc> loadDataset(String dataset) {
		// TODO Auto-generated method stub
		ArrayList<Doc> list = new ArrayList();
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataset));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] sparts = line.split(",");
				String id = sparts[0];
				String tags = sparts[1];
				String connent = sparts[2];
				Doc d = new Doc(id, tags, connent);
				list.add(d);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public static void outputQuery(String path, ArrayList<Query> queryList) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write("LLDA\n");
			for (Query q : queryList) {
				StringBuilder sb = new StringBuilder();
				sb.append(q.query_id + ",");
				for (Node tag : q.getInferedTagsFromLLDA().values()) {
					sb.append(tag.getName() + ":" + tag.getWeight() + ",");
				}
				bw.write(sb.toString());
				bw.newLine();
			}
			bw.write("TermTagIndex\n");
			for (Query q : queryList) {
				StringBuilder sb = new StringBuilder();
				sb.append(q.query_id + ",");
				for (Node tag : q.getTermTagInferTags().values()) {
					sb.append(tag.getName() + ":" + tag.getWeight() + ",");
				}
				bw.write(sb.toString());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

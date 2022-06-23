package evaluate_EnTagRec_U;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.preprocess.QuestionInformationManager;

import edu.stanford.nlp.tmt.model.llda.*;
import Network.Graph;
import Network.Node;
import Network.Tag;
import Network.generateNetwork;
import TagInferBasedonUser.InferBasedOnUser;
import TermTagIndex.TermTagIndex;
import TermTagIndex.TermTagIndexBuilder;
import queryExpansion.Query;
import queryExpansion.run;
import topicinfer.*;
import tagRecommend.*;

import edu.stanford.nlp.tmt.model.llda.CVB0LabeledLDA;

public class RunKtimesForEffectSizeTest_on_b_f_individual {

	/**
	 * @param args
	 */
	static String logFile = null;

	static boolean generateCrossData = true;
	static boolean estimated = true;
	static boolean generateNetwork = false;
	// query expansion or not
	static boolean expansion = false;
	static boolean topicInfer = true;
	static boolean termTagIndex = true;

	static boolean inferBasedOnUser = false;
	static final int crossNumber = 10;
	static int infer_topK = 70;
	static int termTagIndex_topK = 70;
	static int returnTopK = 10;
	// static String train = "trainDataset.csv";
	static String train = "trainDataset_distr.csv";
	static String test = "testDataset.csv";
	static String golden = "goldenSet.csv";
	static ArrayList<Tag> tag_doc = new ArrayList();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int repeat = 5;
		String root = "G:/research/tag_recommendation/folksonomy/freecode/";
		// String root = 		 "G:/research/tag_recommendation/folksonomy/AlltheFourDataset/appleSource/";
		//String root = "G:/research/tag_recommendation/folksonomy/AlltheFourDataset/askubuntuSource/";
		// String root =
		// "G:/research/tag_recommendation/folksonomy/StackOverFlowForShaowei/";
		// String root
		// ="F:/shaowei/research/tag_recommendation/folksonomy/StackOverFlowForShaowei/";
		String inputData = root + "dataset.csv";

		BufferedWriter bwRepeat = new BufferedWriter(new FileWriter(root
				+ "f_b_repeat_precision_recall_test.csv"));
		bwRepeat.write("id,EnRecTagb_P@5,EnRecTagb_R@5,EnRecTagb_P@10,EnRecTagb_R@10,EnRecTagf_P@5,EnRecTagf_R@5,EnRecTagf_P@10,EnRecTagf_R@10\n");

		for (int repeatI = 0; repeatI < repeat; repeatI++) {
			bwRepeat.write(repeatI + ",");

			String TestDir = root + "testcase_repeat" + repeatI + "/";
			// String TestDir = root + "testcase/";
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
			Date date = new Date();

			logFile = TestDir + "log_" + dateFormat.format(date) + ".txt";

			HashMap<String, Double> final_results = new HashMap();
			// String root =
			// "G:/research/tag_recommendation/folksonomy/freecode/";
			// generate cross data
			if (generateCrossData) {
				generateTestData_Distribution(inputData,
						tenCrossValidation.crossNumber, TestDir);
			}

			String[] SingleComp = { "b", "f" };
			int[] returnTopKs = { 5, 10 };

			for (String singleC : SingleComp) {
				if (singleC.equals("b")) {
					topicInfer = true;
					termTagIndex = false;
					//expansion = false;
				} else {
					topicInfer = false;
					termTagIndex = true;
					//expansion = true;
				}

				
				String posPreprecessedDir = root + "posdata_preprocessed/";

				HashMap<String, Double> result_recall_5 = new HashMap();
				HashMap<String, Double> result_precision_5 = new HashMap();
				HashMap<String, Double> result_recall_10 = new HashMap();
				HashMap<String, Double> result_precision_10 = new HashMap();
				// HashMap<String,Double> result_10= new HashMap();
				// perform cross validation
				try {
					for (int i = 0; i < crossNumber; i++) {
						String trainingData = TestDir + i + "/" + train;
						String testData = TestDir + i + "/" + golden;
						String outputModelDir = TestDir + i + "/model";
						// load golden set
						// BufferedWriter bw = new BufferedWriter(new
						// FileWriter(TestDir+i+"/tagInferred_result.txt"));
						HashMap<String, Doc> goldenSet = loadGoldset(inputData);
						// estimate ladeled LDA
						if (estimated) {
							if (!new File(outputModelDir).isDirectory())
								topicEstimate.estimateTopic(outputModelDir,
										trainingData);
						}

						// load llda model

						CVB0LabeledLDA model = TopicInfer
								.getmodel(outputModelDir);
						String currentTestDir = TestDir + i;
						System.out.println("start inferring..........");
						// construct training query
						String inferTopicForTrainingPath = currentTestDir
								+ "/query-training-out.csv";
						TopicInfer.getTopKTopic(model, outputModelDir,
								trainingData, inferTopicForTrainingPath,
								infer_topK);
						ArrayList<Query> queryListForTraining = run
								.loadQueryFromFile(inferTopicForTrainingPath,
										trainingData);

						// construct testing query

						String inferTopicPathForTesting = currentTestDir
								+ "/query-testing-out.csv";
						TopicInfer.getTopKTopic(model, outputModelDir,
								testData, inferTopicPathForTesting, infer_topK);
						ArrayList<Query> queryListForTesting = run
								.loadQueryFromFile(inferTopicPathForTesting,
										testData);

						// load testing data text
						for (Query q : queryListForTesting) {
							String id = q.query_id;
							Doc d = goldenSet.get(id);
							String input = posPreprecessedDir + d.getName();
							String content = TermTagIndexBuilder
									.readContentIntoOneLine(input);
							q.setText(content);
							q.trueTags = d.getTaglist();
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
							q.trueTags = d.getTaglist();
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
								tti.AssignTags(q,
										TermTagIndex.INFER_NON_UNIQUE_TOKEN,
										termTagIndex_topK);
							}

							for (Query q : queryListForTraining) {
								tti.AssignTags(q,
										TermTagIndex.INFER_NON_UNIQUE_TOKEN,
										termTagIndex_topK);
							}

						}

						// spread in the network

						// "D:/Shaowei/research/Folksonomies/stackoverflow/mergedStackOverFlowIndex50/resultGraph.txt";

						// outputQuery(currentTestDir+"/before_testdata",queryListForTesting);
						// outputQuery(currentTestDir+"/before_traindata",queryListForTraining);

						// query expansion
						int qid = 1;
						if (expansion) {
							// generate network
							String graphInputForTesting = currentTestDir
									+ "/GraphresultForTesting.txt";
							String graphInputForTraining = currentTestDir
									+ "/GraphresultForTraining.txt";
							if (generateNetwork) {
								// output for training
								generateNetwork gn = new generateNetwork(
										trainingData);
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

						// outputQuery(currentTestDir+"/after_testdata",queryListForTesting);
						// outputQuery(currentTestDir+"/after_traindata",queryListForTraining);

						// evaluate

						
						for (int returnK : returnTopKs) {
							returnTopK = returnK;
							// output trained result
							
							LinearCombinationTrainerAndTester crossValidation = new LinearCombinationTrainerAndTester(
									queryListForTesting, goldenSet, returnTopK);
							// HashMap<String, Double> recalls =
							// crossValidation.performCrossValidation(10);
							// train a and b, for the linear combination
							// double[] paras = crossValidation.trainParameters(
							// queryListForTraining);

							// output candidate tags of each query
							/*
							 * writeToLogFile(logFile, queryListForTesting, 50);
							 */
							//double[] paras = crossValidation.trainParameters(
							//		queryListForTraining);
							//
							double[] paras = new double[4];
							paras[0] = 1;
							paras[1] = 1;
							// retu rn recall
							HashMap<String, Double> recalls = crossValidation
									.calculateRecalls(paras,
											queryListForTesting);

							// return precision

							HashMap<String, Double> precisions = crossValidation
									.calculatPrecision(paras,
											queryListForTesting);
							double sum = 0;
							for (String key : recalls.keySet()) {
								
								//bwLocal.write(key + "," + recalls.get(key));
								if(returnTopK==5){
								result_recall_5.put(key, recalls.get(key));
								result_precision_5.put(key, precisions.get(key));
								
								}
								if(returnTopK==10){
									result_recall_10.put(key, recalls.get(key));
									result_precision_10.put(key, precisions.get(key));
									
									}
								sum += recalls.get(key);
								//bwLocal.newLine();
							}
							double averageRecall = sum
									/ (double) (recalls.size());

							System.out.println("iter" + i + "\t" + paras[0]
									+ "\t" + paras[1] + "\t" + paras[2] + "\t"
									+ averageRecall);
							
						}
						// bw.close();

					}
					
					// output recall and precision
					
					double precision_sum_5 = 0;
					double recall_sum_5 = 0;
					for (String key : result_recall_5.keySet()) {
						
						precision_sum_5 += result_precision_5.get(key);
						recall_sum_5 += result_recall_5.get(key);
					}
					
					double recall_5 = (double) recall_sum_5
							/ (double) result_recall_5.size();
					double precision_5 = (double) precision_sum_5
							/ (double) result_precision_5.size();
					System.out.println("final average precision_5 and recall_5:"
							+ precision_5 + "\t" + recall_5);
					
					bwRepeat.write(precision_5 + "," + recall_5 + ",");
					

					double precision_sum_10 = 0;
					double recall_sum_10 = 0;
					for (String key : result_recall_10.keySet()) {
						
						precision_sum_10 += result_precision_10.get(key);
						recall_sum_10 += result_recall_10.get(key);
					}
					
					double recall_10 = (double) recall_sum_10
							/ (double) result_recall_10.size();
					double precision_10 = (double) precision_sum_10
							/ (double) result_precision_10.size();
					System.out.println("final average precision_10 and recall_10:"
							+ precision_10 + "\t" + recall_10);
					
					bwRepeat.write(precision_10 + "," + recall_10 + ",");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// for (String key : final_results.keySet()){
			// System.out.println(key + "\t" + final_results.get(key));

			// }
			bwRepeat.write("\n");
		}
		bwRepeat.close();
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
			output(subTestDir + "/" + train, currentTrainset, true);
			output(subTestDir + "/" + test, currentTestset, false);
			output(subTestDir + "/" + golden, currentTestset, true);
		}
	}

	private static void generateTestData_Distribution(String dataset,
			int crossnumber, String TestDir) {
		// TODO Auto-generated method stub

		HashMap<String, Doc> docList = loadDatasetbasedOnTag(dataset);
		HashMap<String, Doc> remainingTest = (HashMap<String, Doc>) docList
				.clone();
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

			ArrayList<Doc> currentTestset = new ArrayList();
			ArrayList<Doc> currentTrainset = new ArrayList();
			if (i == crossnumber - 1) {
				// dump the remained doc to test case
				for (Doc d : remainingTest.values())
					currentTestset.add(d);

				for (String key : docList.keySet()) {
					if (!currentTestset.contains(docList.get(key))) {
						currentTrainset.add(docList.get(key));
					}
				}
			} else {
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

			}
			// output test and train set
			output(subTestDir + "/" + train, currentTrainset, true);
			output(subTestDir + "/" + test, currentTestset, false);
			output(subTestDir + "/" + golden, currentTestset, true);
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

	public static void writeToLogFile(String file,
			ArrayList<Query> queryListForTesting, int topK) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			for (Query q : queryListForTesting) {
				bw.write(q.query_id + "\n");
				bw.write("true tags:");
				int count = 0;
				for (String tag : q.trueTags) {
					bw.write(tag + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");
				bw.write("termTagInfer tags:");
				count = 0;
				for (Node n : q.termTagInferTags.values()) {
					bw.write(n.getName() + ":" + n.getWeight() + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");

				bw.write("inferedTagsFromLLDA tags:");
				count = 0;
				for (Node n : q.inferedTagsFromLLDA.values()) {
					bw.write(n.getName() + ":" + n.getWeight() + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");
				bw.write("inferedTagsFromUser tags:");
				count = 0;
				for (Node n : q.inferedTagsFromUser.values()) {
					bw.write(n.getName() + ":" + n.getWeight() + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");

				bw.write("inferedTagsFromAsscoiatedRule tags:");
				count = 0;
				for (Node n : q.inferedTagsFromAsscoiatedRule.values()) {
					bw.write(n.getName() + ":" + n.getWeight() + "\t");
					count++;
					if (count > topK)
						break;
				}
				bw.write("\n");

			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

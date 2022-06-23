package evaluate_EnTagRec_UA;

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
import java.util.List;

import org.preprocess.QuestionInformationManager;

import edu.stanford.nlp.tmt.model.llda.*;
import FormatData.FormatDataFromTagChangeData;
import FormatData.GenerateDataForTagChange;
import Network.Graph;
import Network.Node;
import Network.Tag;
import Network.generateNetwork;
import TagInferBasedonUser.InferBasedOnUser;
import TermTagIndex.TermTagIndex;
import TermTagIndex.TermTagIndexBuilder;
import queryExpansion.Query;
import queryExpansion.run;
import scala.reflect.generic.Trees.This;
import topicinfer.*;
import tagRecommend.*;

import edu.stanford.nlp.tmt.model.llda.CVB0LabeledLDA;
import evaluate_EnTagRec_U.IOManager;
import getAssociatePattern.InferenceWithAssocRules;

public class RunKtimesForEffectSizeTest_onTagChangeData {

	/**
	 * @param args
	 * 
	 * evaluate on all tags in ground truth, not exclude the one in given tags
	 */
	
	static boolean debug = true;
	static String logFile = null;
	static boolean generateCrossData = true;
	static boolean estimated = true;
	static boolean generateNetwork = true;

	// query expansion or not
	static boolean expansion = false;
	static boolean topicInfer = true;
	static boolean termTagIndex = true;
	static boolean inferBasedOnUser = true;
	static final int crossNumber = 10;
	public static String ARinputFile = "tagsForAR.csv";
	static int infer_topK = 50;
	static int termTagIndex_topK = 50;
	static int returnTopK = 10;

	// parameters of associated rules mining
	static boolean useAddiionalTags = true;
	static int givenTagN = 1;
	static double minsup = 0.0;
	static double minconf = 0.00;
	static double minlift = 0;

	// static String train = "trainDataset.csv";
	static String train = "trainDataset_distr.csv";
	static String test = "testDataset.csv";
	static String golden = "goldenSet.csv";
	static ArrayList<Tag> tag_doc = new ArrayList();

	public static void main(String[] args) throws IOException {

		
		// TODO Auto-generated method stub
		// String root = "E:/shaowei/freecode/";
		// String root = "G:/research/tag_recommendation/data_and_results/ChangeTagData/stackoverflow/";
		//String root = "G:/research/tag_recommendation/data_and_results/AlltheFourDataset/appleSource/";
		// String root =
		// "G:/research/tag_recommendation/folksonomy/AlltheFourDataset/askubuntuSource/";
		// String root=
		// "G:/research/tag_recommendation/folksonomy/freecode/";
		 
		if(args.length < 2){
			System.out.println("usage [root path] [repeat times]");
			return ;
		}
		String root = args[0];
		int repeatN  = Integer.parseInt(args[1]);
		 
		HashMap<String, Double> final_results = new HashMap();
		// String root  ="G:/research/tag_recommendation/data_and_results/AlltheFourDataset/StackOverFlowForShaowei/";
		String inputData = root + "dataset.csv";
		
		
		BufferedWriter bwRepeat = new BufferedWriter(new FileWriter(root+"repeat_precision_recall_AU.csv", true));
		bwRepeat.write("id,EnRecTagUA_P@5,EnRecTagUA_R@5,EnRecTagUA_P@10,EnRecTagUA_R@10,EnRecTagU_P@5,EnRecTagU_R@5,EnRecTagU_P@10,EnRecTagU_R@10\n");

		for (int repeatI = 3; repeatI < repeatN; repeatI++) {
			bwRepeat.write(repeatI+",");
			
			String TestDir = root + "testcase_addtionalTag" + repeatI + "/";
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
			Date date = new Date();

			logFile = TestDir + "log_" + dateFormat.format(date) + ".txt";
			
			
			
			int[] testKs = { 70 };
			int[] returnTopKs = { 5, 10 };
			boolean[] addTags = { true, false };
			for (boolean addTag : addTags) {

				useAddiionalTags = addTag;
				for (int returnK : returnTopKs) {
					returnTopK = returnK;

					for (int testk : testKs) {
						infer_topK = testk;
						termTagIndex_topK = testk;

						String resultOutput = null;
						if (!useAddiionalTags)
							resultOutput = TestDir + "/GivenTag" + givenTagN
									+ "fbu_recall_precision" + "@" + returnTopK
									+ "_" + infer_topK + "_"
									+ termTagIndex_topK + ".csv";
						else
							resultOutput = TestDir + "/GivenTag" + givenTagN
									+ "afbu_recall_precision" + "@"
									+ returnTopK + "_" + infer_topK + "_"
									+ termTagIndex_topK + ".csv";

						String posPreprecessedDir = root
								+ "posdata_preprocessed/";
						if (generateCrossData) {
							generateTestData_Distribution(inputData,
									tenCrossValidation.crossNumber, TestDir);
						}

						HashMap<String, Double> result_recall = new HashMap();
						HashMap<String, Double> result_precision = new HashMap();
						
						InferBasedOnUser inferBOU =null;
						if(inferBasedOnUser)
							inferBOU= new InferBasedOnUser(root);

						// HashMap<String,Double> result_10= new HashMap();
						// perform cross validation
						try {
							for (int i = 0; i < crossNumber; i++) {
								
								String trainingData = TestDir + i + "/"
										+ train;
								String testData = TestDir + i + "/" + golden;
								String outputModelDir = TestDir + i + "/model";
								String eachFoldLogFile = TestDir + i + "/log.txt";
								// load golden set
								// BufferedWriter bw = new BufferedWriter(new
								// FileWriter(TestDir+i+"/tagInferred_result.txt"));
								HashMap<String, Doc> goldenSet = loadGoldset(inputData);
								
								InferenceWithAssocRules inferAR =null;
								
								if(useAddiionalTags){
									inferAR = new InferenceWithAssocRules(
											root + "/", minsup,
											minconf, minlift);
									inferAR.initialize(ARinputFile);
								}
								
								// estimate ladeled LDA
								if (estimated) {
									if (!new File(outputModelDir).isDirectory())
										topicEstimate.estimateTopic(
												outputModelDir, trainingData);
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
										trainingData,
										inferTopicForTrainingPath, infer_topK);
								ArrayList<Query> queryListForTraining = run
										.loadQueryFromFile(
												inferTopicForTrainingPath,
												trainingData);

								// construct testing query

								String inferTopicPathForTesting = currentTestDir
										+ "/query-testing-out.csv";
								TopicInfer.getTopKTopic(model, outputModelDir,
										testData, inferTopicPathForTesting,
										infer_topK);
								ArrayList<Query> queryListForTesting = run
										.loadQueryFromFile(
												inferTopicPathForTesting,
												testData);

								// load testing data text
								for (Query q : queryListForTesting) {
									String id = q.query_id;
									Doc d = goldenSet.get(id);
									String input = posPreprecessedDir
											+ d.getName();
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
									String input = posPreprecessedDir
											+ d.getName();
									String content = TermTagIndexBuilder
											.readContentIntoOneLine(input);
									q.setText(content);
									q.trueTags = d.getTaglist();
									// q.query_id = d.getName();
								}

								// infer topic llda
								if (topicInfer) {
									run.loadInferredTopic(
											inferTopicPathForTesting,
											queryListForTesting);
									run.loadInferredTopic(
											inferTopicForTrainingPath,
											queryListForTraining);
								}

								// infer topic from term_tag_index
								if (termTagIndex) {
									TermTagIndex tti = new TermTagIndex();
									tti.LoadIndexFromFile(root
											+ "term_tag_index.txt");
									for (Query q : queryListForTesting) {
										tti.AssignTags(
												q,
												TermTagIndex.INFER_NON_UNIQUE_TOKEN,
												termTagIndex_topK);
									}

									for (Query q : queryListForTraining) {
										tti.AssignTags(
												q,
												TermTagIndex.INFER_NON_UNIQUE_TOKEN,
												termTagIndex_topK);
									}

								}

								if (inferBasedOnUser) {
									inferBOU.infer(queryListForTraining,
											queryListForTraining, goldenSet);
									inferBOU.infer(queryListForTraining,
											queryListForTesting, goldenSet);

								}

								// the set storing the tags need to be excluded  in evaluation and the given tags
								HashMap<String, List<String>> givenTags = new HashMap();
								HashMap<String, List<String>> exclusiveTagOnEvaluation = new HashMap();
								
								GenerateDataForTagChange.getGivenAndExclusiveTags(root+"originalData_tag50.csv", givenTags, exclusiveTagOnEvaluation, goldenSet);
								
								
								if (useAddiionalTags) {
									
									inferAR.infer(queryListForTesting,
											goldenSet, givenTags, debug);
									inferAR.infer(queryListForTraining,
											goldenSet, givenTags, !debug);
									if(debug){
										inferAR.outputHitSet(eachFoldLogFile,queryListForTesting);
										System.err.println("miss :" + inferAR.miss + " hit:" + inferAR.hit);
									}
								}
								// spread in the network

								
								// query expansion
								
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

								IOManager.writeToLogFile(currentTestDir+"/detailedInferTagForEachComponent.txt", queryListForTesting, 50);

								
								// output trained result
								BufferedWriter bwLocal = new BufferedWriter(
										new FileWriter(currentTestDir
												+ "/recall_withTraining.csv"));
								LinearCombinationTrainerAndTester crossValidation = new LinearCombinationTrainerAndTester(
										queryListForTesting, goldenSet,
										returnTopK);
								// HashMap<String, Double> recalls =
								// crossValidation.performCrossValidation(10);
								// train a and b, for the linear combination
								double[] paras = crossValidation
										.trainParametersWithAssoR(
												queryListForTraining, 4,
												exclusiveTagOnEvaluation,
												useAddiionalTags,
												inferBasedOnUser);
								
/*								  double[] paras = new double[4]; 
								  paras[0] =1;
								  paras[1] =0.1; 
								  paras[2] = 0.1; 
								  paras[3] =  0.1;*/
//								

								
								// retu rn recall
								HashMap<String, Double> recalls = crossValidation
										.calculateRecalls(paras,queryListForTesting,exclusiveTagOnEvaluation);

								// return precision

								HashMap<String, Double> precisions = crossValidation
										.calculatPrecision(paras,
												queryListForTesting, exclusiveTagOnEvaluation);
								double sum = 0;
								for (String key : recalls.keySet()) {
									bwLocal.write(key + "," + recalls.get(key));
									result_recall.put(key, recalls.get(key));
									result_precision.put(key,
											precisions.get(key));
									sum += recalls.get(key);
									bwLocal.newLine();

								}
								double averageRecall = sum
										/ (double) (recalls.size());

								System.out.println("iter" + i + "\t" + paras[0]
										+ "\t" + paras[1] + "\t" + paras[2]
										+ "\t" + paras[3] + "\t"
										+ averageRecall);
								bwLocal.close();
								// bw.close();

							}

							BufferedWriter bw = new BufferedWriter(
									new FileWriter(resultOutput));

							// output recall and precision
							bw.write("id,recall,precision\n");
							double precision_sum = 0;
							double recall_sum = 0;
							for (String key : result_recall.keySet()) {
								bw.write(key + "," + result_recall.get(key)
										+ "," + result_precision.get(key)
										+ "\n");
								precision_sum += result_precision.get(key);
								recall_sum += result_recall.get(key);
							}
							bw.close();

							double recall= (double) recall_sum
									/ (double) result_recall.size();
							double precision = (double) precision_sum
									/ (double) result_precision.size();
							System.out
									.println("final average precision and recall:"
											+ precision
											+ "\t" + recall);
							
							final_results.put(resultOutput, recall);
							bwRepeat.write(precision+","+recall+",");
							bwRepeat.flush();
							IOManager.outputHashMap(result_recall, resultOutput);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			for (String name : final_results.keySet()) {
				System.out.println(name + ":" + final_results.get(name));
			}
			bwRepeat.write("\n");
			bwRepeat.flush();
			
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
}

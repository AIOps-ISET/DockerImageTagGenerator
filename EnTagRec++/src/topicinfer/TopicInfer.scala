package topicinfer

import scalanlp.io._
import scalanlp.stage._
import scalanlp.stage.text._
import scalanlp.text.tokenize._
import scalanlp.pipes.Pipes.global._
import edu.stanford.nlp.tmt.stage._
import edu.stanford.nlp.tmt.model.lda._
import edu.stanford.nlp.tmt.model.llda._
import scalanlp.io.CSVFile.CSVFileAsParcel
import scala.collection.mutable.ListBuffer
import java.io.PrintWriter



case class Topic(name :String, score :Double) {
	override def toString  = name +":" +score
	var Name :String = name;
	var Score :Double = score;
	
}



object TopicInfer {
	
  def compScore(t1: Topic, t2: Topic) = (t1.Score > t2.Score)
  
   def main(args: Array[String]): Unit = {
    val modelpath = "D:\\Shaowei\\research\\Folksonomies\\data\\testcase\\0\\model";
    val query ="D:\\Shaowei\\research\\Folksonomies\\data\\testcase\\0\\testDataset.csv";
     val queryout ="D:\\Shaowei\\research\\Folksonomies\\data\\testcase\\0\\testDataset-out.csv";
    val output = "D:\\Shaowei\\research\\Folksonomies\\data\\labeled model";
    //getTopKTopic(modelpath, query, queryout,10);
  }
  
  def getmodel(path: String):CVB0LabeledLDA={
    val labeledmodel = LoadCVB0LabeledLDA(path);
    return labeledmodel;
  }
  
  def getTopKTopic(labeledmodel: CVB0LabeledLDA, trainModelpath : String, querypath : String, outputpath : String, topK : Int) {
    
		val trainedModelPath = trainModelpath
		  //"D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\TopicModel tool\\llda-cvb0-c4d11853-771-a669dfd0-30f9b365";
		val modelPath = file(trainedModelPath);
		//val queryPath = args(1);
		//
		val queryPath = querypath
		  //"D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\TopicModel tool\\query.csv";
		val outputPath = outputpath
		  //"D:\\Shaowei\\research\\Folksonomies\\stackoverflow\\TopicModel tool\\query-out.csv";
		val bf = new PrintWriter(outputPath);
		
		println("Loading "+modelPath);
		//val labeledmodel = LoadCVB0LabeledLDA(modelPath);
		val model = labeledmodel.asCVB0LDA;
		// Or, for a Gibbs model, use:
		// val model = LoadGibbsLDA(modelPath);
		
		// A new dataset for inference.  (Here we use the same dataset
		// that we trained against, but this file could be something new.)
		val source = CSVFile(queryPath) ~> IDColumn(1);
		//val source = Array(1, 2, 3, 4);
		val text = {
		  source ~>                              // read from the source file
		  Column(3) ~>                           // select column containing text
		  TokenizeWith(model.tokenizer.get)      // tokenize with existing model's tokenizer
		}
		
		// Base name of output files to generate
		val output = file(modelPath, source.meta[java.io.File].getName.replaceAll(".csv",""));


		// turn the text into a dataset ready to be used with LDA
		val dataset = LDADataset(text,model.termIndex);
		
		println("Writing document distributions to "+output+"-document-topic-distributions.csv");
		val perDocTopicDistributions =  //InferCVB0DocumentTopicAssignments(model,dataset);
		  InferCVB0DocumentTopicDistributions(model, dataset);
		 
		// output 
		val list = perDocTopicDistributions.toList;
		for( i <- 0 to list.length-1){
		  val tuple = list(i);
		//  println(tuple._1);
		  val topicScores = tuple._2;
		  
		  var sList = new ListBuffer[Topic]();
		  for(j <- 0 to topicScores.length-1)
		  {
		    val topic = labeledmodel.topicName(j);
		    //println(j+" "+topic + ":" + topicScores(j))
		    val t = new Topic(topic,topicScores(j));
		    sList +=  t;
		  }
		  
		 // println(sList.length);
		
		  sList = sList.sortBy(_.Score);
		  sList = sList.reverse;
		  bf.print(i+",");
		  
		 var endIndex = topK;
		  if(topK > sList.length){
		    endIndex =  sList.length;
		  }
		  for(i <-0 to endIndex-1){
		    val t = sList(i);
		   // println(t.toString);
		    bf.print(t.toString+",");
		  }
		  bf.println();
		  
		}
		bf.close();
		
		
		//CSVFile(output+"-document-topic-distributuions.csv").write(perDocTopicDistributions);
		
		//println("Writing topic usage to "+output+"-usage.csv");
		//val usage = QueryTopicUsage(model, dataset, perDocTopicDistributions);
		//CSVFile(output+"-usage.csv").write(usage);
		
		//println("Estimating per-doc per-word topic distributions");
		//val perDocWordTopicDistributions = EstimatePerWordTopicDistributions(
		//  model, dataset, perDocTopicDistributions);
		
		//println("Writing top terms to "+output+"-top-terms.csv");
		//val topTerms = QueryTopTerms(model, dataset, perDocWordTopicDistributions, numTopTerms=50);
		//CSVFile(output+"-top-terms.csv").write(topTerms);
		
		
		  }

}
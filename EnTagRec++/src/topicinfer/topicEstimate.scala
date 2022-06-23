package topicinfer
import scalanlp.io._;
import scalanlp.stage._;
import scalanlp.stage.text._;
import scalanlp.text.tokenize._;
import scalanlp.pipes.Pipes.global._;

import edu.stanford.nlp.tmt.stage._;
import edu.stanford.nlp.tmt.model.lda._;
import edu.stanford.nlp.tmt.model.llda._;

object topicEstimate {

  def main(args: Array[String]): Unit = {
    val input = "W:\\research\\tag_recommendation\\data\\StackOverFlowForShaowei\\testcase\\0\\trainDataset_distr.csv";
    val output = "C:\\Users\\shaoweiwang.2010\\Downloads\\labeled model";
    estimateTopic(output, input);
  }
  
  def estimateTopic(output: String, input:String){
    val source = CSVFile(input) ~> IDColumn(1);

val tokenizer = {
  SimpleEnglishTokenizer() ~>            // tokenize on space and punctuation
  CaseFolder() ~>                        // lowercase everything
  WordsAndNumbersOnlyFilter() ~>         // ignore non-words and non-numbers
 // MinimumLengthFilter(3) ~>                // take terms with >=3 characters
  StopWordFilter("en")					// remove stop words
}

val text = {
  source ~>                              // read from the source file
  Column(3) ~>                           // select column containing text
  TokenizeWith(tokenizer) ~>             // tokenize with tokenizer above
  TermCounter() ~>                       // collect counts (needed below)
 // TermMinimumDocumentCountFilter(4) ~>   // filter terms in <4 docs
  TermDynamicStopListFilter(30) ~>       // filter out 30 most common terms
  DocumentMinimumLengthFilter(5)         // take only docs with >=5 terms
}

// define fields from the dataset we are going to slice against
val labels = {
  source ~>                              // read from the source file
  Column(2) ~>                           // take column two, the year
  TokenizeWith(WhitespaceTokenizer()) ~> // turns label field into an array
  TermCounter() //->                   // collect label counts
  //TermMinimumDocumentCountFilter(10)      filter labels in < 10 docs
}

val dataset = LabeledLDADataset(text, labels);

// define the model parameters
val modelParams = LabeledLDAModelParams(dataset);

// Name of the output model folder to generate
//val modelPath = file("llda-cvb0-"+dataset.signature+"-"+modelParams.signature);
val modelPath = file(output);
// Trains the model, writing to the given output path
TrainCVB0LabeledLDA(modelParams, dataset, output = modelPath, maxIterations = 500);
// or could use TrainGibbsLabeledLDA(modelParams, dataset, output = modelPath, maxIterations = 1500);
  }
}
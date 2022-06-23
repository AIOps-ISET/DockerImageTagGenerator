Input file:   (see example in the data)
In rq1, all the datasets  we used for rq1 are included.
	1. stack overflow
	2. ask ubuntu
	3. ask different
	4. super user
	5. freecode

For each dataset, following files are needed:   
 -> tag_doc_50.txt to store the tags and its corresponding objects 
 -> rawdata folder to store the objects, each objects include the description (e.g., title, content) of the object 
 -> Posts.xml that store the information about the objects, like the creation data, tags, user that post the objects, etc. it is needed as the input, but we didn't include it here, since it too bug. 
  it could be downloaded from stack exchange site (https://archive.org/details/stackexchange) for each dataset  

In rq2,  all the datasets we used for rq2 are included.
	1. stack overflow
	2. ask ubuntu
	3. ask different
	4. super user

For each dataset, there are following files: 
 -> Posts.xml. Please download from  stack exchange site (https://archive.org/details/stackexchange) for each dataset
 -> originalData.csv to store the objects we used in rq2.


 
How to run the experiments?
We have provided the runnable jar to run the experiments of RQ1 and RQ2.   
The steps are as follows:
RQ1: 
1. preprocess and prepare data
	1.1 run org.preprocess.rawTextDataPreprocessor.java to filter out the html label or tag in the text 
	-> java -cp [path of EnTagRec.jar] org.preprocess.rawTextDataPreprocessor [root of dataset]
	1.2. run org.preprocess.htmlFilter.java
	-> java -cp [path of EnTagRec.jar] org.preprocess.htmlFilter [root of dataset]
	1.3. run LabeledLDA.generateDataset.java for LLDA
	-> java -cp [path of EnTagRec.jar] LabeledLDA.generateDataset [root of dataset]
	1.4. run TermTagIndex.POS.java to analyze the pos of doc
	->java -cp [path of EnTagRec.jar] TermTagIndex.POS [root of dataset]
	1.5. run TermTagIndex.TermTagIndexBuild.java to create a term tag index (only remain the NN)
	-> java -cp [path of EnTagRec.jar] TermTagIndex.TermTagIndexBuild [root of dataset]


2. evaluate  EnTagRecU and EnTagRec
	2.1 run evaluate_EnTagRec_U.RunKtimesForEffectSizeTest_U.java
	-> java -cp [path of EnTagRec.jar] evaluate_EnTagRec_U.RunKtimesForEffectSizeTest_U [root of dataset] [repeat times]

Results:
The results for each repeatation could be found at 'testcase_repeati' under root of dataset
The final results could be found at [dataset]_repeat_precision_recall.csv under under root of dataset


rq2:
1. preprocess and prepare data
	1.1 run GenerateDataForTagChange to prepare the data 
	-> java -cp [path of EnTagRec.jar] FormatData.GenerateDataForTagChange  [root of dataset] 

2. evaluate EnTagRecU and EnTagRec++
	1.1 run RunKtimesForEffectSizeTest_onTagChangeData.java to get the results
	-> java -cp [path of EnTagRec.jar] evaluate_EnTagRec_UA.RunKtimesForEffectSizeTest_onTagChangeData  [root of dataset] 

Results:
The results for each repeatation could be found at 'testcase_addtionalTagi' under under root of dataset
The final results could be found at repeat_precision_recall_AU.csv under under root of dataset







We also provide the source code in src folder and the necessary dependent library in tools.  To run it, you need to install scala as well. 

Or import it to eclipse to run it
requirements of eclipse:
1. scala plugin 


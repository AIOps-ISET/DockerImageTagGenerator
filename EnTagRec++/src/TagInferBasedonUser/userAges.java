package TagInferBasedonUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.preprocess.QuestionInformationManager;

public class userAges {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String projectDir = args[0];
		//String projectDir = "F:\\shaowei\\research\\tag_recommendation\\folksonomy\\AlltheFourDataset\\appleSource\\";
		String postFile = projectDir + "posts.xml";
		String metaFile = projectDir + "meta.txt";
		String userInfoFile = projectDir + "Users.xml";
		int readLineN = 1000000;
		
		 String sDate= "2008-12-31";
         DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         Date eDate;
		try{
			eDate = format.parse(sDate);
		
		
		//QuestionInformationManager fac = new QuestionInformationManager();
		//fac.questionId_docId_map = fac.getRowIdToFileMap(metaFile);
		
		QuestionInformationManager qiManager;qiManager = new QuestionInformationManager();
		
			
		     qiManager.getRowIdToFileMap(metaFile);
		     HashMap<String, Date> creationDates = qiManager.getInformationForUser(userInfoFile);
		     int sum = 0;
			 for(Date cDate : creationDates.values()){
				 sum += daysBetween(cDate, eDate);
			 }
			 System.out.println(creationDates.size());
			 System.out.println( (double)sum/(double)creationDates.size());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
   }

}

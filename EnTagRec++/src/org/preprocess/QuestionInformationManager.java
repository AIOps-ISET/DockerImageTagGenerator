package org.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.netlib.util.intW;

public class QuestionInformationManager {
	
	private HashMap<Integer, Integer> questionId_docId_map;
	private HashMap<String, Integer> docMap;
	private HashMap<Integer, Question> docId_question_map ;
	private HashMap<String, ArrayList<Question>> userId_questionList_map ;
	private HashMap<String, String> docId_User_Map;
	
	private ArrayList<String> user;

	
	public HashMap<String, String> getDocId_User_Map() {
		return docId_User_Map;
	}

	public void setDocId_User_Map(HashMap<String, String> docId_User_Map) {
		this.docId_User_Map = docId_User_Map;
	}

	public HashMap<Integer, Integer> getQuestionId_docId_map() {
		return questionId_docId_map;
	}

	public void setQuestionId_docId_map(
			HashMap<Integer, Integer> questionId_docId_map) {
		this.questionId_docId_map = questionId_docId_map;
	}

	public HashMap<String, Integer> getDocMap() {
		return docMap;
	}

	public void setDocMap(HashMap<String, Integer> docMap) {
		this.docMap = docMap;
	}

	public HashMap<Integer, Question> getDocId_question_map() {
		return docId_question_map;
	}

	public void setDocId_question_map(HashMap<Integer, Question> docId_question_map) {
		this.docId_question_map = docId_question_map;
	}

	public HashMap<String, ArrayList<Question>> getUserId_questionList_map() {
		return userId_questionList_map;
	}

	public void setUserId_questionList_map(
			HashMap<String, ArrayList<Question>> userId_questionList_map) {
		this.userId_questionList_map = userId_questionList_map;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String projectDir = "F:\\shaowei\\research\\tag_recommendation\\folksonomy\\AlltheFourDataset\\appleSource\\";
		String postFile = projectDir + "posts.xml";
		String metaFile = projectDir + "meta.txt";
		int readLineN = 100000;
		QuestionInformationManager fac = new QuestionInformationManager();
		fac.getRowIdToFileMap(metaFile);
		fac.getContentAndTag(postFile, projectDir,readLineN);
		 
	}
	
	public QuestionInformationManager(){
		
	}
	
	
	
	/*
	 * input: metaFile 
	 * return a hashmap contains the rowid and fileid
	 */
	public void getRowIdToFileMap(String metaFile) throws IOException{
		System.out.println("load map from: " + metaFile);
		BufferedReader br = new BufferedReader(new FileReader(metaFile));
		this.questionId_docId_map = new HashMap<Integer, Integer>();
		this.user = new ArrayList<>();
		while(br.ready()){
			String line  = br.readLine();
			String[] tmp = line.split("\t");
			Integer docId = Integer.parseInt(tmp[0]);
			Integer questionId = Integer.parseInt(tmp[1]);
			questionId_docId_map.put( questionId, docId);
			
			String userId = tmp[2];
			this.user.add(userId);
			
			
		}
		br.close();
		
		
	}
	
	// for freecode input format
	public void getContentAndTag(String userInfoFile){
		userId_questionList_map = new HashMap();
		docId_User_Map = new HashMap();
		try{
		BufferedReader br = new BufferedReader(new FileReader(userInfoFile));
		while(br.ready()){
			String line = br.readLine();
			
			String[] ele = line.split("\t");
			String userId = ele[1];
			String docId = ele[0];
			/*
			Question q = new Question(docId);
			if(!userId_questionList_map.containsKey(userId)){
				ArrayList<Question> questionList = new ArrayList();
				questionList.add(q);
				userId_questionList_map.put(userId, questionList );
			}else{
				userId_questionList_map.get(userId).add(q);
			}
			*/
			docId_User_Map.put(docId, userId);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	// for project except for freecode
	public void getContentAndTag(String postFile, String outputDir,int readlineN) throws IOException{
		System.out.println("Start parsing xml file............");
		BufferedReader br = new BufferedReader(new FileReader(postFile));
		docMap = new HashMap<String, Integer>();
		docId_question_map = new HashMap();
		docId_User_Map = new HashMap();
		userId_questionList_map = new HashMap();
		String temp = null;
		int j = 0;
		int fileNum = 1;
		
		
		while (((temp = br.readLine()) != null) ) 
		
		{
			j++;
			if (j == 1 || j == 2) {
				continue;
			}
			if(fileNum>readlineN)
				break;
			

			StringBuffer sb = new StringBuffer();

			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			sb.append("<posts>");
			sb.append(temp);
			sb.append("</posts>");
			Document doc = null;
			try {
				doc = DocumentHelper.parseText(sb.toString());
				Element rootElt = doc.getRootElement();

				Element ele = rootElt.element("row");
				int questionId = Integer.parseInt(ele.attribute("Id").getText());
				
				if(questionId== 38386)
					System.out.println();
				//skip the one not in our dataset.
				if(!this.questionId_docId_map.containsKey(questionId))
					continue;
				int docId = this.questionId_docId_map.get(questionId);
				
				int PostTypeId = Integer.parseInt(ele.attribute("PostTypeId")
						.getText());
                String sDate=ele.attribute("CreationDate").getText().replace("T", " ");
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");
                Date date = format.parse(sDate);           
                String tags = null;
				String userId = null;
				if (PostTypeId == 1) {
					
					
					try {
						tags = ele.attribute("Tags").getText().replace("<", "").replace(">", " ") ;
						userId = ele.attribute("OwnerUserId").getText();
					} catch (Exception e) {

						continue;
					}
					
					//File tmp = new File(outputDir
					//		+ String.valueOf(String.valueOf(fileNum)));
					//if (!tmp.exists()) {
					//	tmp.createNewFile();
					//}
					String ID = ele.attribute("Id").getText();
					String Title = ele.attribute("Title").getText().replace("\n", "") ;
					String Body = ele.attribute("Body").getText().replace("\n","") ;
					//FileOutputStream tmpout = new FileOutputStream(tmp, true);
					//tmpout.write(Title.getBytes("utf-8"));
					//tmpout.write(Tags.getBytes("utf-8"));
					//tmpout.write(Body.getBytes("utf-8"));
					//tmpout.close();
					if(!this.questionId_docId_map.containsKey(questionId))
						continue;
					
					// add question to docid map
					Question q = new Question(docId, questionId, tags,date,Body, Title);
					docId_question_map.put(docId, q);
					
					// build userid and quesiton association
					if(!userId_questionList_map.containsKey(userId)){
						ArrayList<Question> questionList = new ArrayList();
						questionList.add(q);
						userId_questionList_map.put(userId, questionList );
					}else{
						userId_questionList_map.get(userId).add(q);
					}
					StringBuffer metaSb = new StringBuffer();
					metaSb.append(fileNum + "\t");
					metaSb.append(ID + "\t");
					metaSb.append("\n");
					//outInfo.write(metaSb.toString().getBytes("utf-8"));
					docMap.put(ID, fileNum);
					
					fileNum++;
					if (fileNum % 1000 == 0) {
						System.out.print("processing file: "+fileNum+ "\t\n");
					}

				} else if (PostTypeId == 2) {
					String sQuestionID = ele.attribute("ParentId").getText();
					String Body = ele.attribute("Body").getText() + "\n";
					
					try {
						tags = ele.attribute("Tags").getText().replace("<", "").replace(">", " ") ;
						userId = ele.attribute("OwnerUserId").getText();
					} catch (Exception e) {

						continue;
					}
					
					// collect the tags associate to user
					Question q = new Question(0, Integer.parseInt(sQuestionID), tags,date,null, null);
					if(!userId_questionList_map.containsKey(userId)){
						ArrayList<Question> questionList = new ArrayList();
						questionList.add(q);
						userId_questionList_map.put(userId, questionList );
					}else{
						userId_questionList_map.get(userId).add(q);
					}
					
					
					if (!docMap.containsKey(sQuestionID)) {
						
						StringBuffer metaSb = new StringBuffer();
						metaSb.append(fileNum + "\t");
						metaSb.append(sQuestionID + "\t");
						metaSb.append("\n");
						//outInfo.write(metaSb.toString().getBytes("utf-8"));
						docMap.put(sQuestionID, fileNum);
						
						fileNum++;
					} else {
						
					}
					
					
				}
				
				docId_User_Map.put(String.valueOf(docId), userId);
				
			} catch (DocumentException e) {
				e.printStackTrace();
				continue;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		//outInfo.close();
		br.close();
		
		System.out.println("End parsing xml file..........");
		
		// output questions to files
		
		String outputFile = outputDir + "QuestionInformation.cvs";
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.write("docId,questionId,createdDate,tags,content\n");
		for(Question q : docId_question_map.values()){
			bw.write(q.getDocId()+","+q.getRowId()+"," + q.getTitle() +","+q.getCreatedDate()+","+q.getTags()+","+q.getContent()+"\n");
			
		}
		bw.close();
		
		// output tags information of each user/owner
		outputFile = outputDir + "userTagInformation.txt";
		
		bw = new BufferedWriter(new FileWriter(outputFile));
		for(String userId : userId_questionList_map.keySet()){
			HashMap<String,Integer> tag_count = new HashMap();
			ArrayList<Question> list = userId_questionList_map.get(userId);
			for(int i =0; i < list.size(); i++){
				Question q= list.get(i);
				String[] tags = q.getTags().trim().split(" +");
				for(String tag: tags){
					if(tag_count.containsKey(tag))
						tag_count.put(tag, tag_count.get(tag)+1);
					else
						tag_count.put(tag, 1);
				}
				
			}
			
			bw.write(userId+"\t");
			int sum =0;
			for(String tag : tag_count.keySet()){
				bw.write(tag+":"+tag_count.get(tag)+"\t");
				sum += tag_count.get(tag);
			}
			
			bw.write("\n");
			double average = (double)sum/(double)tag_count.size();
			bw.write("avg:" + average+"\n");
		}
		bw.close();

	}

	public HashMap<String, Date> getInformationForUser(String userInfoFile) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, Date> results = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(userInfoFile));
		String temp = null;
		int j = 0;
		while (((temp = br.readLine()) != null) ) 
			
		{
			j++;
			if (j == 1 || j == 2) {
				continue;
			}
			if(j%1000 ==0)
				System.out.println(j);
			
			StringBuffer sb = new StringBuffer();

			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			sb.append("<posts>");
			sb.append(temp);
			sb.append("</posts>");
			Document doc = null;
			try {
				doc = DocumentHelper.parseText(sb.toString());
				Element rootElt = doc.getRootElement();

				Element ele = rootElt.element("row");
				String userId = ele.attribute("Id").getText();
				
				if(!this.user.contains(userId)){
					continue;
				}
				
                String sDate=ele.attribute("CreationDate").getText().replace("T", " ");
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");
                Date cDate =format.parse(sDate);
                
                results.put(userId, cDate);
                
                if(results.size() == this.user.size() || j > 200000)
                	break;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return results;
	}

}


class Question{
	private Date createdDate;
	private int docId;
	private int rowId;
	private String tags;
	private String content;
	private String title;
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Question(int docId, int rowId, String tags, Date postDate, String content, String title){
		this.docId = docId;
		this.rowId = rowId;
		this.tags = tags;
		this.createdDate= postDate;
		this.content = content;
		this.title = title;
		
	}

	public Question(int docId) {
		// TODO Auto-generated constructor stub
		this.docId = docId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getPostDate() {
		return createdDate;
	}

	public void setPostDate(Date postDate) {
		this.createdDate = postDate;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
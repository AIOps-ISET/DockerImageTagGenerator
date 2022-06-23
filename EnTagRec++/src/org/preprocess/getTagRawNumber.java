package org.preprocess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class getTagRawNumber {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFile = "O:\\shaowei\\folksonomy\\AlltheFourDataset\\appleSource\\appleSource.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = null;
			HashMap<String, Integer> pool = new HashMap();
			while((line = br.readLine())!=null){
				String[] strs = line.split("\t");
				for(int i =3; i< strs.length; i++){
					pool.put(strs[i], 1);
				}
			}
			br.close();
			System.out.println(pool.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

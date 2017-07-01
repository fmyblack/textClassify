package com.fmyblack.word.rmm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class WordDictionary{

	Set<String> wordDic = new HashSet<String>();
	int maxLength = 0;
	
	private static WordDictionary ins = null;
	
	public static synchronized WordDictionary getIns() {
		if(ins == null) {
			ins = new WordDictionary();
		}
		return ins;
	}
	
	public boolean containsWord(String s) {
		return this.wordDic.contains(s);
	}
	
	public int getMaxLength() {
		return this.maxLength;
	}
	
	private WordDictionary() {
		String wordFile = "/Users/fmyblack/javaproject/textClassify/dic/nls_dict.data";
//		String wordFile = "/Users/fmyblack/data/ik/main.dic";
		try {
			initWordDic(wordFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initWordDic(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = null;
		while((line = br.readLine()) != null) {
			line = line.split("\t")[0];
			line = line.trim();
			int tmpLength = line.length();
			if(tmpLength <= 1) {
				continue;
			}
			wordDic.add(line);
			if(tmpLength > maxLength) {
				maxLength = tmpLength;
			}
		}
	}
}

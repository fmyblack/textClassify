package com.fmyblack.textClassify.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fmyblack.word.WordSegmenter;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

public class Document {

	public String filePath;
	Map<String, Integer> wordsFreq = new HashMap<String, Integer>();
	
	public Document(String filePath) {
		this.filePath = filePath;
	}
	
	public boolean containsWord(String word) {
		return wordsFreq.containsKey(word);
	}
	
	public int getWordFreq(String word) {
		if(wordsFreq.containsKey(word)) {
			return wordsFreq.get(word);
		}
		return 0;
	}
	
	public void incrWord(String word) {
		wordsFreq.put(word, wordsFreq.containsKey(word) ? wordsFreq.get(word) + 1 : 1);
	}
	
	public Map<String, Integer> docToWords(WordSegmenter ws) {
		try(BufferedReader breader = Files.newReader(new File(this.filePath), Charsets.UTF_8);) {
			String line = null;
			while(null != (line = breader.readLine())) {
				line = line.replaceAll("\\p{Punct}", " ")
						.replaceAll("\\pP", " ")
						.replaceAll("　", " ")
						.replaceAll("\\p{Blank}", " ")
						.replaceAll("\\p{Space}", " ")
						.replaceAll("\\p{Cntrl}", " ");
				
				for (String sen : Splitter.on(" ").omitEmptyStrings()
						.splitToList(line)) {
					for(String word : ws.segment(sen)) {
						incrWord(word);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return wordsFreq;
	}
}

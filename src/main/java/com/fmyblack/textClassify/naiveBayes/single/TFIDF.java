package com.fmyblack.textClassify.naiveBayes.single;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fmyblack.textClassify.tfidf.TFIDFBase;
import com.fmyblack.textClassify.tfidf.TagBase;

public class TFIDF extends TFIDFBase{

	private TFIDF(String[] words) {
		super(words);
	}
	
	public TFIDF(int documentsNum, Set<String> words) {
		super(documentsNum, words);
		// TODO Auto-generated constructor stub
	}
	
	public void fit(List<TagBase> tags) {
		Map<String, Double> wordsIdf = idf(tags);
		for(TagBase tagBase : tags) {
			Tag tag = (Tag) tagBase;
			tag.train(wordsIdf, this.documentsNum);
		}
	}
	
	public void save(FileWriter file) throws IOException {
		StringBuilder sb = new StringBuilder();
		for(String word : this.words) {
			sb.append(word + " ");
		}
		file.write(sb.toString() + "\r\n");
	}
	
	public static TFIDF load(String savedWord) {
		return new TFIDF(savedWord.split(" "));
	}
}

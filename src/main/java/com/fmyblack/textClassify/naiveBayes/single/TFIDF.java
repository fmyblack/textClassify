package com.fmyblack.textClassify.naiveBayes.single;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TFIDF {

	Set<String> words;
	int documentsNum;
	
	private TFIDF(String[] words) {
		this.words = new HashSet<String>();
		this.words.addAll(Arrays.asList(words));
	}
	
	public TFIDF(int documentsNum, Set<String> words) {
		this.documentsNum = documentsNum;
		this.words = words;
	}
	
	public void fit(List<Tag> tags) {
		Map<String, Double> wordsIdf = idf(tags);
		for(Tag tag : tags) {
			tag.train(wordsIdf, this.documentsNum);
		}
	}
	
	public Map<String, Double> idf(List<Tag> tags) {
		Map<String, Double> wordsIdf = new HashMap<String, Double>();
		for(String word : this.words) {
			int tdAll = tdAll(tags, word);
			double idf = caculateIdf(tdAll);
			wordsIdf.put(word, idf);
		}
		return wordsIdf;
	}
	
	public int tdAll(List<Tag> tags, String word) {
		int tdAll = 0;
		for(Tag tag : tags) {
			tdAll += tag.td(word);
		}
		return tdAll;
	}
	
	public double caculateIdf(int td) {
		return Math.log( (this.documentsNum + 2) * 1.0d / (td + 1) );
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
	
	public static void main(String[] args) {
		System.out.println(Math.log(5*1.0d/3));
	}
}

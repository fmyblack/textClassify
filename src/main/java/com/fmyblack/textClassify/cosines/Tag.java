package com.fmyblack.textClassify.cosines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fmyblack.textClassify.tfidf.Document;
import com.fmyblack.textClassify.tfidf.TagBase;

import breeze.linalg.cond;

public class Tag extends TagBase{

	// 训练前
	List<Document>	docs	= new ArrayList<Document>();
	String			name;
	
	// 训练后
	Set<String> words = new HashSet<String>();
	Map<String, Double> wordsTfidf = new HashMap<String, Double>();
	double bottom = 1.0;
	
	public Tag(String name) {
		this.name = name;
	}
	
	public void addDoc(Document doc) {
		docs.add(doc);
	}
	
	public void train(Map<String, Double> idf) {
		initWords();
		for(String word : this.words) {
			double wordTfidf = idf.get(word) * this.tf(word);
			wordsTfidf.put(word, wordTfidf);
			this.bottom += Math.pow(wordTfidf, 2);
		}
		this.bottom = Math.sqrt(this.bottom);
	}
	
	public double caculateAccuracy(Map<String, Double> idf, Map<String, Integer> wordsFreq) {
		double accuracy = 0.0;
		double trainBottom = 1.0;
		for(Map.Entry<String, Integer> entry : wordsFreq.entrySet()) {
			String word = entry.getKey();
			double freq = entry.getValue();
			if(!idf.containsKey(word)) {
				continue;
			}
			double trainTfidf = freq * idf.get(word);
			if(this.getTagWordTfidf(word) > 0.0) {
				accuracy += trainTfidf * this.getTagWordTfidf(word);
			}
			trainBottom += Math.pow(trainTfidf, 2);
		}
		trainBottom = Math.sqrt(trainBottom);
		return accuracy / this.bottom / trainBottom;
	}
	
	public double getTagWordTfidf(String word) {
		if(this.wordsTfidf.containsKey(word)) {
			return this.wordsTfidf.get(word);
		} else {
			return 0.0;
		}
	}
	
	private void initWords() {
		for(Document doc : docs) {
			this.words.addAll(doc.getWords());
		}
	}
	
	private int tf(String word) {
		int tf = 0;
		for(Document doc : this.docs) {
			tf += doc.tf(word);
		}
		return tf;
	}
	
	public int td(String word) {
		int td = 0;
		for(Document doc : docs) {
			if(doc.containsWord(word)) {
				td++;
			}
		}
		return td;
	}
}

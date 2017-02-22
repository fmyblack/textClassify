package com.fmyblack.textClassify.naiveBayes.single;

import java.util.*;

public class Tag {

	List<Document> docs = new ArrayList<Document>();
	String name;
	
	boolean isTrained = false;
	Set<String> words = new HashSet<String>();
	Map<String, Double> wordsProbility = new HashMap<String, Double>();
	int wordsNum;
	double tagProbility = 1.0;
	
	public Tag(String name) {
		this.name = name;
	}
	
	public void addDoc(Document doc) {
		docs.add(doc);
	}
	
	public void train(Map<String, Double> wordsIdf, int documentsNum) {
		this.initWords();
		this.caculateWordsProbility(wordsIdf);
		this.tagProbility = docs.size() * 1.0 / documentsNum;
		this.isTrained = true;
	}
	
	private void caculateWordsProbility(Map<String, Double> wordsIdf) {
		for(String word : this.words) {
			Double idf = wordsIdf.get(word);
			this.wordsProbility.put(word, caculateWordProbility(idf, word));
		}
	}
	
	private double caculateWordProbility(Double idf, String word) {
		if(idf == null) {
			return 0;
		} else {
			return this.tf(word) * 1.0 / this.wordsNum * idf;
		}
	}
	
	private void initWords() {
		for(Document doc : docs) {
			this.words.addAll(doc.getWords());
			this.wordsNum += doc.getWordsNum();
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
	
	public Result caculateAccuracy(Map<String, Integer> wordsFre) {
		double accuracy = 1.0;
		for(Map.Entry<String, Integer> wordFre : wordsFre.entrySet()) {
			String wordName = wordFre.getKey();
			int freq = wordFre.getValue();
			Double tfidf = this.wordsProbility.get(wordName);
			if(tfidf == null || tfidf.equals(0)) {
				continue;
			}
			accuracy *= tfidf * freq;
		}
		accuracy *= this.tagProbility;
		return new Result(this.name, accuracy);
	}
	
	@Override
	public String toString() {
		return this.name + this.wordsProbility.toString();
	}
}

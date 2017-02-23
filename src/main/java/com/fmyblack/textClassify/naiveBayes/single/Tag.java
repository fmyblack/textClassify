package com.fmyblack.textClassify.naiveBayes.single;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Tag{

	private static double lambda = 0.00001;
	
	// 训练前
	List<Document> docs = new ArrayList<Document>();
	String name;
	
	// 训练后
	boolean isTrained = false;
	Set<String> words = new HashSet<String>();
	Map<String, Double> wordsProbility = new HashMap<String, Double>();
	int wordsNum;
	double tagProbility = 1.0;
	
	private Tag(){};
	
	public Tag(String name) {
		this.name = name;
	}
	
	public void addDoc(Document doc) {
		docs.add(doc);
	}
	
	public void train(Map<String, Double> wordsIdf, int documentsNum) {
//		this.initWords();
		this.initWords(wordsIdf);
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
			return (this.tf(word) + lambda ) * 1.0 / ( (this.wordsNum + 1 ) * lambda ) * this.wordsNum * idf;
		}
	}
	
	private void initWords() {
		for(Document doc : docs) {
			this.words.addAll(doc.getWords());
			this.wordsNum += doc.getWordsNum();
		}
	}
	
	private void initWords(Map<String, Double> wordsIdf) {
		this.words = wordsIdf.keySet();
		this.wordsNum = this.words.size();
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
			accuracy += Math.log(tfidf * freq);
		}
		accuracy += Math.log(this.tagProbility);
		return new Result(this.name, accuracy);
	}
	
	@Override
	public String toString() {
		return this.name + this.wordsProbility.toString();
	}
	
	public void save(FileWriter file) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name + "\t");
		sb.append(this.tagProbility + "\t");
		for(Map.Entry<String, Double> entry : this.wordsProbility.entrySet()) {
			sb.append(entry.getKey() + ":" + entry.getValue() + ";");
		}
		file.write(sb.toString() + "\r\n");
	}
	
	public static Tag load(String savedTag) {
		Tag tag = new Tag();
		String[] cols = savedTag.split("\t");
		tag.name = cols[0];
		tag.tagProbility = Double.parseDouble(cols[1]);
		for(String wordProbility : cols[2].split(";")) {
			String word = wordProbility.split(":")[0];
			String probility = wordProbility.split(":")[1];
			tag.wordsProbility.put(word, Double.parseDouble(probility));
		}
		return tag;
	}
}

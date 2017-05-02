package com.fmyblack.textClassify.naiveBayes.single;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import com.fmyblack.textClassify.tfidf.Document;
import com.fmyblack.textClassify.tfidf.TagBase;

public class Tag extends TagBase{

	// 训练前
	List<Document> docs = new ArrayList<Document>();
	String name;
	
	// 训练后
	boolean isTrained = false;
	Set<String> words = new HashSet<String>();
	Map<String, Double> wordsProbility = new HashMap<String, Double>();
	int wordsNum;
	double zeroWordProbility = -1;
	double tagProbility = 1.0;
	
	private Tag(){};
	
	public Tag(String name) {
		this.name = name;
	}
	
	public void addDoc(Document doc) {
		docs.add(doc);
	}
	
	public void train(Map<String, Double> wordsIdf, int documentsNum) {
		this.initWords();
//		this.initWords(wordsIdf);
		this.caculateWordsProbility(wordsIdf);
		this.tagProbility = docs.size() * 1.0 / documentsNum;
		this.isTrained = true;
	}
	
	private void caculateWordsProbility(Map<String, Double> wordsIdf) {
		int allWordsNum = wordsIdf.size();
		for(Map.Entry<String, Double> entry : wordsIdf.entrySet()) {
			String word = entry.getKey();
			double idf = entry.getValue();
			int wordTf = this.tf(word);
			if(wordTf == 0) {
				caculateZeroTfProbility(idf, allWordsNum);
			} else {
				this.wordsProbility.put(word, caculateWordProbility(idf, wordTf, allWordsNum));
			}
		}
	}
	
	private void caculateZeroTfProbility(double idf, int allWordsNum) {
		if(this.zeroWordProbility < 0) {
			this.zeroWordProbility = NaiveBayesModel.lambda / ( this.wordsNum + allWordsNum * NaiveBayesModel.lambda ) * Math.pow(idf, NaiveBayesModel.idfPower);
		}
	}
	
	private double caculateWordProbility(double idf, int tf, int allWordsNum) {
		return (tf + NaiveBayesModel.lambda ) * 1.0 / ( this.wordsNum + allWordsNum * NaiveBayesModel.lambda ) * Math.pow(idf, NaiveBayesModel.idfPower);
	}
	
	private void initWords() {
		for(Document doc : docs) {
//			this.words.addAll(doc.getWords());
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
	
	public Result caculateAccuracy(Map<String, Integer> wordsFre, Set<String> trainWords) {
		double accuracy = 0.0;
		for(Map.Entry<String, Integer> wordFre : wordsFre.entrySet()) {
			String wordName = wordFre.getKey();
			int freq = wordFre.getValue();
			if(!trainWords.contains(wordName)) {
				continue;
			}
			double wordProbility = this.getWordProbility(wordName);
			accuracy += Math.log(wordProbility * freq + 1);
		}
		accuracy += Math.log(this.tagProbility + 1);
		return new Result(this.name, accuracy);
	}
	
	private double getWordProbility(String wordName) {
		if(this.wordsProbility.containsKey(wordName)) {
			return this.wordsProbility.get(wordName);
		} else {
			return this.zeroWordProbility;
		}
	}
	
	@Override
	public String toString() {
		return this.name + this.wordsProbility.toString();
	}
	
	public void save(FileWriter file) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name + "\t");
		sb.append(this.tagProbility + "\t");
		sb.append(this.zeroWordProbility + "\t");
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
		tag.zeroWordProbility = Double.parseDouble(cols[2]);
		for(String wordProbility : cols[3].split(";")) {
			String word = wordProbility.split(":")[0];
			String probility = wordProbility.split(":")[1];
			tag.wordsProbility.put(word, Double.parseDouble(probility));
		}
		return tag;
	}
}

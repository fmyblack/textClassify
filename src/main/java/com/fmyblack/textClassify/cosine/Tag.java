package com.fmyblack.textClassify.cosine;

import java.util.Map;

import com.fmyblack.textClassify.IDF;
import com.fmyblack.textClassify.Result;
import com.fmyblack.textClassify.doc.TFDocIterable;

public class Tag {

	String				name;
	TFDocIterable		docs		= null;
	IDF idf = null;
	int					idfPower	= 1;
	Map<String, Double>	wordsProbility;
	int					totalWordsNum;
	double bottom = 0.0;
	
	public Tag(String name, TFDocIterable docs) {
		this.name = name;
		this.docs = docs;
		wordsProbility = docs.words;
		totalWordsNum = docs.getTotalWordsNum();
	}
	
	public void fit(IDF idf) {
		this.idf = idf;
		for (Map.Entry<String, Double> entry : wordsProbility.entrySet()) {
			String word = entry.getKey();
			double tf = entry.getValue();
			double prop = caculateWordProbility(tf, idf.getIdf(word));
			wordsProbility.put(word, prop);
			bottom += Math.pow(prop, 2);
		}
		bottom = Math.sqrt(bottom);
	}
	
	public double caculateWordProbility(double tf, double idf) {
		return tf * 1.0 / this.totalWordsNum * Math.pow(idf, idfPower);
	}
	
	public Result caculateAccuracy(Map<String, Integer> wordsFreq, int wordsNum) {
		double accuracy = 0.0;
		double classifyBottom = 0.0;
		for(Map.Entry<String, Integer> entry : wordsFreq.entrySet()) {
			String word = entry.getKey();
			double freq = entry.getValue();
			if(!idf.containsWord(word)) {
				continue;
			}
			double idfValue = idf.getIdf(word);
			double classifyTfIdf = freq * idfValue / wordsNum;
			classifyBottom += Math.pow(classifyTfIdf, 2);
			if(!wordsProbility.containsKey(word)) {
				continue;
			}
			accuracy += wordsProbility.get(word) * classifyTfIdf;
		}
		classifyBottom = Math.sqrt(classifyBottom);
		return new Result(this.name, accuracy / this.bottom / classifyBottom);
	}
	
}

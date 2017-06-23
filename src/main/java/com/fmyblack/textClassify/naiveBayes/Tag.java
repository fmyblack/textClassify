package com.fmyblack.textClassify.naiveBayes;

import java.util.*;

import com.fmyblack.textClassify.IDF;
import com.fmyblack.textClassify.doc.TFDocIterable;

public class Tag {

	String				name;
	TFDocIterable		docs		= null;
	int					idfPower	= 3;
	Map<String, Double>	wordsProbility;
	int					totalWordsNum;

	public Tag(String name, TFDocIterable docs) {
		this.name = name;
		this.docs = docs;
		wordsProbility = docs.words;
		totalWordsNum = docs.getTotalWordsNum();
	}

	public void fit(IDF idf) {
		for (Map.Entry<String, Double> entry : wordsProbility.entrySet()) {
			String word = entry.getKey();
			double tf = entry.getValue();
			double prop = caculateWordProbility(tf, idf.getIdf(word));
			wordsProbility.put(word, prop);
		}
	}

	public double caculateWordProbility(double tf, double idf) {
		return tf * 1.0 / this.totalWordsNum * Math.pow(idf, idfPower);
	}

	public Result caculateAccuracy(Map<String, Integer> wordsFre) {
		double accuracy = 0.0;
		for (Map.Entry<String, Integer> wordFre : wordsFre.entrySet()) {
			String wordName = wordFre.getKey();
			int freq = wordFre.getValue();
			if (!wordsProbility.containsKey(wordName)) {
				continue;
			}
			double wordProbility = this.getWordProbility(wordName);
			accuracy += Math.log(wordProbility * freq + 1);
		}
		return new Result(this.name, accuracy);
	}

	private double getWordProbility(String word) {
		return this.wordsProbility.get(word);
	}

}

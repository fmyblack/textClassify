package com.fmyblack.textClassify.tfidf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fmyblack.textClassify.naiveBayes.single.Tag;

public class TFIDFBase {

	protected Set<String> words;
	protected int documentsNum;
	
	protected TFIDFBase(String[] words) {
		this.words = new HashSet<String>();
		this.words.addAll(Arrays.asList(words));
	}
	
	public TFIDFBase(int documentsNum, Set<String> words) {
		this.documentsNum = documentsNum;
		this.words = words;
	}
	
	public Set<String> getWords() {
		return this.words;
	}
	
	public Map<String, Double> idf(List<TagBase> tags) {
		Map<String, Double> wordsIdf = new HashMap<String, Double>();
		for(String word : this.words) {
			int tdAll = tdAll(tags, word);
			double idf = caculateIdf(tdAll);
			wordsIdf.put(word, idf);
		}
		return wordsIdf;
	}
	
	public int tdAll(List<TagBase> tags, String word) {
		int tdAll = 0;
		for(TagBase tag : tags) {
			tdAll += tag.td(word);
		}
		return tdAll;
	}
	
	public double caculateIdf(int td) {
		return Math.log( (this.documentsNum + 2) * 1.0d / (td + 1) );
	}
	
	public static void main(String[] args) {
		System.out.println(Math.log(5*1.0d/3));
	}
}

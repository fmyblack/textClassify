package com.fmyblack.textClassify.tfidf;

import java.util.*;

public class Document{

	private Map<String, Integer> wordsFrequency = new HashMap<String, Integer>();
	private int wordsNum = 0;
	
	public Document(List<String> words) {
		wordsNum = words.size();
		for(String word : words) {
			Integer oldFrequency = wordsFrequency.get(word);
			int frequency = oldFrequency == null ? 1 : oldFrequency + 1;
			wordsFrequency.put(word, frequency);
		}
	}
	
	public Map<String, Integer> getWordsFrequency () {
		return this.wordsFrequency;
	}
	
	public Set<String> getWords() {
		return this.wordsFrequency.keySet();
	}
	
	public int getWordsNum() {
		return this.wordsNum;
	}
	
	public int tf(String word) {
		return wordsFrequency.containsKey(word) ? wordsFrequency.get(word) : 0;
	}
	
	public boolean containsWord(String word) {
		return wordsFrequency.containsKey(word);
	}
	
	@Override
	public String toString() {
		String s = "";
		int i = 0;
		for(String key : wordsFrequency.keySet()) {
			s += key;
			i++;
			if(i > 3) {
				break;
			}
		}
		return s;
	}
}

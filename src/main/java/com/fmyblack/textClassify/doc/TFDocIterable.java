package com.fmyblack.textClassify.doc;

import java.util.List;
import java.util.Map;

import com.fmyblack.word.WordSegmenter;

public class TFDocIterable extends DocIterable{

	public TFDocIterable(List<String> docsPath, WordSegmenter ws) {
		super(docsPath, ws);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void mergeWords(Map<String, Integer> docWords) {
		// TODO Auto-generated method stub
		for(Map.Entry<String, Integer> entry : docWords.entrySet()) {
			String word = entry.getKey();
			totalWordsNum += entry.getValue();
			if(words.containsKey(word)) {
				words.put(word, words.get(word) + entry.getValue());
			} else {
				words.put(word, 0.0 + entry.getValue());
			}
		}
	}
	
	public int getTotalWordsNum() {
		return this.totalWordsNum;
	}
}

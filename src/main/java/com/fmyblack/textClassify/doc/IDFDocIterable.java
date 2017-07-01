package com.fmyblack.textClassify.doc;

import java.util.List;
import java.util.Map;

import com.fmyblack.word.WordSegmenter;

public class IDFDocIterable extends DocIterable{

	public IDFDocIterable(List<String> docsPath, WordSegmenter ws) {
		super(docsPath, ws);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void mergeWords(Map<String, Integer> docWords) {
		for(String word : docWords.keySet()) {
			words.put(word, 0.0);
		}
	}

	public int df(String word) {
		int i = 0;
		for(Document doc : this) {
			if(doc.containsWord(word)) {
				i++;
			}
		}
		return i;
	}
}

package com.fmyblack.textClassify;

import java.util.Map;

import com.fmyblack.textClassify.doc.IDFDocIterable;

public class IDF {

	IDFDocIterable		docs;
	Map<String, Double>	idfs	= null;

	public IDF(IDFDocIterable docs) {
		this.docs = docs;
		idfs = docs.words;
		fit();
	}

	private void fit() {
		int docsSize = docs.size();
		for (String word : idfs.keySet()) {
			int df = docs.df(word);
			double idf = Math.log((docsSize + 2) * 1.0d / (df + 1));
			idfs.put(word, idf);
		}
	}

	public boolean containsWord(String word) {
		return this.idfs.containsKey(word);
	}
	
	public double getIdf(String word) {
		return idfs.get(word);
	}
}

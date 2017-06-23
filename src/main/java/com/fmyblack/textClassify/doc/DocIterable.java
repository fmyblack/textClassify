package com.fmyblack.textClassify.doc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fmyblack.word.WordSegmenter;

public abstract class DocIterable implements Iterable<Document> {

	protected Document[] docs = null;
	protected int totalWordsNum = 0;
	public Map<String, Double> words = new HashMap<String, Double>();
	
	public DocIterable(List<String> docsPath, WordSegmenter ws) {
		docs = new Document[docsPath.size()];
		for(int i = 0; i < docsPath.size(); i++) {
			docs[i] = new Document(docsPath.get(i));
			mergeWords(docs[i].docToWords(ws));
		}
	}
	
	protected abstract void mergeWords(Map<String, Integer> docWords);
	
	public int size() {
		return docs.length;
	}
	
	@Override
	public Iterator<Document> iterator() {
		// TODO Auto-generated method stub
		return new Iterator<Document>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return index < docs.length;
			}

			@Override
			public Document next() {
				// TODO Auto-generated method stub
				return docs[index++];
			}};
	}

}

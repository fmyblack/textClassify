package com.fmyblack;

import com.fmyblack.textClassify.ClassifyModel;
import com.fmyblack.textClassify.cosine.CosineClassifier;
import com.fmyblack.textClassify.lr.LRClassifier;
import com.fmyblack.textClassify.naiveBayes.NaiveBayesClassifier;
import com.fmyblack.word.WordSegmenter;
import com.fmyblack.word.character.SingleCharacter;
import com.fmyblack.word.rmm.Rmm;
import com.fmyblack.word.mm.MM;

public class ClassifierFactory {

	public final static String NAIVEBAYES = "naivebayes";
	public final static String COSINES = "cosine";
	public final static String LR = "lr";
	
	public final static String MM = "mm";
	public final static String RMM = "rmm";
	public final static String sw = "sw";
	
	String seedsDir;
	
	public ClassifierFactory(String seedsDir) {
		this.seedsDir = seedsDir;
	}
	
	public ClassifyModel getClassifyModel(String modelName, String segmenterName) {
		WordSegmenter seg = getWordSegmenter(segmenterName);
		
		if(modelName.toLowerCase().equals(NAIVEBAYES)) {
			return new NaiveBayesClassifier(seg);
		} else if(modelName.toLowerCase().equals(COSINES)) {
			return new CosineClassifier(seg);
		} else if(modelName.toLowerCase().equals(LR)) {
			return new LRClassifier(seg);
		}
		return null;
	}
	
	private WordSegmenter getWordSegmenter(String segmenterName) {
		WordSegmenter seg = null;
		if(segmenterName.equals(MM)) {
			seg = new MM();
		} else if(segmenterName.equals(RMM)) {
			seg = new Rmm();
		} else if(segmenterName.equals(sw)) {
			seg = new SingleCharacter();
		}
		
		return seg;
	}
}

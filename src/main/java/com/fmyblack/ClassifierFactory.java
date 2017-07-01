package com.fmyblack;

import com.fmyblack.textClassify.ClassifyModel;
import com.fmyblack.textClassify.cosine.CosineClassifier;
import com.fmyblack.textClassify.lr.LRClassifier;
import com.fmyblack.textClassify.naiveBayes.NaiveBayesClassifier;
import com.fmyblack.word.rmm.Rmm;

public class ClassifierFactory {

	public final static String NAIVEBAYES = "naivebayes";
	public final static String COSINES = "cosine";
	public final static String LR = "lr";
	
	public final static String RMM = "rmm";
	
	String seedsDir;
	
	public ClassifierFactory(String seedsDir) {
		this.seedsDir = seedsDir;
	}
	
	public ClassifyModel getClassifyModel(String modelName, String segmenterName) {
		if(modelName.toLowerCase().equals(NAIVEBAYES) && segmenterName.toLowerCase().equals(RMM)) {
			return new NaiveBayesClassifier(Rmm.getIns());
		} else if(modelName.toLowerCase().equals(COSINES) && segmenterName.toLowerCase().equals(RMM)) {
			return new CosineClassifier(Rmm.getIns());
		} else if(modelName.toLowerCase().equals(LR) && segmenterName.toLowerCase().equals(RMM)) {
			return new LRClassifier(Rmm.getIns());
		}
		return null;
	}
}

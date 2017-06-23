package com.fmyblack;

import com.fmyblack.textClassify.ClassifyModel;
import com.fmyblack.textClassify.naiveBayes.NaiveBayesClassify;
import com.fmyblack.word.rmm.Rmm;

public class ClassifierFactory {

	public final static String NAIVEBAYES = "naivebayes";
	
	public final static String RMM = "rmm";
	
	String seedsDir;
	
	public ClassifierFactory(String seedsDir) {
		this.seedsDir = seedsDir;
	}
	
	public ClassifyModel getClassifyModel(String modelName, String segmenterName) {
		if(modelName.toLowerCase().equals(NAIVEBAYES) && segmenterName.toLowerCase().equals(RMM)) {
			return new NaiveBayesClassify(Rmm.getIns());
		}
		return null;
	}
}

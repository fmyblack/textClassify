package com.fmyblack.textClassify;

import java.util.List;
import java.util.Map;

import com.fmyblack.textClassify.naiveBayes.Result;

public interface ClassifyModel {
	
	public abstract void train(Map<String, List<String>> trainSeeds);
	
	public abstract Result classify(String text);
}

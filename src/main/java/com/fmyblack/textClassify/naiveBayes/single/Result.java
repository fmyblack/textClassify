package com.fmyblack.textClassify.naiveBayes.single;

public class Result {

	String tag;
	double accuracy;
	
	public Result(String tag, double accuracy) {
		this.tag = tag;
		this.accuracy = accuracy;
	}
	
	public String getTag() {
		return tag;
	}
	public double getAccuracy() {
		return accuracy;
	}
	
	@Override
	public String toString() {
		return this.tag + "\t" + Math.log(this.accuracy);
	}
}

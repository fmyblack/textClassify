package com.fmyblack.textClassify.cosines;

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
	public void setTag(String tag) {
		this.tag = tag;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	@Override
	public String toString() {
		return this.tag + "\t" + this.accuracy * 10;
	}
}

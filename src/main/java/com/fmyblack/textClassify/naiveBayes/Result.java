package com.fmyblack.textClassify.naiveBayes;

import java.text.DecimalFormat;

public class Result {

	String	tag;
	double	accuracy;

	public Result(String tag, double accuracy) {
		this.tag = tag;
		this.accuracy = accuracy;
	}

	public String getTag() {
		return tag;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getAccuracy() {
		return accuracy;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("######0.00");
		return this.tag + "\t" + df.format(this.accuracy);
	}
}

package com.fmyblack.common;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class JavaSparkContextSingleTon {

	private static JavaSparkContext ins = null;
	
	private JavaSparkContextSingleTon(){};
	
	public static synchronized JavaSparkContext getJavaSparkContext(){
		if(ins == null) {
			initJavaSparkContext("default_app");
		}
		return ins;
	}
	
	public static synchronized JavaSparkContext getJavaSparkContext(String appName) {
		if(ins == null) {
			initJavaSparkContext(appName);
		}
		return ins;
	}
	
	private static void initJavaSparkContext(String appName) {
		SparkConf conf = new SparkConf()
				.setAppName(appName)
				.setMaster("local[2]");
		ins = new JavaSparkContext(conf);
	}
}

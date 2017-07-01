package com.fmyblack;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fmyblack.textClassify.ClassifyModel;
import com.fmyblack.textClassify.Result;

import scala.Tuple2;

public class ClassifyTest {

	public static void main(String[] args) {
		String userDirPath = System.getProperty("user.dir");
		String seedsDir = userDirPath + File.separator + "seeds";
		
		double trainDataPercent = 0.7;
		Tuple2<Map<String, List<String>>, Map<String, List<String>>> trainAndtestSeeds = 
				divideSeedsToTrainAndTest(seedsDir, trainDataPercent);
		Map<String, List<String>> trainSeeds = trainAndtestSeeds._1();
		Map<String, List<String>> testSeeds = trainAndtestSeeds._2();
		
		ClassifierFactory cf = new ClassifierFactory(seedsDir);
		ClassifyModel cModel = cf.getClassifyModel("naiveBayes", "rmm");
//		ClassifyModel cModel = cf.getClassifyModel("cosine", "rmm");
		
		long start = System.currentTimeMillis();
		cModel.train(trainSeeds);
		long trainEnd = System.currentTimeMillis();
		testAccuracy(cModel, testSeeds);
		long classifyEnd = System.currentTimeMillis();
		System.out.println("训练耗时:\t" + (trainEnd - start) + "ms");
		System.out.println("分类耗时:\t" + (classifyEnd - trainEnd) + "ms");
	}
	
	public static void testAccuracy(ClassifyModel cModel, Map<String, List<String>> testSeeds) {
		int right = 0;
		int all = 0;
		for(Map.Entry<String, List<String>> entry : testSeeds.entrySet()) {
			String tag = entry.getKey();
			for(String docPath : entry.getValue()) {
				Result result = cModel.classify(docPath);
				System.out.println(tag + "\t" + result.toString());
				if(tag.equals(result.getTag())) {
					right++;
				}
				all++;
			}
		}
		System.out.println("共测试 " + all + " 篇文章");
		System.out.println("分类正确 " + right + " 篇文章");
		DecimalFormat df = new DecimalFormat("######0.00");
		System.out.println("准确率为 " + df.format(right * 1.0 / all * 100) + "%");
	}
	
	public static Tuple2<Map<String, List<String>>, Map<String, List<String>>> divideSeedsToTrainAndTest(String seedsDir, double trainDataPercent) {
		Map<String, List<String>> trainData = new HashMap<String, List<String>>();
		Map<String, List<String>> testData = new HashMap<String, List<String>>();
		
		Random rand = new Random();
		
		File seeds = new File(seedsDir);
		for(File tag : seeds.listFiles()) {
			String tagName = tag.getName();
			List<String> trainSeeds = new ArrayList<String>();
			List<String> validationSeeds = new ArrayList<String>();
			for(File seed : tag.listFiles()) {
				if(rand.nextDouble() <= trainDataPercent) {
					trainSeeds.add(seed.getAbsolutePath());
				} else {
					validationSeeds.add(seed.getAbsolutePath());
				}
			}
			trainData.put(tagName, trainSeeds);
			testData.put(tagName, validationSeeds);
		}
		
		return new Tuple2<Map<String, List<String>>, Map<String, List<String>>>(trainData, testData);
	}
}

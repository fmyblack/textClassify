package com.fmyblack;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fmyblack.textClassify.ClassifyModel;
import com.fmyblack.textClassify.naiveBayes.Result;

import scala.Tuple2;

public class ClassifyTest {

	public static void main(String[] args) {
		String dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data";
		String seedsDir = dir + File.separator + "nbc_seeds";
//		
		
		Tuple2<Map<String, List<String>>, Map<String, List<String>>> trainAndValidation = 
				divideSeedsToTrainAndValidation(seedsDir, 0.7);
		Map<String, List<String>> trainSeeds = trainAndValidation._1();
		Map<String, List<String>> validationSeeds = trainAndValidation._2();
		
		ClassifierFactory cf = new ClassifierFactory(seedsDir);
		ClassifyModel cModel = cf.getClassifyModel("naivebayes", "rmm");
		
		cModel.train(trainSeeds);
		testAccuracy(cModel, validationSeeds);
	}
	
	public static void testAccuracy(ClassifyModel cModel, Map<String, List<String>> validationSeeds) {
		int right = 0;
		int all = 0;
		for(Map.Entry<String, List<String>> entry : validationSeeds.entrySet()) {
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
	
	public static Tuple2<Map<String, List<String>>, Map<String, List<String>>> divideSeedsToTrainAndValidation(String seedsDir, double trainPercent) {
		Map<String, List<String>> trainData = new HashMap<String, List<String>>();
		Map<String, List<String>> validationData = new HashMap<String, List<String>>();
		
		Random rand = new Random();
		
		File seeds = new File(seedsDir);
		for(File tag : seeds.listFiles()) {
			String tagName = tag.getName();
			List<String> trainSeeds = new ArrayList<String>();
			List<String> validationSeeds = new ArrayList<String>();
			for(File seed : tag.listFiles()) {
				if(rand.nextDouble() <= trainPercent) {
					trainSeeds.add(seed.getAbsolutePath());
				} else {
					validationSeeds.add(seed.getAbsolutePath());
				}
			}
			trainData.put(tagName, trainSeeds);
			validationData.put(tagName, validationSeeds);
		}
		
		return new Tuple2<Map<String, List<String>>, Map<String, List<String>>>(trainData, validationData);
	}
}

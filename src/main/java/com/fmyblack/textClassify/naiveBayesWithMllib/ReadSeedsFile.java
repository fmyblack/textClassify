package com.fmyblack.textClassify.naiveBayesWithMllib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

import com.fmyblack.common.ConfigHelper;
import com.fmyblack.common.Const;

import scala.Tuple2;

public class ReadSeedsFile {

	public static String dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data/nbc_seeds";
	
	public static JavaPairRDD<String, String> readSeed(JavaSparkContext jsc) {
		String seedsDir = ConfigHelper.getConf(Const.confPrefix, "seedsDir");
		return readSeed(seedsDir, jsc);
	}
	
	public static JavaPairRDD<String, String> readSeed(String seedsPath, JavaSparkContext jsc) {
		return readSeed(listSeedDir(seedsPath), jsc);
	}
	
	/**
	 * pairRdd,key为标签，value为文章内容，未分词
	 * @param seedDirNames
	 * @param jsc
	 * @return
	 */
	public static JavaPairRDD<String, String> readSeed(List<String> seedDirNames, JavaSparkContext jsc) {
		JavaPairRDD<String, String> seedToText = null;
		for( String tagName : seedDirNames) {
			String path = dir + File.separator + tagName;
			JavaPairRDD<String, String> seedPair = jsc.wholeTextFiles(path)
					.mapToPair(new PairFunction<Tuple2<String,String>, String, String>() {
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						public scala.Tuple2<String,String> call(Tuple2<String,String> wholeText) throws Exception {
							return new Tuple2<String, String>(tagName, wholeText._2());
						};
					});
			if(seedToText == null) {
				seedToText = seedPair;
			} else {
				seedToText = seedToText.union(seedPair);
			}
		}
		return seedToText;
	}
	
	/**
	 * 获取标签列表
	 * @param seedsPath
	 * @return
	 */
	public static List<String> listSeedDir(String seedsPath) {
		File dir = new File(seedsPath);
		List<String> seedDirNames = new ArrayList<String>();
		if(dir.isDirectory()) {
			String[] fileList = dir.list();
			for(String fileName : fileList) {
				File seedFile = new File(seedsPath + File.separator + fileName);
				if(seedFile.isDirectory()) {
					seedDirNames.add(fileName);
				}
			}
		}
		return seedDirNames;
	}
	
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("First_Spark_SApp")
				.setMaster("local[2]");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		
		List<String> seedDirNames = listSeedDir(dir);
		readSeed(seedDirNames, jsc);
	}
}

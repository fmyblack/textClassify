package com.fmyblack.textClassify.naiveBayes.mllib;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;

import com.fmyblack.common.ConfigHelper;
import com.fmyblack.common.JavaSparkContextSingleTon;
import com.fmyblack.word.Rmm;

import scala.Tuple2;

public class NaiveBayesTrainModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7200844778357105342L;
	
	private NaiveBayesModel model = null;
	private JavaSparkContext jsc;
	
	public NaiveBayesTrainModel(JavaSparkContext jsc){
		this.jsc = jsc;
	};
	
	public void save(String filePath) throws Exception {
		if(model == null) {
			throw new Exception("this model did not be trained");
		}
		this.model.save(this.jsc.sc(), filePath);
	}
	
	public void train() {
		JavaPairRDD<String, String> labeledDocuments = ReadSeedsFile.readSeed(jsc);
		this.model = train(labeledDocuments);
	}
	
	public NaiveBayesModel getModel() {
		if(model == null) {
			System.out.println("model is null befor get model");
		}
		return this.model;
	}
	
	public double predict(Vector testData) {
		if(this.model == null) {
			System.out.println("model is null");
		}
		return this.model.predict(testData);
	}
	
	public static NaiveBayesModel train(JavaPairRDD<String, String> labeledDocuments) {
		final IDFModel idfModel = TFIDF.trainDocuments(labeledDocuments);
		JavaPairRDD labeledCorpus = labeledDocuments.reduceByKey(new Function2<String, String, String>() {
			
			@Override
			public String call(String document1, String document2) throws Exception {
				// TODO Auto-generated method stub
				return document1 + " " + document2;
			}
		});
		JavaPairRDD<String, Vector> tfDocs = TFIDF.tf(TFIDF.rmmForLabeledDocuments(labeledCorpus));
		JavaPairRDD<String, Vector> tfidfDocs = TFIDF.tfidf(tfDocs, idfModel);
		JavaRDD<LabeledPoint> labeledDoc = tfidfDocs.map(new Function<Tuple2<String, Vector>, LabeledPoint>() {
			public LabeledPoint call(Tuple2<String, Vector> tfidfDoc) throws Exception {
				return new LabeledPoint(stringToDouble(tfidfDoc._1()), tfidfDoc._2());
			};
		});
		return NaiveBayes.train(labeledDoc.rdd());
	}

	public static Double stringToDouble(String s) {
		int i = Integer.parseInt(s.split("-")[0]);
		return i + 0.0d;
	}
	
	public static void main(String[] args) {
		ConfigHelper.init();

		long start = System.currentTimeMillis();
	    String dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data/nbc_seeds";
		SparkConf conf = new SparkConf().setAppName("First_Spark_SApp").setMaster("local[2]");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		
//		NaiveBayesTrainModel nbtm = new NaiveBayesTrainModel(jsc);
//		nbtm.train();
//		final NaiveBayesModel nbm = nbtm.getModel();
	
		String result_dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data/result";
		final NaiveBayesModel nbm = NaiveBayesModel.load(jsc.sc(), result_dir);
		
//		try {
//			nbtm.save("/Users/fmyblack/javaproject/textClassify/src/main/resources/data/result");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		for(double d : nbm.labels()) {
			System.out.print(d + "\t");
		}
		List<String> seedDirNames = ReadSeedsFile.listSeedDir(dir);
		JavaPairRDD<String, String> labeledDocuments = ReadSeedsFile.readSeed(seedDirNames, jsc);
		final Rmm rmm = new Rmm();
		List<Tuple2<String, Double>> results = labeledDocuments.mapToPair(new PairFunction<Tuple2<String,String>, String, List<String>>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -660937136102122160L;

			@Override
			public Tuple2<String, List<String>> call(Tuple2<String, String> document) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<String, List<String>>(document._1(), rmm.rmmSegment(document._2()));
			}
		}).mapToPair(new PairFunction<Tuple2<String,List<String>>, String, Double>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7992695421520965905L;
			final HashingTF hashingTF = new HashingTF();

			@Override
			public Tuple2<String, Double> call(Tuple2<String, List<String>> doc) throws Exception {
				// TODO Auto-generated method stub
//				System.out.println(doc._1() + "\t" + doc._2());
				Vector vector = hashingTF.transform(doc._2());
//				System.out.println(vector);
				if(vector == null) {
					return null;
				}
				if(nbm == null) {
					System.out.println("nbm is null");
				}
				System.out.println(doc._1() + "\t" + nbm.predictProbabilities(vector));
				return new Tuple2<String, Double>(doc._1(), nbm.predict(vector));
			}
		}).collect();
		
		System.out.println(System.currentTimeMillis() - start);
		int all = 0;
		int right = 0;
		for(Tuple2<String, Double> result : results) {
			all++;
			if(result == null) {
				System.out.println("null");
			}
			System.out.println(result._1() + "\t" + result._2());
			String pre = "" + result._2();
			if(result._1().substring(0, 2).equals(pre.substring(0,  2))) {
				right++;
			}
		}
		System.out.println("all:\t" + all + "\nright:\t" + right);
	}
}

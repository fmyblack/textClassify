package com.fmyblack.textClassify.naiveBayes.mllib;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;

import com.fmyblack.common.JavaSparkContextSingleTon;

import scala.Tuple2;

public class NaiveBayesTrainModel {

	private NaiveBayesModel model = null;
	private JavaSparkContext jsc;
	
	public NaiveBayesTrainModel(JavaSparkContext jsc){
		this.jsc = jsc;
	};
	
	public void save(String filePath) throws Exception {
		if(model == null) {
			throw new Exception("this model did not be trained");
		}
		this.model.save(JavaSparkContextSingleTon.getJavaSparkContext().sc(), filePath);
	}
	
	public void train() {
		JavaPairRDD<String, String> labeledDocuments = ReadSeedsFile.readSeed(jsc);
		train(labeledDocuments);
	}
	
	public void train(JavaPairRDD<String, String> labeledDocuments) {
		final IDFModel idfModel = TFIDF.trainDocuments(labeledDocuments);
		JavaPairRDD<String, Vector> tfDocs = TFIDF.tf(TFIDF.rmmForLabeledDocuments(labeledDocuments));
		JavaPairRDD<String, Vector> tfidfDocs = TFIDF.tfidf(tfDocs, idfModel);
		JavaRDD<LabeledPoint> labeledDoc = tfidfDocs.map(new Function<Tuple2<String, Vector>, LabeledPoint>() {
			public LabeledPoint call(Tuple2<String, Vector> tfidfDoc) throws Exception {
				return new LabeledPoint(stringToDouble(tfidfDoc._1()), tfidfDoc._2());
			};
		});
		this.model = NaiveBayes.train(labeledDoc.rdd());
	}

	public static Double stringToDouble(String s) {
		int i = Integer.parseInt(s.split("-")[0]);
		return i + 0.0d;
	}
	
	public static void main(String[] args) {
//	    String dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data/nbc_seeds";
//		SparkConf conf = new SparkConf().setAppName("First_Spark_SApp").setMaster("local[2]");
//		JavaSparkContext jsc = new JavaSparkContext(conf);
//
//		List<String> seedDirNames = ReadSeedsFile.listSeedDir(dir);
//		JavaPairRDD<String, String> labeledDocuments = ReadSeedsFile.readSeed(seedDirNames, jsc);
//		
//		train(labeledDocuments);
//		JavaRDD<LabeledPoint>[] tmp = labeledDoc.randomSplit(new double[] { 1.0, 0.4 });
//		JavaRDD<LabeledPoint> training = tmp[0]; // training set
//		JavaRDD<LabeledPoint> test = tmp[1]; // test set
//
//		final NaiveBayesModel model = NaiveBayes.train(training.rdd(), 2.0);
//		JavaPairRDD<Double, Double> predictionAndLabel = test
//				.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
//					@Override
//					public Tuple2<Double, Double> call(LabeledPoint p) {
//						return new Tuple2<>(model.predict(p.features()), p.label());
//					}
//				});
//		double accuracy = predictionAndLabel.filter(new Function<Tuple2<Double, Double>, Boolean>() {
//			@Override
//			public Boolean call(Tuple2<Double, Double> pl) {
//				return pl._1().equals(pl._2());
//			}
//		}).count() / (double) test.count();
//		for(Tuple2<Double, Double> t : predictionAndLabel.collect()) {
//			if(!t._1().equals(t._2())) {
//				System.out.print("------");
//			}
//			System.out.println(t.toString());
//		}
//		System.out.println(accuracy);
	}
}

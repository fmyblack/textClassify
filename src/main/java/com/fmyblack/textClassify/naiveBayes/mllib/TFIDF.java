package com.fmyblack.textClassify.naiveBayes.mllib;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;

import com.fmyblack.word.Rmm;

import scala.Tuple2;

public class TFIDF {

	public static String dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data/nbc_seeds";

	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("First_Spark_SApp").setMaster("local[2]");
		JavaSparkContext jsc = new JavaSparkContext(conf);

		List<String> seedDirNames = ReadSeedsFile.listSeedDir(dir);
		JavaPairRDD<String, String> labeledDocuments = ReadSeedsFile.readSeed(seedDirNames, jsc);
		JavaRDD<String> documents = labeledDocuments.map(new Function<Tuple2<String, String>, String>() {
			public String call(Tuple2<String, String> labeledDocument) throws Exception {
				return labeledDocument._2();
			};
		});

		List<String> list = Arrays.asList(new String[] { "a b", "a c", "a c c" });
		JavaRDD<List<String>> test = jsc.parallelize(list).map(new Function<String, List<String>>() {
			@Override
			public List<String> call(String arg0) throws Exception {
				// TODO Auto-generated method stub
				return Arrays.asList(arg0.split(" "));
			}
		});

		JavaRDD<Vector> vectors = tf(test);
		for (Vector ori : vectors.take(10)) {
			System.out.println(ori);
		}
		IDFModel idfModel = idf(vectors);
		for (Vector sample : idfModel.transform(vectors).take(10)) {
			System.out.println(sample);
		}
	}

	public static JavaPairRDD<String, Vector> tfidf(JavaPairRDD<String, Vector> tfDocuments, final IDFModel idfModel) {
		return tfDocuments.mapToPair(new PairFunction<Tuple2<String,Vector>, String, Vector>() {
			@Override
			public Tuple2<String, Vector> call(Tuple2<String, Vector> tfDoc) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<String, Vector>(tfDoc._1(), idfModel.transform(tfDoc._2()));
			}
		});
	}
	
	public static IDFModel trainDocuments(JavaPairRDD<String, String> labeledDocuments) {
		JavaRDD<String> documents = labeledDocuments.map(new Function<Tuple2<String, String>, String>() {
			public String call(Tuple2<String, String> labeledDocument) throws Exception {
				return labeledDocument._2();
			};
		});
		return idf(tf(rmmForDocuments(documents)));
	}

	public static IDFModel trainDocuments(JavaRDD<String> documents) {
		return idf(tf(rmmForDocuments(documents)));
	}

	public static IDFModel idf(JavaRDD<Vector> vectors) {
		return new IDF().fit(vectors);
	}

	public static JavaPairRDD<String, Vector> tf(JavaPairRDD<String, List<String>> labeledDocumentsSegmentation) {
		final HashingTF hashingTF = new HashingTF();
		return labeledDocumentsSegmentation.mapToPair(new PairFunction<Tuple2<String, List<String>>, String, Vector>() {

			/**
			* 
			*/
			private static final long serialVersionUID = -6453873127054451962L;

			@Override
			public Tuple2<String, Vector> call(Tuple2<String, List<String>> labeledDocSegmentation) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<String, Vector>(labeledDocSegmentation._1(),
						hashingTF.transform(labeledDocSegmentation._2()));
			}
		});
	}

	public static JavaRDD<Vector> tf(JavaRDD<List<String>> documentsSegmentation) {
		HashingTF hashingTF = new HashingTF();
		return hashingTF.transform(documentsSegmentation);
	}

	public static JavaPairRDD<String, List<String>> rmmForLabeledDocuments(
			JavaPairRDD<String, String> labeledDocuments) {
		final Rmm rmm = new Rmm();
		return labeledDocuments.mapToPair(new PairFunction<Tuple2<String, String>, String, List<String>>() {
			@Override
			public Tuple2<String, List<String>> call(Tuple2<String, String> labeledDocument) throws Exception {
				// TODO Auto-generated method stub
				return new Tuple2<String, List<String>>(labeledDocument._1(), rmm.rmmSegment(labeledDocument._2()));
			}
		});
	}

	public static JavaRDD<List<String>> rmmForDocuments(JavaRDD<String> documents) {
		final Rmm rmm = new Rmm();
		return documents.map(new Function<String, List<String>>() {
			@Override
			public List<String> call(String doc) throws Exception {
				// TODO Auto-generated method stub
				return rmm.rmmSegment(doc);
			}
		});
	}
}

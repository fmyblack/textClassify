package com.fmyblack.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;

import scala.Tuple2;

public class TFIDFTest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8971862622010849560L;
	
	transient JavaSparkContext jsc;
	
	public TFIDFTest(JavaSparkContext jsc) {
		this.jsc = jsc;
	}
	
	public void process() {
		JavaRDD<String> oriD = getOri();
		JavaRDD<List<String>> tfpredata = handleTFIDFData(oriD);
		HashingTF hashingTF = new HashingTF((int) Math.pow(2, 22));
		System.out.println(hashingTF.numFeatures());
		IDFModel idf = idf(tfpredata, hashingTF);
		JavaPairRDD<String, Map<String, Double>> tag2word = handleData(oriD);
		JavaPairRDD<String, Map<Double, String>> result = caculateTop(tag2word, idf.idf(), hashingTF);
		System.out.println(result.count());
		for(Tuple2<String, Map<Double, String>> s : result.collect()) {
			System.out.println(s._1() + "\t" + s._2().values());
		}
	}
	
	public JavaPairRDD<String, Map<Double, String>> caculateTop(JavaPairRDD<String, Map<String, Double>> tag2word, final Vector idf, final HashingTF hashingTF) {
		return tag2word.reduceByKey(new Function2<Map<String, Double>, Map<String, Double>, Map<String, Double>>() {
			
			@Override
			public Map<String, Double> call(Map<String, Double> v1, Map<String, Double> v2)
					throws Exception {
				// TODO Auto-generated method stub
				for(Map.Entry<String, Double> entry : v2.entrySet()) {
					String word = entry.getKey();
					Double freq = entry.getValue();
					if(v1.containsKey(word)) {
						v1.put(word, freq + v1.get(word));
					} else {
						v1.put(word, freq);
					}
				}
				return v1;
			}
		}).mapToPair(new PairFunction<Tuple2<String,Map<String,Double>>, String, Map<Double, String>>() {
			@Override
			public Tuple2<String, Map<Double, String>> call(
					Tuple2<String, Map<String, Double>> t) throws Exception {
				// TODO Auto-generated method stub
				SortedMap<Double, String> sort = new TreeMap<Double, String>();
				double min = 0;
				for(Map.Entry<String, Double> wordFreq : t._2().entrySet()) {
					String word = wordFreq.getKey();
					Double freq = wordFreq.getValue();
					double idfv = idf.apply(hashingTF.indexOf(word));
					double tfidf = Math.pow(idfv, 3) * freq;
					if(sort.size() == 20) {
						if(tfidf > min) {
							sort.remove(min);
							sort.put(tfidf, word);
							min = getMin(sort);
						}
					} else {
						sort.put(tfidf, word);
						min = getMin(sort);
					}
				}
				return new Tuple2(t._1(), sort);
			}
		});
	}
	
	public IDFModel idf(JavaRDD<List<String>> pre, final HashingTF hashingTF) {
		JavaRDD<Vector> vts = hashingTF.transform(pre);
		return new IDF().fit(vts);
	}
	
	public JavaRDD<List<String>> handleTFIDFData(JavaRDD<String> oriD) {
		return oriD.mapToPair(new PairFunction<String, String, String>() {
					@Override
					public Tuple2<String, String> call(String t)
							throws Exception {
						// TODO Auto-generated method stub
						String[] cols = t.split("\t");
						if(cols.length < 22) {
							return null;
						}
						if(cols[21].trim().equals("")) {
							return null;
						}
//						if(!cols[21].contains("-caiyun-")) {
//							return null;
//						}
						if(cols[20].trim().equals("")) {
							return null;
						}
						return new Tuple2(cols[8], cols[20]);
					}
				}).filter(new Function<Tuple2<String,String>, Boolean>() {
					
					@Override
					public Boolean call(Tuple2<String, String> v1) throws Exception {
						// TODO Auto-generated method stub
						if(v1 == null) return false;
						return true;
					}
				}).reduceByKey(new Function2<String, String, String>() {
					
					@Override
					public String call(String v1, String v2) throws Exception {
						// TODO Auto-generated method stub
						return v1;
					}
				}).map(new Function<Tuple2<String,String>, List<String>>() {
					@Override
					public List<String> call(Tuple2<String, String> v1)
							throws Exception {
						// TODO Auto-generated method stub
						List<String> set = new ArrayList<String>();
						for(String s : v1._2().split(",")) {
							String[] cols = s.split(":");
							set.add(cols[0]);
						}
						return set;
					}
				});
	}
	
	public JavaPairRDD<String, Map<String, Double>> handleData(JavaRDD<String> oriD) {
		return oriD.mapToPair(new PairFunction<String, String, String>() {
					@Override
					public Tuple2<String, String> call(String t)
							throws Exception {
						// TODO Auto-generated method stub
						String[] cols = t.split("\t");
						if(cols.length < 22) {
							return null;
						}
						if(cols[21].trim().equals("")) {
							return null;
						}
//						if(!cols[21].contains("-caiyun-")) {
//							return null;
//						}
						if(cols[20].trim().equals("")) {
							return null;
						}
						return new Tuple2(cols[21], cols[20]);
					}
				}).filter(new Function<Tuple2<String,String>, Boolean>() {
					
					@Override
					public Boolean call(Tuple2<String, String> v1) throws Exception {
						// TODO Auto-generated method stub
						if(v1 == null) return false;
						return true;
					}
				}).mapToPair(new PairFunction<Tuple2<String,String>, String, Map<String, Integer>>() {
					@Override
					public Tuple2<String, Map<String, Integer>> call(
							Tuple2<String, String> t) throws Exception {
						// TODO Auto-generated method stub
						Map<String, Integer> map = new HashMap<String, Integer>();
						for(String s : t._2().split(",")) {
							String[] cols = s.split(":");
							if(map.containsKey(cols[0])) {
								map.put(cols[0], map.get(cols[0]) + Integer.parseInt(cols[1]));
							} else {
								map.put(cols[0], Integer.parseInt(cols[1]));
							}
						}
						return new Tuple2(t._1(), map);
					}
				}).flatMapToPair(new PairFlatMapFunction<Tuple2<String,Map<String, Integer>>, String, Map<String, Double>>() {
					@Override
					public Iterable<Tuple2<String, Map<String, Double>>> call(
							Tuple2<String, Map<String, Integer>> t) throws Exception {
						// TODO Auto-generated method stub
						List<Tuple2<String, Map<String, Double>>> l = new ArrayList();
						for(String s : t._1().split("\\|")) {
							String tagid = s.split("-")[0];
							double d = Double.parseDouble(s.split("-")[1]);
							Map<String, Double> calMap = new HashMap<String, Double>();
							for(Map.Entry<String, Integer> entry : t._2().entrySet()) {
								calMap.put(entry.getKey(), entry.getValue() * Math.pow(d, 10));
							}
							l.add(new Tuple2(tagid, calMap));
						}
						return l;
					}
				});
	}
	
	public JavaRDD<String> getOri() {
		String tmp = "/Users/fmyblack/data/batch_user_tags/20170503_10/merge";
		return this.jsc.textFile(tmp);
	}
	
	private static double getMin(SortedMap<Double, String> sort) {
		for(Double min : sort.keySet()) {
			return min;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("First_Spark_SApp").setMaster("local[4]");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		TFIDFTest h = new TFIDFTest(jsc);
		h.process();
	}
}

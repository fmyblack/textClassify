package com.fmyblack.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fmyblack.textClassify.naiveBayesWithMllib.TFIDF;
import com.fmyblack.word.rmm.Rmm;

import scala.Tuple2;

public class WordsSet implements Serializable {

	Rmm					rmm	= null;
	String				dir	= null;
	Map<String, Double> idf = new HashMap<String, Double>();
	Set<String> words = new HashSet<String>();
	int allDocs = 0;

	public WordsSet(String dir) {
		this.rmm = Rmm.getIns();
		this.dir = dir;
	}

	public void train() {
		List<String> male = Arrays.asList("news_car", "news_discovery", "news_game",
				"news_military", "news_sports", "news_society");
		List<String> female = Arrays.asList("news_baby", "news_entertainment", "news_fashion",
				"news_food");
		
		List<Set<String>> l = loadidf();
		for(String word : words) {
			int docs = docs(l, word);
			double value = Math.log((allDocs * 1.0 + 1) / (docs * 1.0 + 1 ) );
			idf.put(word, value);
		}
		
		StringBuilder sb = new StringBuilder();
		try(FileWriter fw = new FileWriter("/Users/fmyblack/data/words");) {
			
			JSONArray m = calc(male);
			sb.append(m.toJSONString() + "\n");
			JSONArray m2 = calc(female);
			sb.append(m2.toJSONString() + "\n");
			fw.write(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JSONArray calc(List<String> sex) {
		Map<String, Double> map = new HashMap<String, Double>();
		for(String sexTag : sex) {
			String sexTagPath = dir + File.separator + sexTag;
			File sexTagF = new File(sexTagPath);
			for(String doc : sexTagF.list()) {
				String text = readFile(sexTagPath + File.separator + doc);
				List<String> worddd = rmm.segment(text);
				for(String word : worddd) {
					map.put(word, map.containsKey(word) ? map.get(word) + 1 : 1);
				}
			}
		}
		
		JSONArray ja = new JSONArray();
		PriorityQueue<Tuple2<String, Double>> pq = new PriorityQueue<Tuple2<String, Double>>(2000, new Comparator<Tuple2<String, Double>>() {
			@Override
			public int compare(Tuple2<String, Double> o1,
					Tuple2<String, Double> o2) {
				// TODO Auto-generated method stub
				double d = o1._2() - o2._2();
				if(d > 0) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		for(Map.Entry<String, Double> entry : map.entrySet()) {
			String word = entry.getKey();
			double idfv = idf.get(word);
			double tf = entry.getValue() > 100 ? 100 : entry.getValue();
			pq.add(new Tuple2<String, Double>(word, 1.0d / (1 + Math.pow(Math.E, -1.0 * Math.pow(idfv, 10) / 1000000000 * tf))));
		}
		System.out.println(pq.size());
		
		for(int i = 0; i < 2000; i++) {
			Tuple2<String, Double> t = pq.poll();
//			System.out.println(t._1() + "\t" + t._2());
			JSONObject jo = new JSONObject();
			jo.put(t._1(), (int) (t._2() * 100) - 30);
			ja.add(jo);
		}
		return ja;
	}
	
	public int docs(List<Set<String>> l, String word) {
		int nums = 0;
		for(Set<String> s : l) {
			if(s.contains(word)) {
				nums++;
			}
		}
		return nums;
	}
	
	public List<Set<String>> loadidf() {
		List<Set<String>> l = new ArrayList<Set<String>>();
		File dirF = new File(dir);
		for(String tag : dirF.list()) {
			Set<String> s = new HashSet<String>();
			String tagPath = dir + File.separator + tag;
			File tagF = new File(tagPath);
			for(String f : tagF.list()) {
				allDocs++;
				String text = readFile(tagPath + File.separator + f);
				List<String> list = rmm.segment(text);
				s.addAll(list);
				words.addAll(list);
			}
			l.add(s);
		}
		return l;
	}
	
	public String readFile(String path) {
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(path));
			String readString = null;
			while(( readString = reader.readLine()) != null) {
				sb.append(readString.trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String dir = "/Users/fmyblack/data/nbc_seeds";
		WordsSet ws = new WordsSet(dir);
		ws.train();
	}
}
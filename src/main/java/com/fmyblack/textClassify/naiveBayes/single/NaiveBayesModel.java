package com.fmyblack.textClassify.naiveBayes.single;

import java.io.File;
import java.util.*;

import com.fmyblack.common.FileReaderUtil;
import com.fmyblack.word.Rmm;

public class NaiveBayesModel {

	List<Tag> tags;
	Rmm rmm;
	
	private NaiveBayesModel(){
		rmm = new Rmm();
	};
	
	public static NaiveBayesModel train(String seedsDir) {
		NaiveBayesModel nbm = new NaiveBayesModel();
		
		Seeds seeds = new Seeds(seedsDir);
		nbm.tags = seeds.getTags();
		int documentsNum = seeds.getDocumentsNum();
		Set<String> words = seeds.getWords();
		TFIDF tfidf = new TFIDF(documentsNum, words);
		tfidf.fit(nbm.tags);
		
//		for(Tag tag : nbm.tags) {
//			System.out.println(tag.toString());
//		}
		
		return nbm;
	}
	
	public Result classify(String text) {
		List<String> words = rmm.rmmSegment(text);
		Document doc = new Document(words);
		Map<String, Integer> wordsFre = doc.getWordsFrequency();
		Result currentResult = null;
		double maxAccuracy = 1.0;
		for(Tag tag : this.tags) {
			Result tmpResult = tag.caculateAccuracy(wordsFre);
//			System.out.println(tmpResult.toString());
			if(tmpResult.getAccuracy() < maxAccuracy) {
				currentResult = tmpResult;
				maxAccuracy = currentResult.getAccuracy();
			}
		}
		return currentResult;
	}
	
	public static void main(String[] args) {
		String dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data/nbc_seeds";
		NaiveBayesModel nbm = NaiveBayesModel.train(dir);
		test(nbm, dir);
	}
	
	public static void test(NaiveBayesModel nbm, String dir) {
		File seeds = new File(dir);
		int all = 0;
		int right = 0;
		for(String seed : seeds.list()) {
			String newDir = dir + File.separator + seed;
			File s = new File(newDir);
			for(String textName : s.list()) {
				String text = FileReaderUtil.readFile(newDir + File.separator + textName);
				Result r = nbm.classify(text);
				System.out.println(seed + "\t" + r.toString());
				all++;
				if( seed.equals(r.getTag())) {
					right++;
				}
			}
		}
		System.out.println("all:\t" + all);
		System.out.println("right:\t" + right);
	}
}

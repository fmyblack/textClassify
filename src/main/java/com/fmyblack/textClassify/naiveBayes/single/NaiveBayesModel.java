package com.fmyblack.textClassify.naiveBayes.single;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.fmyblack.common.FileReaderUtil;
import com.fmyblack.textClassify.tfidf.Document;
import com.fmyblack.textClassify.tfidf.TagBase;
import com.fmyblack.word.rmm.Rmm;

public class NaiveBayesModel{

	List<TagBase> tags;
	TFIDF tfidf;
	Rmm rmm;
	static int idfPower = 2;
	static double lambda = 0.0000001;
	
	private NaiveBayesModel(){
		rmm = Rmm.getIns();
	};
	
	public static NaiveBayesModel train(String seedsDir) {
		NaiveBayesModel nbm = new NaiveBayesModel();
		
		Seeds seeds = new Seeds(seedsDir);
		nbm.tags = seeds.getTags();
		int documentsNum = seeds.getDocumentsNum();
		Set<String> words = seeds.getWords();
		nbm.tfidf = new TFIDF(documentsNum, words);
		nbm.tfidf.fit(nbm.tags);
		
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
		double maxAccuracy = 0.0;
		Set<String> trainWords = this.tfidf.getWords();
		for(TagBase tagBase : this.tags) {
			Tag tag = (Tag) tagBase;
			Result tmpResult = tag.caculateAccuracy(wordsFre, trainWords);
//			System.out.println(tmpResult.toString());
			if(tmpResult.getAccuracy() > maxAccuracy) {
				currentResult = tmpResult;
				maxAccuracy = currentResult.getAccuracy();
			}
		}
		currentResult.setAccuracy(maxAccuracy / words.size() * 100);
		return currentResult;
	}
	
	public void save(String fileName) {
		File file = new File(fileName);
		if(file.exists()) {
			file.delete();
		}
		try {
			FileWriter fw = new FileWriter(fileName, true);
			this.tfidf.save(fw);
			for(TagBase tagBase : tags) {
				Tag tag = (Tag) tagBase;
				tag.save(fw);
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}
	
	public static NaiveBayesModel load(String fileName) {
		NaiveBayesModel nbm = new NaiveBayesModel();
		nbm.tags = new ArrayList<TagBase>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String readString = null;
			if(( readString = reader.readLine()) != null) {
				TFIDF tfidf = (TFIDF) TFIDF.load(readString);
				nbm.tfidf = tfidf;
			}
			while(( readString = reader.readLine()) != null) {
				Tag tag = Tag.load(readString);
				nbm.tags.add(tag);
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
		return nbm;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		String dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data";
		String seedsDir = dir + File.separator + "nbc_seeds";
		String testDir = dir + File.separator + "test_data";
		String resultOut = dir + File.separator + "result.out";
		NaiveBayesModel nbm = NaiveBayesModel.train(seedsDir);
		nbm.save(resultOut);
//		
//		NaiveBayesModel nbm = NaiveBayesModel.load(resultOut);
//		
//        test(nbm, seedsDir);
        testNew(nbm, testDir);
//		testNewFile(nbm);
	}
	
	public static void testNewFile(NaiveBayesModel nbm) {
		String text = ""
//				+ "贫困地区发展特色产业促进精准脱贫指导意见"
//				+ "国家统计局于22日发布了31个省份2016年的地区生产总值(GDP)数据。"
//				+ "从经济总量上来看，广东、江苏不出意外地牢牢占据着排名第一、第二的位置，不过广东的第一经济大省位子更稳了;在名义增速方面，则呈现了南快北慢的趋势，西南地区的西藏、贵州和重庆均超过了11%，位列前三。"
//				+ "值得一提的是，在“挤水分”后，辽宁在全国的GDP排名，从之前的第10名下滑到14名，排在了中部省份安徽的后面。"
//				+ "1月17日，辽宁省委副书记、省长陈求发在辽宁省十二届人大八次会议上作政府工作报告时首次确认，辽宁省所辖市、县，在2011年至2014年存在财政数据造假的问题，指出在2011年至2014年“官出数字、数字出官”，导致经济数据被注入水分。"
//				+ "辽宁省财政厅数据显示，2015年全省一般公共预算收入2125.6亿元，下降33.4%，其中税收收入1650.2亿元，下降29.2%;非税收入475.4亿元，下降44.9%。辽宁省财政厅当时对此的解释是，造成全省财政收入大幅下降的主要原因中，排在首位的是按“三严三实”依法依规组织财政收入，做实了财政收入的影响。";
				+ "简章规定，从今年开始，国际学生不需要考试，只要通过HSK汉语水平测试，就可以申请清华大学本科。而以往所有申请清华本科的国际学生，都需要先参加4至5个科目的考试，然后再加试或面试后录取。"
				+ "网友为此惊呼：“十年寒窗不如一纸国籍，清华新政让中国考生欲哭无泪！”人们担心，清华为外籍学生设置的入学门槛太低，对国内考生不公平。更担心的是，清华新政一出，有条件的家长，完全可以让子女放弃中国国籍，以“移民”的方法曲线上清华。"
				+ "据报道，很多东南亚或非洲小国移民中介机构，已经打出了“免试上清华”的宣传口号，提供整体解决方案。"
				+ "写写"
				;
				Result r = nbm.classify(text);
		System.out.println(r.toString());
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
	
	public static void testNew(NaiveBayesModel nbm, String dir) {
		int all = 0;
		int right = 0;
		int none = 0;
		for(String seed : new File(dir).list()) {
			String newDir = dir + File.separator + seed;
			for(String textName : new File(newDir).list()) {
				String text = readFile(newDir + File.separator + textName);
				Result r = nbm.classify(text);
				all++;
				if(r == null) {
					none++;
					continue;
				} else {
					System.out.println(seed + "\t" + r.toString());
				}
				if( seed.equals(r.getTag())) {
					right++;
				}
			}
		}
		System.out.println("all:\t" + all);
		System.out.println("right:\t" + right);
		System.out.println("none:\t" + none);
	}
	
	public static String readFile(String filePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			StringBuilder text = new StringBuilder();
			String line = null;
			while((line = br.readLine()) != null) {
				text.append(line);
			}
			return text.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

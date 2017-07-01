package com.fmyblack.textClassify.lr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fmyblack.textClassify.ClassifyModel;
import com.fmyblack.textClassify.Result;
import com.fmyblack.textClassify.doc.Document;
import com.fmyblack.word.WordSegmenter;

public class LRClassifier implements ClassifyModel {

	WordSegmenter	ws;
	int				tagSize = 0;
	int capacity = 20 * 1000;
	List<TrainData> trainDatas = new ArrayList<TrainData>();
	Map<Integer, String> indexToTag = new HashMap<Integer, String>();
	double[][] theta;
	double lambda = 0.01;
	double alpha = 50000;

	public LRClassifier(WordSegmenter wordSegmenter) {
		ws = wordSegmenter;
	};

	@Override
	public void train(Map<String, List<String>> trainSeeds) {
		// TODO Auto-generated method stub
		System.out.println("start training ...");

		System.out.println("start loading document to vector...");
		int tagIndex = 0;
		for (Map.Entry<String, List<String>> entry : trainSeeds.entrySet()) {
			List<String> tagDocs = entry.getValue();
			for(String docPath : tagDocs) {
				Document doc = new Document(docPath);
				Map<String, Integer> wordsFreq = doc.docToWords(ws);
				Vector vec = new Vector(capacity);
				vec.add(wordsFreq);
				TrainData trainData = new TrainData(vec, tagIndex);
				trainDatas.add(trainData);
			}
			tagSize++;
			indexToTag.put(tagIndex, entry.getKey());
			tagIndex++;
		}
		
		gradDownCost();
	}
	
	public void gradDownCost() {
		theta = randTheta();
		for(int trainNum = 0; trainNum < 50; trainNum++) {
			double[][] thetaDerivativeTemp = new double[tagSize][capacity];
			double J = 0.0;
			for(TrainData td : trainDatas) {
				J += td.cost(theta, tagSize);
				td.grad(theta, tagSize, thetaDerivativeTemp);
			}
			J += lambda * sumOfSquareInMatrix(theta, tagSize, capacity) / 2;
			J = J / trainDatas.size();
			System.out.println("cost in time " + trainNum + ":\t" + J);
			for(int i = 0; i < tagSize; i++) {
				for(int j = 0; j < capacity; j++) {
					double thetaDerivative = thetaDerivativeTemp[i][j] / trainDatas.size() + lambda * theta[i][j] / trainDatas.size();
					theta[i][j] -= alpha * thetaDerivative;
				}
			}
		}
	}
	
	public double sumOfSquareInMatrix(double[][] theta, int x, int y) {
		double sum = 0.0;
		for(int i = 0; i < x; i++) {
			for(int j = 0; j < y; j++) {
				sum += Math.pow(theta[i][j], 2);
			}
		}
		return sum;
	}
	
	public double[][] randTheta(){
		double[][] theta = new double[tagSize][capacity];
		Random rand = new Random();
		for(int i = 0; i < tagSize; i++) {
			for(int j = 0; j < capacity; j++) {
				theta[i][j] = rand.nextDouble();
			}
		}
		
		return theta;
	}

	@Override
	public Result classify(String path) {
		// TODO Auto-generated method stub
		Document doc = new Document(path);
		Map<String, Integer> wordsFreq = doc.docToWords(ws);
		Vector vec = new Vector(capacity);
		vec.add(wordsFreq);
		double max = -1 * Double.MAX_VALUE;
		String currentTag = null;
		for(int i = 0; i < tagSize; i++) {
			double accuracy = 0.0;
			for(int j = 0; j < capacity; j++) {
				accuracy += theta[i][j] * vec.get(j);
			}
			if(accuracy > max) {
				max = accuracy;
				currentTag = indexToTag.get(i);
			}
		}
		return new Result(currentTag, max);
	}

}

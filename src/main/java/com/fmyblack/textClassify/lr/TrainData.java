package com.fmyblack.textClassify.lr;

import java.math.BigDecimal;

public class TrainData {

	Vector vector = null;
	int resultIndex;
	
	public TrainData(Vector vector, int resultIndex) {
		this.vector = vector;
		this.resultIndex = resultIndex;
	}
	
	public double cost(double[][] theta, int tagSize){
		double cost = 0.0;
		for(int i = 0; i < tagSize; i++) {
			double tagCost = 0.0;
			for(int j = 0; j < vector.size; j++) {
				tagCost += theta[i][j] * vector.get(j);
			}
			if(i == resultIndex) {
				cost += -1 * Math.log(sigmoid(tagCost));
			} else {
				cost += -1 * Math.log((1 - sigmoid(tagCost)));
			}
		}
		return cost;
	}
	
	public void grad(double[][] theta, int tagSize, double[][] thetaDerivative) {
		double[] hypo = hypothesis(theta, tagSize);
		for(int i = 0; i < tagSize; i++) {
			double hypoMinusResult = 0.0;
			if( i == resultIndex) {
				hypoMinusResult = hypo[i] - 1;
			} else {
				hypoMinusResult = hypo[i] - 0;
			}
			for(int j = 0; j < vector.size; j++) {
				thetaDerivative[i][j] += hypoMinusResult * vector.get(j);
			}
		}
	}
	
	public double[] hypothesis(double[][] theta, int tagSize) {
		double[] h = new double[tagSize];
		for(int i = 0; i < tagSize; i++) {
			for(int j = 0; j < vector.size; j++) {
				h[i] += theta[i][j] * vector.get(j);
			}
			h[i] = sigmoid(h[i]);
		}
		return h;
	}
	
	private double sigmoid(double x) {
		return 1.0 / (1 + Math.pow(Math.E, -1 * x));
	}
}

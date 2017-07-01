package com.fmyblack.textClassify.lr;

import java.util.Map;

public class Vector {

	int size;
	double[] x;
	
	public Vector(int size) {
		this.size = size;
		x = new double[size];
	}
	
	public void add(Map<? extends Object, Integer> map) {
		int allWordsNum = 0;
		for(Map.Entry<? extends Object, Integer> entry : map.entrySet()) {
			allWordsNum += entry.getValue();
		}
		for(Map.Entry<? extends Object, Integer> entry : map.entrySet()) {
			Object key = entry.getKey();
			double value = 1.0 * entry.getValue() / allWordsNum;
			int index = hash(key) % size;
			if(index < 0) {
				index += size;
			}
			x[index] += value;
		}
	}
	
	public double get(int index) {
		return x[index];
	}
	
	public int size() {
		return size;
	}
	
	private int hash(Object o) {
		return o.hashCode();
	}
}

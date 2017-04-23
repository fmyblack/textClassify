package com.fmyblack.word.rmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticFreq {

	Map<String, Long> map = new HashMap<String, Long>();
	Rmm rmm;
	
	public StatisticFreq() {
		rmm = Rmm.getIns();
	}

	public void save(String f) throws IOException {
		FileWriter fw = new FileWriter(f);
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Long> entry : map.entrySet()) {
			sb.append(entry.getKey() + "\t" + entry.getValue() + "\n");
		}
		fw.write(sb.toString());
		fw.close();
	}
	
	public void treeDir(File dir) throws IOException {
		if(dir.isDirectory()) {
			File[] fs = dir.listFiles();
			for(File f : fs) {
				treeDir(f);
			}
		} else {
			readFile(dir);
		}
	}
	
	public void readFile(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = null;
		while((line = br.readLine()) != null) {
			line = line.trim();
			if(line == "") {
				continue;
			}
			List<String> l = rmm.rmmSegment(line);
			addFreq(l);
		}
	}
	
	public void addFreq(List<String> list) {
		for(String s : list) {
			Long freq = map.containsKey(s) ? map.get(s) + 1 : 1;
			map.put(s, freq);
		}
	}
	
	public static void main(String[] args) throws IOException {
		StatisticFreq s = new StatisticFreq();
		String f = "";
		File dir = new File(f);
		s.treeDir(dir);
		String save = "/Users/fmyblack/data/ik/nls_freq.data";
		s.save(save);
	}
}

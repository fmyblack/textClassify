package com.fmyblack.word.rmm;

import java.util.ArrayList;
import java.util.List;

public class Rmm {

	WordDictionary wd = null;
	int maxLength;
	
	static Rmm ins = null;
	
	public static void main(String[] args) {
		String s = "我们是北京,大学的学生 ";
		Rmm rmm = Rmm.getIns();
		System.out.println(rmm.rmmSegment(s));
	}
	
	public List<String> rmmSegment(String s){
		List<String> words = new ArrayList<String>();
		
		String text = s.trim();
		int leaveTextLength = text.length();
		int rmmMaxLength = this.maxLength < leaveTextLength ? this.maxLength : leaveTextLength;
		
		while(true) {
			if(leaveTextLength <= 1) {
				break;
			}
			for(int i = rmmMaxLength; i > 0; i--) {
				if(i > leaveTextLength) {
					
				} else if(i == 1) {
					text = text.substring(0, leaveTextLength - 1);
					leaveTextLength--;
				} else {
					String segment = text.substring(leaveTextLength - i);
					if(this.wd.containsWord(segment)) {
						words.add(segment);
						text = text.substring(0, leaveTextLength - i);
						leaveTextLength -= i;
						break;
					}
				}
			}
		}
		
		return words;
	}
	
	public static synchronized Rmm getIns() {
		if(ins == null) {
			ins = new Rmm();
		}
		return ins;
	}
	
	private Rmm() {
		wd = WordDictionary.getIns();
		maxLength = wd.getMaxLength();
	}
}

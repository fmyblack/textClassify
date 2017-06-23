package com.fmyblack.word.rmm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fmyblack.word.WordSegmenter;

public class Rmm implements WordSegmenter,Serializable {

	WordDictionary wd = null;
	int maxLength;
	
	static Rmm ins = null;
	
	public static void main(String[] args) {
		String s = "他们在高维正质量猜想上有了突破";
		Rmm rmm = Rmm.getIns();
		System.out.println(rmm.segment(s));
	}
	
	public List<String> segment(String s){
		List<String> words = new ArrayList<String>();
		
		String text = s.trim();
		int leaveTextLength = text.length();
		int rmmMaxLength = this.maxLength < leaveTextLength ? this.maxLength : leaveTextLength;
		
		if(rmmMaxLength == 0) {
			return null;
		}
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

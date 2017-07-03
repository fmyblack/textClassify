package com.fmyblack.word.mm;

import java.util.ArrayList;
import java.util.List;

import com.fmyblack.word.WordDictionary;
import com.fmyblack.word.WordSegmenter;

public class MM implements WordSegmenter {

	WordDictionary	wd	= null;
	int				maxLength;

	public MM() {
		wd = WordDictionary.getIns();
		maxLength = wd.getMaxLength();
	}

	@Override
	public List<String> segment(String s) {
		// TODO Auto-generated method stub
		List<String> words = new ArrayList<String>();

		String text = s.trim();
		int leaveTextLength = text.length();
		int rmmMaxLength = this.maxLength < leaveTextLength ? this.maxLength
				: leaveTextLength;

		if (rmmMaxLength == 0) {
			return null;
		}
		while (true) {
			if (leaveTextLength <= 1) {
				break;
			}
			for (int i = rmmMaxLength; i > 0; i--) {
				if (i > leaveTextLength) {

				} else if (i == 1) {
					text = text.substring(1);
					leaveTextLength--;
				} else {
					String segment = text.substring(0, i);
					if (this.wd.containsWord(segment)) {
						words.add(segment);
						text = text.substring(i);
						leaveTextLength -= i;
						break;
					}
				}
			}
		}

		return words;
	}

}

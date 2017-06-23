package com.fmyblack.word;

import java.util.List;

public interface WordSegmenter {

	public abstract List<String> segment(String s);
}

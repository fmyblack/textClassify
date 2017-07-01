package com.fmyblack.word;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fmyblack.word.rmm.Rmm;

public class RmmTests {

	Rmm rmm;
	
	@Before
	public void setUp() {
		rmm = Rmm.getIns();
	}
	
	@Test
	public void testRmm() {
		String text = "测试一句话的分词效果";
		List<String> result = Arrays.asList(new String[]{"效果", "分词", "一句话", "测试"});
		assertEquals(result, rmm.segment(text));
	}
	
	@After
	public void tearDown() {
		rmm = null;
	}
	
}

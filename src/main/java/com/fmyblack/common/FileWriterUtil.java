package com.fmyblack.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterUtil {

	/**
	 * 少量文本
	 * @param fileName
	 * @param isAppend
	 */
	public static void writeFile(String fileName, boolean isAppend) {
		try {
			FileWriter fw = new FileWriter(fileName, isAppend);
			fw.write("hello world\n");
			fw.write("the second line\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeFileUseBuffer(String fileName, boolean isAppend) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, isAppend));
			bw.write("hello world\n");
			bw.write("the second line\n");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String fileName = "/Users/fmyblack/javaproject/fmyblack-util/src/main/resources/data/testwrite.data";
		writeFileUseBuffer(fileName, true);
	}
}

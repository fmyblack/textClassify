package com.fmyblack.textClassify.cosines.single;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fmyblack.common.FileReaderUtil;
import com.fmyblack.textClassify.tfidf.Document;
import com.fmyblack.textClassify.tfidf.TagBase;
import com.fmyblack.word.Rmm;

public class Seeds {

	Rmm rmm;
	String seedsDirPath;
	Set<String> wordsSet = new HashSet<String>();
	List<TagBase> tags;
	int documentsNum = 0;
	
	public Seeds(String seedsDirPath) {
		this.seedsDirPath = seedsDirPath;
		rmm = new Rmm();
		this.readSeeds();
	}
	
	public List<TagBase> getTags() {
		return this.tags;
	}
	
	public Set<String> getWords() {
		return this.wordsSet;
	}
	
	public int getDocumentsNum() {
		return this.documentsNum;
	}
	
	public void readSeeds() {
		this.tags = new ArrayList<TagBase>();
		File seedsDir = new File(this.seedsDirPath);
		String[] seedsName = seedsDir.list();
		for(String seedName : seedsName) {
			String tagSeedPath = this.seedsDirPath + File.separator + seedName;
			this.tags.add(readTagSeed(tagSeedPath, seedName));
		}
	}
	
	public Tag readTagSeed(String tagSeedPath, String name) {
		Tag tag = new Tag(name);
		File tagSeedDir = new File(tagSeedPath);
		for(String seedName : tagSeedDir.list()) {
			seedName = tagSeedPath + File.separator + seedName;
			String text = FileReaderUtil.readFile(seedName);
			this.documentsNum++;
			List<String> words = rmm.rmmSegment(text);
			wordsSet.addAll(words);
			Document doc = new Document(words);
			tag.addDoc(doc);
		}
		return tag;
	}
}

package com.fmyblack.word;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Rmm implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5169484994968903393L;
	
	private WordDictionary wdBc = null;

    public static void main(String[] args) throws IOException, IllegalAccessException {
//        final WordDictionary wordDict = WordDictionary.getIns();
        Rmm wp = new Rmm();
//        wp.wdBc = wordDict;
        List<String> words = wp.rmmSegment("海军首次举行授剑仪式 军方3大佩剑都长啥样？_国内新闻_环球网");
        System.out.println(words.toString());
    }
    
    public Rmm() {
        try {
            this.wdBc = WordDictionary.getIns();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param segment
     */
    public List<String> rmmSegment(String segment){
        List<String> list = new ArrayList<String>();
        
        String s1 = segment;
        String wordBefore = null;
        
        String w = null;
        
        while(true){
            //返回结果
            if(null == s1 || s1.isEmpty()){
                break;
            }
            int s1Len = s1.length();
            
            //截取长度
            Integer segmentLen = this.wdBc.getMaxLen();
            // 取最小的那个段长度
            segmentLen = s1Len < segmentLen ? s1Len : segmentLen;
            
            // 如果前面分词成功，则重新取词，否则继续分词之前的w
            if(null == w){
                w = s1.substring(s1Len - segmentLen, s1Len);
            }
            //只有一个字
            char c = w.charAt(0);
            String dw = w.substring(1);
            
            String nextS1 = s1.substring(0, s1Len-w.length());
            String nextBeforeWord = w;
            String exitNoSingleWord = null;
            
            //当前使用词库中存在
            boolean isWordMatched = false;
            if(this.wdBc.containUsingWord(w) || this.wdBc.containStopWord(w)){
                //w是否存在于停止词库中
                isWordMatched = true;
            } else {
                if(w.length() == 1){
                    // 排除windows文件的开头标识
                    if (c == 65279){
                        isWordMatched = false;
                    }
                }else{
                    //boolean flag = ;
                    // c为数值或“-”
                    // 或c为英语单字
                    // 则匹配成功，否则匹配失败
                    if(!(
                            (
                                    (('-' == c) || ('0' <= c && c <= '9')) &&
                                    this.wdBc.existNumericStr(dw)
                            ) || (
                                    (('a'<=c && c<='z') || ('A'<=c && c<='Z')) &&
                                    this.wdBc.existNumericStr(dw)
                            )
                        )) {
                        isWordMatched = false;
                        
                        // 匹配w为词语失败【去掉最左边的一个字c，继续匹配w-c=dw】
                        nextS1 = s1;
                        nextBeforeWord = wordBefore;
                        exitNoSingleWord = dw;
                    }
                }
            }
            if (isWordMatched){
                // w存在于当前使用词库，是否存在于停止词库的处理
                if(! this.wdBc.containStopWord(w)){
                    //　w不是停止词
                    // 添加新识别的词
//                    words.addWord(w, wordBefore);
                    // 更新词语的before
//                    words.addBefore(wordBefore, w);
                }
                list.add(w);
            }
            
            // 继续nextS1进行分词，前词为nextBeforeWord
            s1 = nextS1;
            wordBefore = nextBeforeWord;
            w = exitNoSingleWord;
        }
        return list;
    }
}

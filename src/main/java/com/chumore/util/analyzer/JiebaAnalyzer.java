package com.chumore.util.analyzer;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.lucene.analysis.Analyzer;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import org.apache.lucene.analysis.Tokenizer;

public class JiebaAnalyzer extends Analyzer {

    private final JiebaSegmenter.SegMode segMode;

    public JiebaAnalyzer(SegMode segMode) {
        this.segMode = segMode;
    }

    // 將 tokenizer 包裝 成 tokenStreamComponents
    @Override
    protected TokenStreamComponents createComponents(String fieldName){
        Tokenizer tokenizer = new JiebaTokenizer(segMode);
        return new TokenStreamComponents(tokenizer);
    }

}

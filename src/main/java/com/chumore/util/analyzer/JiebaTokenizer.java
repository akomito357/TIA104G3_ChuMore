package com.chumore.util.analyzer;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
public class JiebaTokenizer extends Tokenizer {
    private final JiebaSegmenter segmenter = new JiebaSegmenter();
    private final JiebaSegmenter.SegMode segMode;

    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAttr = addAttribute(PositionIncrementAttribute.class);

    private List<SegToken> tokens = new ArrayList<>();
    private int currentTokenIndex = 0;
    private String text;

    public JiebaTokenizer(JiebaSegmenter.SegMode segMode) {
        super();
        this.segMode = segMode;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (currentTokenIndex < tokens.size()) {
            clearAttributes();
            SegToken segToken = tokens.get(currentTokenIndex++);

            if (segToken.startOffset < 0 || segToken.endOffset < segToken.startOffset) {
                throw new IllegalStateException("Invalid token offsets: " +
                        "startOffset=" + segToken.startOffset +
                        ", endOffset=" + segToken.endOffset);
            }

            termAttr.append(segToken.word);
            termAttr.setLength(segToken.word.length());
            posIncrAttr.setPositionIncrement(1);
            offsetAttr.setOffset(segToken.startOffset, segToken.endOffset);

            return true;
        }
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        text = readToString(input);

        tokens = new ArrayList<>();
        int currentOffset = 0;
        for (String word : segmenter.sentenceProcess(text)) {
            int startOffset = text.indexOf(word, currentOffset);
            int endOffset = startOffset + word.length();
            tokens.add(new SegToken(word, startOffset, endOffset));
            currentOffset = endOffset;
        }

        currentTokenIndex = 0;
    }

    @Override
    public void end() throws IOException {
        super.end();
        final int finalOffset = text == null ? 0 : text.length();
        offsetAttr.setOffset(finalOffset, finalOffset);
    }

    private String readToString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        while ((len = reader.read(buf)) != -1) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}

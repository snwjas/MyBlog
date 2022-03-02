package xyz.snwjas.blog.support.wordfilter;

import java.util.List;

/**
 * 敏感词标记
 *
 * @author minghu.zhang
 */
public class FlagIndex {

    /**
     * 标记结果
     */
    private boolean flag;
    /**
     * 是否白名单词汇
     */
    private boolean isWhiteWord;
    /**
     * 标记索引
     */
    private List<Integer> index;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public List<Integer> getIndex() {
        return index;
    }

    public void setIndex(List<Integer> index) {
        this.index = index;
    }

    public boolean isWhiteWord() {
        return isWhiteWord;
    }

    public void setWhiteWord(boolean whiteWord) {
        isWhiteWord = whiteWord;
    }
}

package quiz.englishTest.model;

import java.io.Serializable;

public class Word implements Serializable {

    private int wordId;
    private String text;

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}

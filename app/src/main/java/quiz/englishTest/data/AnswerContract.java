package quiz.englishTest.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import quiz.englishTest.model.Answer;

import static android.provider.BaseColumns._ID;

public class AnswerContract extends EnglishTestDbHelper implements Serializable {

    static final String TABLE_NAME = "answers";
    static final String WORD_ID = "wordId";
    static final String TEXT = "text";
    static final String CORRECT = "correct";

    private static final int FIELD_ID_ID = 0;
    private static final int FIELD_ID_WORD_ID = 1;
    private static final int FIELD_ID_TEXT = 2;
    private static final int FIELD_ID_CORRECT = 3;

    private String[] selectedFields = {_ID, WORD_ID, TEXT, CORRECT};

    public AnswerContract(Context context) {
        super(context);
    }

    public Map<Integer, Answer> getAnswersByWordId(int wordId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query
                (TABLE_NAME,
                        selectedFields,
                        WORD_ID + "=" + wordId,
                        null, null, null, null);

        Map<Integer, Answer> answers = new HashMap<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Answer answer = getAnswerFromCursor(cursor);
                answers.put(answer.getAnswerId(), answer);
            }
        }
        db.close();
        return answers;
    }

    private Answer getAnswerFromCursor(Cursor cursor) {
        Answer answer = new Answer();
        answer.setAnswerId(cursor.getInt(FIELD_ID_ID));
        answer.setWordId(cursor.getInt(FIELD_ID_WORD_ID));
        answer.setText(cursor.getString(FIELD_ID_TEXT));

        boolean correct = (cursor.getInt(FIELD_ID_CORRECT) == 1);
        answer.setCorrect(correct);

        return answer;
    }
}

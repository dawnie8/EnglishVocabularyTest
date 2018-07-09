package quiz.englishTest.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import quiz.englishTest.model.Word;

import static android.provider.BaseColumns._ID;

public class WordsContract extends EnglishTestDbHelper {

    static final String TABLE_NAME = "words";
    static final String TEXT = "text";

    private static final int FIELD_ID_ID = 0;
    private static final int FIELD_ID_TEXT = 1;

    private String[] selectedFields = {_ID, TEXT};

    public WordsContract(Context context) {
        super(context);
    }

    public HashMap<Integer,Word> getWords(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                selectedFields,
                null, null, null, null, null);
        HashMap<Integer,Word> words = new HashMap<>();

        if (cursor != null){
            while (cursor.moveToNext()){
                Word word = getWordsFromCursor(cursor);
                words.put(word.getWordId(), word);
            }
            cursor.close();
        }
        db.close();
        return words;
    }

    private Word getWordsFromCursor(Cursor cursor){
        Word word = new Word();

        word.setWordId(cursor.getInt(FIELD_ID_ID));
        word.setText(cursor.getString(FIELD_ID_TEXT));

        return word;
    }
}


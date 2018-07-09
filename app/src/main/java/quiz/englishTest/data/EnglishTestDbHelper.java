package quiz.englishTest.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import quiz.englishTest.R;

import static android.provider.BaseColumns._ID;

class EnglishTestDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "englishTestDB";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    EnglishTestDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_WORDS =
                "CREATE TABLE "
                        + WordsContract.TABLE_NAME + " ("
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + WordsContract.TEXT + " TEXT NOT NULL"
                        + ");";

        final String CREATE_TABLE_ANSWERS =
                "CREATE TABLE " + AnswerContract.TABLE_NAME + " ("
                        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + AnswerContract.WORD_ID + " INTEGER NOT NULL, "
                        + AnswerContract.TEXT + " TEXT NOT NULL, "
                        + AnswerContract.CORRECT + " INTEGER NOT NULL"
                        + ");";

        db.execSQL(CREATE_TABLE_WORDS);
        db.execSQL(CREATE_TABLE_ANSWERS);

        importWords(db, R.raw.words);
    }

    private void importWords(SQLiteDatabase db, int resourceId) {
        List<String[]> records = readDataFromCsvFile(resourceId);
        String[] recordData;

        long wordId;
        ContentValues values;
        for (int i = 0; i < records.size(); i++) {
            recordData = records.get(i);

            values = new ContentValues();
            values.put(WordsContract.TEXT, recordData[1]);

            wordId = db.insert(WordsContract.TABLE_NAME, _ID, values);

            importAnswers(db, wordId, recordData[2], true);
            importAnswers(db, wordId, recordData[3], false);
            importAnswers(db, wordId, recordData[4], false);
        }
    }

    private void importAnswers(SQLiteDatabase db, long wordId, String answerText, boolean isCorrect) {
        ContentValues values = new ContentValues();

        values.put(AnswerContract.WORD_ID, wordId);
        values.put(AnswerContract.TEXT, answerText);
        values.put(AnswerContract.CORRECT, isCorrect);

        db.insert(AnswerContract.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AnswerContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WordsContract.TABLE_NAME);
        onCreate(db);
    }

    private List<String[]> readDataFromCsvFile(int resourceId) {
        List<String[]> records = null;

        try {
            char separator = ';';
            CSVReader reader = new CSVReader(
                    new InputStreamReader
                            (context.getResources().openRawResource(resourceId)), separator);
            records = reader.readAll();
            reader.close();
        } catch (IOException e) {
            Log.e("EnglishTest", "Error while reading the csv file " + e.getMessage());
            e.printStackTrace();
        }
        return records;
    }

}

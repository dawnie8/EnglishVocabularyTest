package quiz.englishTest.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import quiz.englishTest.data.AnswerContract;
import quiz.englishTest.data.WordsContract;
import quiz.englishTest.R;
import quiz.englishTest.model.Answer;
import quiz.englishTest.model.Word;

public class QuestionActivity extends AppCompatActivity {

    private Map<Integer, Word> questions;

    private AnswerContract answerData;
    private Map<Integer, Answer> answers;
    private Word question;

    private TextView questionTextView;

    private List<Integer> questionsIndexList;
    private int nextQuestionIndex = 0;

    private int questionNum = 0;
    private int correctlyAnswered = 0;

    private CountDownTimer timer;

    private Button answerButton1;
    private Button answerButton2;
    private Button answerButton3;

    private boolean isInBackground;

    private ProgressBar progressBar;

    private ImageView currentImage;
    private ArrayList<ImageView> imagesArray;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionTextView = findViewById(R.id.questionTextView);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);

        progressBar = findViewById(R.id.progressBar);
        ImageView image1 = findViewById(R.id.image1);
        ImageView image2 = findViewById(R.id.image2);
        ImageView image3 = findViewById(R.id.image3);
        ImageView image4 = findViewById(R.id.image4);
        ImageView image5 = findViewById(R.id.image5);
        ImageView image6 = findViewById(R.id.image6);
        ImageView image7 = findViewById(R.id.image7);
        ImageView image8 = findViewById(R.id.image8);
        ImageView image9 = findViewById(R.id.image9);
        ImageView image10 = findViewById(R.id.image10);

        imagesArray = new ArrayList<>();
        imagesArray.add(image1);
        imagesArray.add(image2);
        imagesArray.add(image3);
        imagesArray.add(image4);
        imagesArray.add(image5);
        imagesArray.add(image6);
        imagesArray.add(image7);
        imagesArray.add(image8);
        imagesArray.add(image9);
        imagesArray.add(image10);

        question = new Word();
        answerData = new AnswerContract(this);

        WordsContract questionData = new WordsContract(this);
        questions = questionData.getWords();

        questionsIndexList = new ArrayList<>();
        for (int questionId : questions.keySet()) {
            questionsIndexList.add(questionId);
        }

        Collections.shuffle(questionsIndexList);

        displayNextQuestion();

    }

    private void displayNextQuestion() {
        question = takeNextQuestion();
        displayQuestion(question);
        answerButton1.setEnabled(true);
        answerButton2.setEnabled(true);
        answerButton3.setEnabled(true);
        createTimer();
    }

    private void createTimer() {
        timer = new CountDownTimer(15400, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished / 100);
                progressBar.setProgress(progressBar.getMax() - (150 - progress));
            }

            @Override
            public void onFinish() {
                currentImage.setImageResource(R.drawable.error);
                if (isSoundOn() && !isInBackground) {
                    playSoundWrong();
                }
                if (questionNum < 10) {
                    timer.cancel();
                    displayNextQuestion();
                } else {
                    showResult();
                }
            }
        }.start();
    }

    private Word takeNextQuestion() {
        int nextQuestionId = questionsIndexList.get(nextQuestionIndex);
        Word question = questions.get(nextQuestionId);
        currentImage = imagesArray.get(questionNum);
        questionNum++;
        nextQuestionIndex++;
        return question;
    }

    private void displayQuestion(Word question) {

        if (question != null) {
            answers = answerData.getAnswersByWordId(question.getWordId());
            List<Integer> myAnswersIndexList = new ArrayList<>();
            for (int answerId : answers.keySet()) {
                myAnswersIndexList.add(answerId);
            }
            Collections.shuffle(myAnswersIndexList);
            questionTextView.setText(question.getText());

            if (answers != null) {
                Answer answer1;
                Answer answer2;
                Answer answer3;

                int answerId1 = myAnswersIndexList.get(0);
                int answerId2 = myAnswersIndexList.get(1);
                int answerId3 = myAnswersIndexList.get(2);

                answer1 = answers.get(answerId1);
                answer2 = answers.get(answerId2);
                answer3 = answers.get(answerId3);
                answerButton1.setId(answer1.getAnswerId());
                answerButton2.setId(answer2.getAnswerId());
                answerButton3.setId(answer3.getAnswerId());
                answerButton1.setText(answer1.getText());
                answerButton2.setText(answer2.getText());
                answerButton3.setText(answer3.getText());
            }
        }
    }

    public void answerButtonClickHandler(View v) {

        final View buttonClicked = v;
        timer.cancel();
        answerButton1.setEnabled(false);
        answerButton2.setEnabled(false);
        answerButton3.setEnabled(false);
        final Answer answer = answers.get(v.getId());

        if (answer.isCorrect()) {
            buttonClicked.setBackgroundResource(R.drawable.button_selected_correct);
            correctlyAnswered++;
            currentImage.setImageResource(R.drawable.success);
            if (isSoundOn()) {
                playSoundCorrect();
            }

        } else {
            buttonClicked.setBackgroundResource(R.drawable.button_selected_incorrect);
            currentImage.setImageResource(R.drawable.error);
            if (isSoundOn()) {
                playSoundWrong();
            }
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (questionNum < 10) {
                    timer.cancel();
                    buttonClicked.setBackgroundResource(R.drawable.button);
                    displayNextQuestion();
                } else {
                    showResult();
                }
            }
        }, 1800);
    }

    public void playSoundCorrect() {
        mediaPlayer = MediaPlayer.create(this, R.raw.correct);
        if (Build.VERSION.SDK_INT >= 21) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
        } else {
            //noinspection deprecation
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });
    }

    public void playSoundWrong() {
        mediaPlayer = MediaPlayer.create(this, R.raw.mistake);
        if (Build.VERSION.SDK_INT >= 21) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
        } else {
            //noinspection deprecation
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });
    }

    public void showResult() {
        SharedPreferences preferences = getSharedPreferences("preference_file", MODE_PRIVATE);
        int testCount = preferences.getInt("test_count", 0);
        testCount++;
        int overallScore_old = preferences.getInt("overall_score", 0);
        int overallScore_new = overallScore_old + correctlyAnswered;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("test_count", testCount);
        editor.putInt("last_result", correctlyAnswered);
        editor.putInt("overall_score", overallScore_new);
        editor.apply();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public boolean isSoundOn() {
        SharedPreferences preferences = getSharedPreferences("preference_file", MODE_PRIVATE);
        return preferences.getBoolean("sound_preference", false);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        isInBackground = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInBackground = true;
    }

}


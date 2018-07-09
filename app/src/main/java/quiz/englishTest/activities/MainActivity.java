package quiz.englishTest.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import quiz.englishTest.R;

public class MainActivity extends AppCompatActivity {

    private ToggleButton soundToggle;
    private int last_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView lastResultTv = findViewById(R.id.results_last);
        TextView overallResultTv = findViewById(R.id.results_overall);
        TextView resultsAndTestCount = findViewById(R.id.results_and_test_count);

        SharedPreferences preferences = getSharedPreferences("preference_file", MODE_PRIVATE);
        last_score = preferences.getInt("last_result", -1);
        if (last_score != -1) {
            lastResultTv.setText(getString(R.string.last_score, String.valueOf(last_score)));
        } else {
            lastResultTv.setText("-");
        }

        int testCount = preferences.getInt("test_count", 0);
        int overallScore = preferences.getInt("overall_score", 0);
        resultsAndTestCount.setText(getString(R.string.overall_result, String.valueOf(testCount)));
        if (testCount != 0) {
            int average_performance = 10 * overallScore / testCount;
            overallResultTv.setText(average_performance + "%");
        } else {
            overallResultTv.setText("-");
        }

        soundToggle = findViewById(R.id.sound_toggle);
        if (isSoundOn()) {
            soundToggle.setChecked(true);
        }

        Button newRoundButton = findViewById(R.id.newRoundButton);

        newRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

    }

    private void startGame() {
        Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
        intent.putExtra("score", last_score);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void saveMuteState(View v) {
        SharedPreferences preferences = getSharedPreferences("preference_file", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sound_preference", soundToggle.isChecked());
        editor.apply();
    }

    public boolean isSoundOn() {
        SharedPreferences preferences = getSharedPreferences("preference_file", MODE_PRIVATE);
        return preferences.getBoolean("sound_preference", false);
    }


}

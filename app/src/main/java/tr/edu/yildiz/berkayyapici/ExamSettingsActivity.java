package tr.edu.yildiz.berkayyapici;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ExamSettingsActivity extends AppCompatActivity {
    TextInputLayout examDuration, questionScore;
    Spinner examDifficulty;
    Button applyButton;

    String duration, score;
    Integer difficulty;

    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_settings);
        defineVariables();
        getSettings();
        defineListeners();
    }

    private void getSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("exam", MODE_PRIVATE);
        duration = sharedPreferences.getString("duration","");
        score = sharedPreferences.getString("score", "");
        difficulty = sharedPreferences.getInt("difficulty", 6);

        Objects.requireNonNull(examDuration.getEditText()).setText(duration);
        Objects.requireNonNull(questionScore.getEditText()).setText(score);
        examDifficulty.setSelection(6 - difficulty);
    }

    private void storeSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("exam", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("duration", duration);
        editor.putString("score", score);
        editor.putInt("difficulty", 6 - difficulty);
        editor.apply();
    }

    private void defineListeners() {
        applyButton.setOnClickListener(v -> {
            applyButton.startAnimation(scaleUp);
            applyButton.startAnimation(scaleDown);
            if(checkFields()) {
                storeSettings();
                Toast.makeText(ExamSettingsActivity.this, "Settings applied", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(ExamSettingsActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean checkFields() {
        duration = Objects.requireNonNull(examDuration.getEditText()).getText().toString();
        score = Objects.requireNonNull(questionScore.getEditText()).getText().toString();
        difficulty = examDifficulty.getSelectedItemPosition();

        if(duration.isEmpty())
            return false;
        else if(score.isEmpty())
            return false;
        else return difficulty != 0;
    }

    private void defineVariables() {
        examDuration = findViewById(R.id.examDuration);
        questionScore = findViewById(R.id.questionScore);
        examDifficulty = findViewById(R.id.examDifficulty);
        applyButton = findViewById(R.id.applySettings);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.exam_difficulty, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        examDifficulty.setAdapter(adapter);
        examDifficulty.setPrompt("Choose");

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }
}
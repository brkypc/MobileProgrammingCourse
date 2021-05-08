package tr.edu.yildiz.berkayyapici;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class AddQuestionActivity extends AppCompatActivity {
    TextInputLayout enteredQuestion, choice1, choice2, choice3, choice4;
    Button saveQuestion;

    String question, textChoice1, textChoice2, textChoice3, textChoice4;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        defineVariables();
        defineListeners();
    }

    private void defineVariables() {
        enteredQuestion = findViewById(R.id.enteredQuestion);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        saveQuestion = findViewById(R.id.saveQuestion);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private void defineListeners() {
        saveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion.startAnimation(scaleUp);
                saveQuestion.startAnimation(scaleDown);
                if(validateFields()) {
                    //add to the file
                    clearFields();
                    enteredQuestion.requestFocus();
                    Toast.makeText(AddQuestionActivity.this, "Question added to the database.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AddQuestionActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearFields() {
        enteredQuestion.getEditText().setText("");
        choice1.getEditText().setText("");
        choice2.getEditText().setText("");
        choice3.getEditText().setText("");
        choice4.getEditText().setText("");
    }

    private boolean validateFields() {
        getTextFromFields();

        if(question.isEmpty()) {
            return false;
        }
        else if(textChoice1.isEmpty()) {
            return false;
        }
        else if(textChoice2.isEmpty()) {
            return false;
        }
        else if(textChoice3.isEmpty()) {
            return false;
        }
        else if(textChoice4.isEmpty()) {
            return false;
        }
        else
            return true;
    }

    private void getTextFromFields() {
        question = enteredQuestion.getEditText().getText().toString();
        textChoice1 = choice1.getEditText().getText().toString();
        textChoice2 = choice2.getEditText().getText().toString();
        textChoice3 = choice3.getEditText().getText().toString();
        textChoice4 = choice4.getEditText().getText().toString();
    }
}
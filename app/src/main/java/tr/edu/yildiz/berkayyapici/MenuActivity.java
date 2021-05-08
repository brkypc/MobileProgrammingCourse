package tr.edu.yildiz.berkayyapici;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout questionsList, addQuestion, examsList, createExam;
    TextView welcomeText;
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        defineVariables();
        defineListeners();
    }

    private void defineVariables() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        String name = sharedPreferences.getString("name","");
        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome " + name);
        logout = findViewById(R.id.logout);
        questionsList = findViewById(R.id.first);
        addQuestion = findViewById(R.id.second);
        examsList = findViewById(R.id.third);
        createExam = findViewById(R.id.fourth);
    }

    private void defineListeners() {
        questionsList.setOnClickListener(this);
        addQuestion.setOnClickListener(this);
        examsList.setOnClickListener(this);
        createExam.setOnClickListener(this);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if(v.getId() == R.id.first) {
            intent = new Intent(MenuActivity.this, QuestionsActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.second) {
            intent = new Intent(MenuActivity.this, AddQuestionActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.third) {
            intent = new Intent(MenuActivity.this, ExamsActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.fourth) {
            intent = new Intent(MenuActivity.this, ExamsActivity.class);
            startActivity(intent);
        }
    }
}
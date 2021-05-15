package tr.edu.yildiz.berkayyapici;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout questionsList, addQuestion, examsList, createExam;
    TextView welcomeText;
    ImageView menuPicture;

    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        defineVariables();
        defineListeners();
    }

    private void defineVariables() {
        // get current user
        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");

        DatabaseHelper databaseHelper = new DatabaseHelper(MenuActivity.this);
        Cursor cursor = databaseHelper.getUser(username);
        if(cursor.moveToFirst()) {
            name = cursor.getString(2);
        }
        String welcome = "Welcome " + name;
        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(welcome);

        menuPicture = findViewById(R.id.menuPicture);

        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File myFolder = new File(folder, "Mobile Programming App/" + username);
        if (myFolder.exists()) {
            File file = new File(myFolder, "pp.jpg");
            if (file.exists())
                menuPicture.setImageURI(Uri.parse(file.getAbsolutePath()));
        }

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
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if(v.getId() == R.id.first) {
            intent = new Intent(MenuActivity.this, AddUpdateQuestionActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.second) {
            intent = new Intent(MenuActivity.this, QuestionsActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.third) {
            intent = new Intent(MenuActivity.this, ExamSettingsActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.fourth) {
            intent = new Intent(MenuActivity.this, CreateExamActivity.class);
            startActivity(intent);
        }
    }
}
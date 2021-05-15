package tr.edu.yildiz.berkayyapici;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CreateExamActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 555;

    Button createExamButton, shareExam;

    Animation scaleUp, scaleDown;

    ArrayList<Question> questionsList;
    ArrayList<Boolean> checkedList = new ArrayList<>();
    String duration, score, username;
    int difficulty;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);

        defineButtons();

        SharedPreferences sharedPreferences = getSharedPreferences("exam", MODE_PRIVATE);
        duration = sharedPreferences.getString("duration","");
        score = sharedPreferences.getString("score", "");
        difficulty = sharedPreferences.getInt("difficulty", 0);

        SharedPreferences preferences = getSharedPreferences("currentUser", MODE_PRIVATE);
        username = preferences.getString("username","");

        if(difficulty > 0) {
            databaseHelper = new DatabaseHelper(CreateExamActivity.this);
            questionsList = databaseHelper.getQuestions();
            if (questionsList != null) {
                createList();
                defineListeners();
            }
            else {
                TextView noText = findViewById(R.id.noText2);
                noText.setVisibility(View.VISIBLE);
                createExamButton.setVisibility(View.GONE);
                shareExam.setVisibility(View.GONE);
            }
        }
        else {
            createExamButton.setVisibility(View.INVISIBLE);
            shareExam.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Your exam settings are not set", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(folder, "Mobile Programming App/" + username);
            if(file.exists()) {
                saveExamFile();
            }
            else {
                if(file.mkdirs())
                    saveExamFile();
                else
                    Toast.makeText(this, "Could not write to file", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void defineListeners() {
        createExamButton.setOnClickListener(v -> {
            createExamButton.startAnimation(scaleUp);
            createExamButton.startAnimation(scaleDown);
            if (ContextCompat.checkSelfPermission(CreateExamActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateExamActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
            else {
                saveExamFile();
            }
         });

        shareExam.setOnClickListener(v -> {
            shareExam.startAnimation(scaleUp);
            shareExam.startAnimation(scaleDown);

            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File myFolder = new File(folder, "Mobile Programming App/" + username);
            File file = new File(myFolder,"exam.txt");
            if(file.exists()) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.getAbsolutePath()));
                startActivity(Intent.createChooser(share, "Share exam file with"));
            }
            else {
                Toast.makeText(CreateExamActivity.this, "You did not create any exam", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveExamFile() {
        checkedList = checkList(questionsList.size());
        if(checkedList.contains(true)) {
            //create exam
            Toast.makeText(CreateExamActivity.this, "Exam created", Toast.LENGTH_SHORT).show();
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File myFolder = new File(folder, "Mobile Programming App/" + username);
            File file = new File(myFolder,"exam.txt");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(("User : " + username + " Exam"+ "\n").getBytes());
                fos.write(("Exam Duration: " + duration+ "\n").getBytes());
                fos.write(("Question Score: " + score+ "\n").getBytes());
                fos.write(("Exam Difficulty: " + difficulty + " choices\n\n"+ "Questions\n\n").getBytes());
                for(int i = 0; i < questionsList.size(); i++)  {
                    if(checkedList.get(i)) {
                        fos.write((questionsList.get(i).getQuestion() + "\n").getBytes());
                        fos.write((questionsList.get(i).getAnswer() + "\n").getBytes());
                        fos.write((questionsList.get(i).getChoice1() + "\n").getBytes());
                        switch (difficulty) {
                            case 2:
                                fos.write(("-----\n").getBytes());
                                break;
                            case 3:
                                fos.write((questionsList.get(i).getChoice2() + "\n----\n").getBytes());
                                break;
                            case 4:
                                fos.write((questionsList.get(i).getChoice2() + "\n").getBytes());
                                fos.write((questionsList.get(i).getChoice3() + "\n----\n").getBytes());
                                break;
                            case 5:
                                fos.write((questionsList.get(i).getChoice2() + "\n").getBytes());
                                fos.write((questionsList.get(i).getChoice3() + "\n").getBytes());
                                fos.write((questionsList.get(i).getChoice4() + "\n----\n").getBytes());
                                break;
                        }
                    }
                }
            } catch (IOException e) {
               Log.e("myTAG","Failed to write: " + e.getMessage());
            }
        }
        else {
            Toast.makeText(CreateExamActivity.this, "You did not choose any question", Toast.LENGTH_SHORT).show();
        }
    }

    private void createList() {
        checkedList = checkList(questionsList.size());

        RecyclerView recyclerView = findViewById(R.id.rvCreateExam);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(questionsList.size());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CreateExamAdapter adapter = new CreateExamAdapter(this, questionsList, checkedList, difficulty);
        recyclerView.setAdapter(adapter);

        Animation recycler_animation = AnimationUtils.loadAnimation(this, R.anim.recycler_animation);
        recyclerView.startAnimation(recycler_animation);
    }

    private ArrayList<Boolean> checkList(int size) {
        ArrayList<Boolean> listFromFile = new ArrayList<>();

        try {
            InputStream inputStream = CreateExamActivity.this.openFileInput("checkedList.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    listFromFile.add(Boolean.valueOf(receiveString));
                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("myTAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e("myTAG", "Can not read file: " + e.getMessage());
        }

        if(listFromFile.size() == 0 || listFromFile.size() != size) {
            for(int i = listFromFile.size(); i < size; i++)  {
                listFromFile.add(false);
            }
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(CreateExamActivity.this.openFileOutput("checkedList.txt", Context.MODE_PRIVATE));
                for(int i = 0; i < size; i++)  {
                    outputStreamWriter.append(String.valueOf(listFromFile.get(i))).append("\n");
                }
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.getMessage());
            }
        }

        return listFromFile;
    }

    private void defineButtons() {
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);

        createExamButton = findViewById(R.id.createExamButton);
        shareExam = findViewById(R.id.shareExam);
    }
}
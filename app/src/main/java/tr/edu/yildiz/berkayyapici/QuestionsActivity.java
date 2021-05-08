package tr.edu.yildiz.berkayyapici;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class QuestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Questions questions = new Questions();
        ArrayList<String> questionsList = questions.getQuestions();
        String[][] choicesList = questions.getChoices();
        String[] answersList = questions.getAnswers();

        RecyclerView recyclerView = findViewById(R.id.rvQuestions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(9);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(this, questionsList, choicesList, answersList);
        recyclerView.setAdapter(adapter);
    }
}
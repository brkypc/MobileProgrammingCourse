package tr.edu.yildiz.berkayyapici;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        DatabaseHelper databaseHelper = new DatabaseHelper(QuestionsActivity.this);
        ArrayList<Question> questionsList = databaseHelper.getQuestions();
        if(questionsList != null) {
            RecyclerView recyclerView = findViewById(R.id.rvQuestions);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(questionsList.size());

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            MyAdapter adapter = new MyAdapter(this, questionsList);
            recyclerView.setAdapter(adapter);

            Animation recycler_animation = AnimationUtils.loadAnimation(this, R.anim.recycler_animation);
            recyclerView.startAnimation(recycler_animation);
        }
        else {
            TextView noText = findViewById(R.id.noText);
            noText.setVisibility(View.VISIBLE);
        }
    }
}
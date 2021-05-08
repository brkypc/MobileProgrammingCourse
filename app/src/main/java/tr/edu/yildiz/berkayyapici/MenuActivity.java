package tr.edu.yildiz.berkayyapici;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout questionsList, addQuestion, examsList, createExam;
    TextView welcomeText;
    ImageView logout;

    SensorManager sm = null;
    List list;

    SensorEventListener sel = new SensorEventListener(){
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        public void onSensorChanged(SensorEvent event) {

            float loX = event.values[0];
            float loY = event.values[1];
            float loZ = event.values[2];

            double loAccelerationReader = Math.sqrt(Math.pow(loX, 2)
                    + Math.pow(loY, 2)
                    + Math.pow(loZ, 2));

            if (loAccelerationReader > 0.3 && loAccelerationReader < 0.5) {
                Toast.makeText(MenuActivity.this, "You dropped your phone :)", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        defineVariables();
        defineListeners();

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(list.size()>0){
            sm.registerListener(sel, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Toast.makeText(MenuActivity.this, "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(list.size()>0){
            sm.unregisterListener(sel);
        }
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
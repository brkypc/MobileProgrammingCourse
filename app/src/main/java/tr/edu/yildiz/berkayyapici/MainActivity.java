package tr.edu.yildiz.berkayyapici;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Views
    View first, second, third, fourth, fifth, sixth;
    TextView middleText, middleNumber, bottomText;

    //Animations
    Animation topAnimation, middleTextAnimation, middleNumberAnimation, bottomAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        defineVariables();
        setAnimations();
        goToLoginActivity();
    }

    private void defineVariables() {
        //Views
        first = findViewById(R.id.first_line);
        second = findViewById(R.id.second_line);
        third = findViewById(R.id.third_line);
        fourth = findViewById(R.id.fourth_line);
        fifth = findViewById(R.id.fifth_line);
        sixth = findViewById(R.id.sixth_line);
        middleText = findViewById(R.id.middleText);
        middleNumber = findViewById(R.id.middleText2);
        bottomText = findViewById(R.id.bottomText);

        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        middleTextAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_text_animation);
        middleNumberAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_number_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
    }

    private void setAnimations() {
        first.setAnimation(topAnimation);
        second.setAnimation(topAnimation);
        third.setAnimation(topAnimation);
        fourth.setAnimation(topAnimation);
        fifth.setAnimation(topAnimation);
        sixth.setAnimation(topAnimation);
        middleText.setAnimation(middleTextAnimation);
        middleNumber.setAnimation(middleNumberAnimation);
        bottomText.setAnimation(bottomAnimation);
    }

    private void goToLoginActivity() {
        int SPLASH_SCREEN_TIME_OUT = 2000;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        },SPLASH_SCREEN_TIME_OUT);
    }
}
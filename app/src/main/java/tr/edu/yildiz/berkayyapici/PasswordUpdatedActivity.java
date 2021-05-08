package tr.edu.yildiz.berkayyapici;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class PasswordUpdatedActivity extends AppCompatActivity {
    Button goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_updated);

        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
        goToLogin = findViewById(R.id.goToLogin);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin.startAnimation(scaleUp);
                goToLogin.startAnimation(scaleDown);
                Intent intent = new Intent(PasswordUpdatedActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
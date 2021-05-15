package tr.edu.yildiz.berkayyapici;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextView attempt;
    TextInputLayout username, password;
    Button loginButton, goSignUp;
    ProgressBar progressBar;

    Animation scaleUp, scaleDown;

    DatabaseHelper databaseHelper;

    String userEnteredUsername, userEnteredPassword, hashPassword, attemptText;
    int attemptCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        defineVariables();
        defineListeners();
    }

    private void defineVariables() {
        databaseHelper = new DatabaseHelper(LoginActivity.this);

        attempt = findViewById(R.id.attempt);
        username = findViewById(R.id.loginUsername);
        password = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        goSignUp = findViewById(R.id.goSignUp);
        progressBar = findViewById(R.id.loginProgressBar);

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private void defineListeners() {
        loginButton.setOnClickListener(v -> {
            loginButton.startAnimation(scaleUp);
            loginButton.startAnimation(scaleDown);
            if(checkFields()) {
                loginButton.setClickable(false);
                hashPassword = hash(userEnteredPassword);

                String passwordFromDB = databaseHelper.getUserLoginInfo(userEnteredUsername);

                if(passwordFromDB.isEmpty()) {
                    username.setError("There is no such user exists");
                    username.requestFocus();
                    loginButton.setClickable(true);
                    checkAttempt();
                }
                else {
                    if(passwordFromDB.equals(hashPassword)) {
                        Toast.makeText(LoginActivity.this, "Login is successful. You are redirecting...", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);

                        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", userEnteredUsername);
                        editor.apply();

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish();
                        },1000);
                    }
                    else {
                        password.setError("Wrong password");
                        password.requestFocus();
                        loginButton.setClickable(true);
                        checkAttempt();
                    }
                }

            }
        });

        goSignUp.setOnClickListener(v -> {
            goSignUp.setClickable(false);
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkAttempt() {
        attemptCount++;
        attemptText = "Attempt : " + attemptCount + "/3";
        attempt.setText(attemptText);

        if(attemptCount == 3) {
            attempt.setTextColor(Color.RED);
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setAlpha(.5f);        // make the button color as beautiful gray
            loginButton.setClickable(false);  // same functionality with setEnabled(false)
            goSignUp.setClickable(false);

            Toast.makeText(this, "You entered wrong 3 times. You are redirecting to sign up.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            },1500);
        }
    }

    private boolean checkFields() {
        boolean validate = true;

        userEnteredUsername = Objects.requireNonNull(username.getEditText()).getText().toString();
        userEnteredPassword = Objects.requireNonNull(password.getEditText()).getText().toString();

        if(userEnteredUsername.isEmpty()) {
            username.setError("Field cannot be empty");
            validate = false;
        }
        else { username.setError(null); }

        if(userEnteredPassword.isEmpty()) {
            password.setError("Field cannot be empty");
            validate = false;
        }
        else { password.setError(null); }

        return validate;
    }

    public String hash(String password) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte[] messageDigest = digest.digest();

            StringBuilder hash = new StringBuilder();
            for (byte b : messageDigest)
                hash.append(Integer.toHexString(0xFF & b));

            return hash.toString();
        }catch (NoSuchAlgorithmException e) {
            Log.e("myTAG", e.getMessage());
        }
        return "";
    }
}
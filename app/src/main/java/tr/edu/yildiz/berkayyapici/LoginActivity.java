package tr.edu.yildiz.berkayyapici;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    TextView loginAppText, loginText;
    TextInputLayout username, password;
    Button loginButton, goSignUp;
    ProgressBar progressBar;
    String userEnteredUsername, userEnteredPassword, hashPassword;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);
        defineVariables();
        defineListeners();
    }

    private void defineVariables() {
        loginAppText = findViewById(R.id.loginAppText);
        loginText = findViewById(R.id.loginText);
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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setClickable(false);
                validateUser();
                loginButton.startAnimation(scaleUp);
                loginButton.startAnimation(scaleDown);
            }
        });

        goSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignUp.setClickable(false);
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean checkFields() {
        boolean validate = true;

        userEnteredUsername = username.getEditText().getText().toString();
        userEnteredPassword = password.getEditText().getText().toString();

        if(userEnteredUsername.isEmpty()) {
            username.setError("Field cannot be empty");
            validate = false;
        }
        else {
            username.setError(null);
        }

        if(userEnteredPassword.isEmpty()) {
            password.setError("Field cannot be empty");
            validate = false;
        }
        else {
            password.setError(null);
        }

        return validate;
    }

    private void validateUser() {
        if(checkFields()) {

        }
        else
            loginButton.setClickable(true);
    }

    public String hash(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
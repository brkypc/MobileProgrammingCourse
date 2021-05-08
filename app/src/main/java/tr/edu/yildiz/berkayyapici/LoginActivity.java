package tr.edu.yildiz.berkayyapici;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    TextView loginAppText, loginText;
    TextInputLayout username, password;
    CheckBox rememberMe;
    Button forgotPassword, loginButton, goSignUp;
    ProgressBar progressBar;
    String userEnteredUsername, userEnteredPassword, hashPassword;
    DatabaseReference database;
    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;
    boolean isConnected;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String isLoggedIn = sharedPreferences.getString("remember","");
        if(isLoggedIn.equals("true")) {
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }
        checkInternetConnection();
        defineVariables();
        defineListeners();
    }

    private void checkInternetConnection() {
        getInternetConnectionInfo(LoginActivity.this);
        if(!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Please connect to the internet and retry.")
                    .setCancelable(false)
                    .setPositiveButton("Retry", null)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isConnected)
                        dialog.dismiss();
                    else {
                        getInternetConnectionInfo(LoginActivity.this);
                        Toast.makeText(LoginActivity.this, "Please connect to internet and retry.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void getInternetConnectionInfo(LoginActivity loginActivity) {
        connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null && activeNetwork.isConnected();
    }

    private void defineVariables() {
        database = FirebaseDatabase.getInstance().getReference("users");
        loginAppText = findViewById(R.id.loginAppText);
        loginText = findViewById(R.id.loginText);
        username = findViewById(R.id.loginUsername);
        password = findViewById(R.id.loginPassword);
        rememberMe = findViewById(R.id.rememberMe);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginButton = findViewById(R.id.loginButton);
        goSignUp = findViewById(R.id.goSignUp);
        progressBar = findViewById(R.id.loginProgressBar);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private void defineListeners() {
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                }
                else {
                    SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

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
                /*Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View,String>(loginText, "text_tran");
                pairs[1] = new Pair<View,String>(username, "username_tran");
                pairs[2] = new Pair<View,String>(password, "password_tran");
                pairs[3] = new Pair<View,String>(loginButton, "button_tran");
                pairs[4] = new Pair<View,String>(goSignUp, "goButton_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
                finish();*/
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
            Query checkUser = database.orderByChild("username").equalTo(userEnteredUsername);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        progressBar.setVisibility(View.VISIBLE);
                        String passwordFromDB = snapshot.child(userEnteredUsername).child("password").getValue(String.class);
                        hashPassword = hash(userEnteredPassword);
                        if(passwordFromDB.equals(hashPassword)) {
                            String name = snapshot.child(userEnteredUsername).child("fullName").getValue(String.class);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("name", name);
                                    editor.apply();
                                    startActivity(intent);
                                    finish();
                                }
                            },1000);
                        }
                        else {
                            loginButton.setClickable(true);
                            password.setError("Wrong password");
                            password.requestFocus();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    else {
                        loginButton.setClickable(true);
                        username.setError("Wrong username");
                        username.requestFocus();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //database error
                }
            });
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
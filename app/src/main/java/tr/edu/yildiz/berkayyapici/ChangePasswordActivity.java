package tr.edu.yildiz.berkayyapici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChangePasswordActivity extends AppCompatActivity {
    TextInputLayout changedPassword, reChangedPassword;
    Button changePasswordButton;
    ProgressBar progressBar;
    String userPassword, userReEnterPassword, phoneNumberFromUser;
    DatabaseReference database;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        defineVariables();
        defineListeners();
    }

    private void defineVariables() {
        Intent intent = getIntent();
        phoneNumberFromUser = intent.getStringExtra("phoneNumber");
        changedPassword = findViewById(R.id.changedPassword);
        reChangedPassword = findViewById(R.id.reChangedPassword);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        progressBar = findViewById(R.id.changeProgressBar);
        database = FirebaseDatabase.getInstance().getReference("users");
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private void defineListeners() {
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordButton.startAnimation(scaleUp);
                changePasswordButton.startAnimation(scaleDown);
                if(validateFields()) {
                    Query checkUser = database.orderByChild("phoneNo").equalTo(phoneNumberFromUser);
                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                progressBar.setVisibility(View.VISIBLE);
                                changePasswordButton.setClickable(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
                                String username = sharedPreferences.getString("username","");
                                if(!username.isEmpty()) {
                                    database.child(username).child("password").setValue(hash(userPassword));
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ChangePasswordActivity.this, PasswordUpdatedActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //database error
                        }
                    });
                }
            }
        });
    }

    private boolean validateFields() {
        boolean validate = true;
        String checkPassword = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        userPassword = changedPassword.getEditText().getText().toString();
        userReEnterPassword = reChangedPassword.getEditText().getText().toString();

        if(userPassword.isEmpty()) {
            changedPassword.setError("Field cannot be empty");
            validate = false;
        }
        else if (!userPassword.matches(checkPassword)){
            changedPassword.setError("Password must contain minimum eight characters, at least one letter and one number");
            validate = false;
        }
        else {
            changedPassword.setError(null);
        }

        if(userReEnterPassword.isEmpty()) {
            reChangedPassword.setError("Field cannot be empty");
            validate = false;
        }
        else if (!userReEnterPassword.matches(checkPassword)){
            reChangedPassword.setError("Password must contain minimum eight characters, at least one letter and one number");
            validate = false;
        }
        else {
            reChangedPassword.setError(null);
        }

        if (!(userPassword.isEmpty()) && !(userReEnterPassword.isEmpty()) && !(userPassword.equals(userReEnterPassword))) {
            changedPassword.setError("Passwords doesn't match");
            reChangedPassword.setError("Passwords doesn't match");
            validate = false;
        }

        return validate;
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
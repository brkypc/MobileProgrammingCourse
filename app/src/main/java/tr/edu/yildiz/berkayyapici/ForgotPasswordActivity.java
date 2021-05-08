package tr.edu.yildiz.berkayyapici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputLayout phoneNo;
    Button nextButton;
    ProgressBar progressBar;
    String userPhoneNo;
    DatabaseReference database;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        defineVariables();
        defineListeners();
    }

    private void defineVariables() {
        database = FirebaseDatabase.getInstance().getReference("users");
        phoneNo = findViewById(R.id.forgotPasswordPhone);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.forgotProgressBar);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private void defineListeners() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton.startAnimation(scaleUp);
                nextButton.startAnimation(scaleDown);
                if(validateField()) {
                    validateUser();
                }
            }
        });
    }

    private void validateUser() {
        nextButton.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
        Query checkUser = database.orderByChild("phoneNo").equalTo(userPhoneNo);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String username = null;
                    for (DataSnapshot temp:snapshot.getChildren()) {
                        username = temp.child("username").getValue(String.class);
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ForgotPasswordActivity.this, CodeVerificationActivity.class);
                            intent.putExtra("phoneNumber", userPhoneNo);
                            startActivity(intent);
                            finish();
                        }
                    },1000);
                }
                else {
                    phoneNo.setError("There is no such user exists.");
                    phoneNo.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    nextButton.setClickable(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //database error
            }
        });
    }

    private boolean validateField() {
        userPhoneNo = phoneNo.getEditText().getText().toString();
        if(userPhoneNo.isEmpty()) {
            phoneNo.setError("Field cannot be empty");
            return false;
        }
        else if (userPhoneNo.length() < 10) {
            phoneNo.setError("Phone number must be 10 digits.");
            return false;
        }
        else {
            phoneNo.setError(null);
            return true;
        }
    }
}
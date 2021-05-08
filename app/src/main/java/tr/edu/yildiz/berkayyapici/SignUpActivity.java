package tr.edu.yildiz.berkayyapici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
    TextView appText, signUpText;
    TextInputLayout fullName, username, email, phoneNo, birthDate, password, reEnterPassword;
    Button signUpButton, goLogin;
    ProgressBar progressBar;
    DatabaseReference database;
    String userFullName, userUserName, userEmail, userPhoneNo, userBirthDate, userPassword, hashPassword, userReEnterPassword;
    Calendar calendar, c;
    int mYear, mMonth, mDayOfMonth, year, month, dayOfMonth;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_sign_up);
        defineVariables();
        defineListeners();
    }

    private void defineVariables() {
        database = FirebaseDatabase.getInstance().getReference("users");
        appText = findViewById(R.id.appText);
        signUpText = findViewById(R.id.signUpText);
        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phoneNo = findViewById(R.id.phoneNo);
        birthDate = findViewById(R.id.birthDate);
        password = findViewById(R.id.password);
        reEnterPassword = findViewById(R.id.reEnterPassword);
        signUpButton = findViewById(R.id.signUpButton);
        goLogin = findViewById(R.id.goLogin);
        progressBar = findViewById(R.id.progressBar);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR); year-=18;
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, dayOfMonth);
        mYear = year; mMonth = 0; mDayOfMonth = 1;
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private void defineListeners() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpButton.startAnimation(scaleUp);
                signUpButton.startAnimation(scaleDown);
                if (validateFields()) {
                    signUpButton.setClickable(false);
                    goLogin.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    Query checkUser = database.orderByChild("username").equalTo(userUserName);
                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                username.setError("This username is already in use, please try another one");
                                signUpButton.setClickable(true);
                                goLogin.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                hashPassword = hash(userPassword);
                                User user = new User(userFullName, userUserName, userEmail, userPhoneNo, userBirthDate, hashPassword);
                                database.child(userUserName).setValue(user);

                                Toast.makeText(SignUpActivity.this, "You have successfully signed up!", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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

        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                /*   Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View,String>(signUpText, "text_tran");
                pairs[1] = new Pair<View,String>(username, "username_tran");
                pairs[2] = new Pair<View,String>(password, "password_tran");
                pairs[3] = new Pair<View,String>(signUpButton, "button_tran");
                pairs[4] = new Pair<View,String>(goLogin, "goButton_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
                startActivity(intent, options.toBundle());
                */
            }
        });

        birthDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this, R.style.MySpinnerDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        String strDate = format.format(calendar.getTime());

                        birthDate.getEditText().setText(strDate);
                        mYear = year; mMonth = month; mDayOfMonth = dayOfMonth;
                    }
                }, mYear, mMonth, mDayOfMonth);
                dpd.setCancelable(false);
                dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                dpd.show();
            }
        });

        birthDate.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this, R.style.MySpinnerDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, dayOfMonth);
                            String strDate = format.format(calendar.getTime());

                            birthDate.getEditText().setText(strDate);
                            mYear = year; mMonth = month; mDayOfMonth = dayOfMonth;
                        }
                    }, mYear, mMonth, mDayOfMonth);
                    dpd.setCancelable(false);
                    dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                    dpd.show();
                }
            }
        });
    }

    private void getTextFromFields() {
        userFullName = fullName.getEditText().getText().toString();
        userUserName = username.getEditText().getText().toString();
        userEmail = email.getEditText().getText().toString();
        userPhoneNo = phoneNo.getEditText().getText().toString();
        userBirthDate = birthDate.getEditText().getText().toString();
        userPassword = password.getEditText().getText().toString();
        userReEnterPassword = reEnterPassword.getEditText().getText().toString();
    }

    private boolean validateFields() {
        boolean validate = true;
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
        String checkPassword = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        getTextFromFields();

        if(userFullName.isEmpty()) {
            fullName.setError("Field cannot be empty");
            validate = false;
        }
        else {
            fullName.setError(null);
            fullName.setErrorEnabled(false);
        }

        if(userUserName.isEmpty()) {
            username.setError("Field cannot be empty");
            validate = false;
        }
        else {
            username.setError(null);
            username.setErrorEnabled(false);
        }

        if(userEmail.isEmpty()) {
            email.setError("Field cannot be empty");
            validate = false;
        }
        else if (!userEmail.matches(checkEmail)) {
            email.setError("Invalid Email");
            validate = false;
        }
        else {
            email.setError(null);
            email.setErrorEnabled(false);
        }

        if(userPhoneNo.isEmpty()) {
            phoneNo.setError("Field cannot be empty");
            validate = false;
        }
        else if (userPhoneNo.length() < 10) {
            phoneNo.setError("Phone number must be 10 digits.");
            validate = false;
        }
        else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
        }

        if(userBirthDate.isEmpty()) {
            birthDate.setError("Field cannot be empty");
            validate = false;
        }
        else {
            birthDate.setError(null);
            birthDate.setErrorEnabled(false);
        }

        if(userPassword.isEmpty()) {
            password.setError("Field cannot be empty");
            validate = false;
        }
        else if (!userPassword.matches(checkPassword)){
            password.setError("Password must contain minimum eight characters, at least one letter and one number");
            validate = false;
        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);
        }

        if(userReEnterPassword.isEmpty()) {
            reEnterPassword.setError("Field cannot be empty");
            validate = false;
        }
        else if (!userReEnterPassword.matches(checkPassword)){
            reEnterPassword.setError("Password must contain minimum eight characters, at least one letter and one number");
            validate = false;
        }
        else {
            reEnterPassword.setError(null);
            reEnterPassword.setErrorEnabled(false);
        }

        if (!(userPassword.isEmpty()) && !(userReEnterPassword.isEmpty()) && !(userPassword.equals(userReEnterPassword))) {
            password.setError("Passwords doesn't match");
            reEnterPassword.setError("Passwords doesn't match");
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
package tr.edu.yildiz.berkayyapici;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 123;
    private static final int PERMISSION_CODE = 124;

    TextInputLayout username, fullName, email, phoneNo, birthDate, password, reEnterPassword;
    Button signUpButton, goLogin, chooseButton;
    ImageView profilePicture;
    ProgressBar progressBar;

    String userUserName, userFullName, userEmail, userPhoneNo, userBirthDate, userPassword, userReEnterPassword;
    String checkEmail, checkPassword, hashPassword;

    Calendar calendar;
    int mYear, mMonth, mDayOfMonth, year, month, dayOfMonth;

    Animation scaleUp, scaleDown;
    DatabaseHelper databaseHelper;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        defineVariables();
        defineListeners();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void savePhoto() {
        if(imageUri != null) {
            String path = getRealPathFromURI(imageUri);
            if(path != null) {
                File sourceFile = new File(path);
                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                File myFolder = new File(folder, "Mobile Programming App/" + userUserName);
                if (!myFolder.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    myFolder.mkdirs();
                }
                File destFile = new File(myFolder,"pp.jpg");
                try {
                    FileChannel source = new FileInputStream(sourceFile).getChannel();
                    FileChannel destination = new FileOutputStream(destFile).getChannel();
                    if (destination != null && source != null) destination.transferFrom(source, 0, source.size());
                    if (source != null) source.close();
                    if (destination != null) destination.close();
                } catch (IOException e) {
                    Log.d("myTAG","Failed:" + e.getMessage());
                }
            }
        }
    }

    private String getRealPathFromURI(Uri imageUri) {
        String[] projection = { MediaStore.Images.Media.DATA };

        @SuppressLint("Recycle")
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else
            return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File myAppFolder = new File(folder, "Mobile Programming App");

            if (myAppFolder.exists()) {
                pickImageFromGallery();
            } else {
                if (myAppFolder.mkdirs()) {
                    pickImageFromGallery();
                }
            }
        }
        else { Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show(); }
    }

    private void defineListeners() {
        chooseButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
            else { pickImageFromGallery(); }
        });

        signUpButton.setOnClickListener(v -> {
            signUpButton.startAnimation(scaleUp);
            signUpButton.startAnimation(scaleDown);
            if (checkFields()) {
                signUpButton.setClickable(false);
                goLogin.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                String checkUser = databaseHelper.getUserLoginInfo(userUserName);
                if(checkUser.isEmpty()) {
                    hashPassword = hash(userPassword);
                    User user = new User(userUserName, userFullName, userEmail, userPhoneNo, userBirthDate, hashPassword);

                    databaseHelper.addUser(user);
                    savePhoto();
                    Toast.makeText(SignUpActivity.this, "You have successfully signed up!", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    },1000);
                }
                else {
                    username.setError("This username is already exists.");
                    username.requestFocus();
                    signUpButton.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    goLogin.setVisibility(View.VISIBLE);
                }
            }
        });

        goLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Objects.requireNonNull(birthDate.getEditText()).setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this, R.style.MySpinnerDatePickerStyle, (view, year, month, dayOfMonth) -> {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                String strDate = format.format(calendar.getTime());

                birthDate.getEditText().setText(strDate);
                mYear = year; mMonth = month; mDayOfMonth = dayOfMonth;
            }, mYear, mMonth, mDayOfMonth);
            dpd.setCancelable(false);
            dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            dpd.show();
        });

        birthDate.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this, R.style.MySpinnerDatePickerStyle, (view, year, month, dayOfMonth) -> {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    String strDate = format.format(calendar.getTime());

                    birthDate.getEditText().setText(strDate);
                    mYear = year; mMonth = month; mDayOfMonth = dayOfMonth;
                }, mYear, mMonth, mDayOfMonth);
                dpd.setCancelable(false);
                dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                dpd.show();
            }
        });

        Objects.requireNonNull(phoneNo.getEditText()).addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    private void defineVariables() {
        databaseHelper = new DatabaseHelper(SignUpActivity.this);
        username = findViewById(R.id.username);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        phoneNo = findViewById(R.id.phoneNo);
        birthDate = findViewById(R.id.birthDate);
        password = findViewById(R.id.password);
        reEnterPassword = findViewById(R.id.reEnterPassword);
        signUpButton = findViewById(R.id.signUpButton);
        goLogin = findViewById(R.id.goLogin);
        chooseButton = findViewById(R.id.chooseButton);
        profilePicture = findViewById(R.id.profilePicture);
        progressBar = findViewById(R.id.progressBar);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR); year-=18;
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, dayOfMonth);
        mYear = year; mMonth = 0; mDayOfMonth = 1;

        checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
        checkPassword = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private boolean checkFields() {
        boolean validate = true;
        getTextFromFields();

        if(userUserName.isEmpty()) {
            username.setError("Field cannot be empty");
            validate = false;
        }
        else {
            username.setError(null);
            username.setErrorEnabled(false);
        }

        if(userFullName.isEmpty()) {
            fullName.setError("Field cannot be empty");
            validate = false;
        }
        else {
            fullName.setError(null);
            fullName.setErrorEnabled(false);
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

    private void getTextFromFields() {
        userUserName = Objects.requireNonNull(username.getEditText()).getText().toString();
        userFullName = Objects.requireNonNull(fullName.getEditText()).getText().toString();
        userEmail = Objects.requireNonNull(email.getEditText()).getText().toString();
        userPhoneNo = Objects.requireNonNull(phoneNo.getEditText()).getText().toString();
        userBirthDate = Objects.requireNonNull(birthDate.getEditText()).getText().toString();
        userPassword = Objects.requireNonNull(password.getEditText()).getText().toString();
        userReEnterPassword = Objects.requireNonNull(reEnterPassword.getEditText()).getText().toString();
    }

    public String hash(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest)
                hexString.append(Integer.toHexString(0xFF & b));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
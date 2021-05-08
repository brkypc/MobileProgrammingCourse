package tr.edu.yildiz.berkayyapici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class CodeVerificationActivity extends AppCompatActivity {
    ImageView close;
    TextView phoneNumberText;
    PinView pinView;
    Button verifyButton;
    ProgressBar progressBar;
    String phoneNumberFromUser, phoneNumberFinal;
    String codeByFirebase;
    FirebaseAuth mAuth;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        phoneNumberFromUser = intent.getStringExtra("phoneNumber");
        phoneNumberFinal = "+90" + phoneNumberFromUser;
        defineVariables();
        defineListeners();
        sendVerificationCode(phoneNumberFinal);
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeByFirebase = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null) {
                pinView.setText(code);
                verifyCode(code);
                verifyButton.setClickable(false);
            }
            else
                Toast.makeText(CodeVerificationActivity.this, "null code", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(CodeVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeByFirebase, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(CodeVerificationActivity.this, ChangePasswordActivity.class);
                            intent.putExtra("phoneNumber", phoneNumberFromUser);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                verifyButton.setClickable(true);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CodeVerificationActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void defineVariables() {
        phoneNumberText = findViewById(R.id.phoneNumberText);
        phoneNumberText.setText(phoneNumberFinal.substring(0,6) + "*******");
        pinView = findViewById(R.id.pinView);
        verifyButton = findViewById(R.id.verifyButton);
        close = findViewById(R.id.close);
        progressBar = findViewById(R.id.verifyProgressBar);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scaleDown.setStartOffset(100);
    }

    private void defineListeners() {
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyButton.startAnimation(scaleUp);
                verifyButton.startAnimation(scaleDown);
                String userCode = pinView.getText().toString();
                if(!userCode.isEmpty()) {
                    verifyButton.setClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    verifyCode(userCode);
                }
                else {
                    Toast.makeText(CodeVerificationActivity.this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodeVerificationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
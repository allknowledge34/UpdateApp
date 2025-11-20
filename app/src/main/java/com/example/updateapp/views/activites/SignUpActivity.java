package com.example.updateapp.views.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.updateapp.MainActivity;
import com.example.updateapp.R;
import com.example.updateapp.databinding.ActivitySignUpBinding;
import com.example.updateapp.models.UserModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    ImageView googleBtn;
    ImageView emailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        googleBtn = findViewById(R.id.google_btn);
        emailBtn = findViewById(R.id.email_btn);

        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Your Account");
        progressDialog.setMessage("Your Account Is Creating");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.edtName.getText().toString();
                String email = binding.edtEmail.getText().toString();
                String number = binding.edtMobile.getText().toString();
                String password = binding.edtPassword.getText().toString();

                if (name.isEmpty()){
                    binding.edtName.setError("Enter Your Good Name");
                } else if (email.isEmpty()) {
                    binding.edtEmail.setError("Enter Your Valid Email");
                }else if (number.isEmpty()) {
                    binding.edtEmail.setError("Enter Your Valid Mobile Number");
                }else if (password.isEmpty()) {
                    binding.edtPassword.setError("Enter Strong Password");
                }else {

                    progressDialog.show();
                    sendOtpForVerification(name, email, number, password);
                }
            }
        });

        binding.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUpActivity.this,
                        "Click on Google button to login with Google",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.edtName.getText().toString();
                String email = binding.edtEmail.getText().toString();
                String number = binding.edtMobile.getText().toString();
                String password = binding.edtPassword.getText().toString();

                if (name.isEmpty()){
                    binding.edtName.setError("Enter Your Good Name");
                } else if (email.isEmpty()) {
                    binding.edtEmail.setError("Enter Your Valid Email");
                }else if (number.isEmpty()) {
                    binding.edtEmail.setError("Enter Your Valid Mobile Number");
                }else if (password.isEmpty()) {
                    binding.edtPassword.setError("Enter Strong Password");
                }else {

                    progressDialog.show();
                    sendOtpForVerification(name, email, number, password);
                }
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendOtpForVerification(String name, String email, String number, String password) {

        progressDialog.show();

        String phoneNumber = "+91" + number;

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                                progressDialog.dismiss();
                                goToOtpScreen(name, email, number, password, "", true, credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                progressDialog.dismiss();

                                goToOtpScreen(name, email, number, password, verificationId, false, null);
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void goToOtpScreen(String name, String email, String number, String password,
                               String verificationId, boolean autoVerify, PhoneAuthCredential credential) {

        Intent intent = new Intent(SignUpActivity.this, OTPActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("number", number);
        intent.putExtra("password", password);
        intent.putExtra("verificationId", verificationId);
        intent.putExtra("autoVerify", autoVerify);

        if (autoVerify && credential != null) {
            OTPActivity.autoCredential = credential;
        }
        startActivity(intent);
    }

}
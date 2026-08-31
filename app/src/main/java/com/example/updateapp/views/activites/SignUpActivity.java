package com.example.updateapp.views.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.updateapp.R;
import com.example.updateapp.databinding.ActivitySignUpBinding;
import com.example.updateapp.utils.LocaleHelper;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.creating_account));
        progressDialog.setMessage(getString(R.string.account_creating));

        binding.btnSignUp.setOnClickListener(v -> doValidation());

        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        googleBtn.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.google_login_message), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        emailBtn.setOnClickListener(v -> doValidation());
    }

    private void doValidation() {

        String name = binding.edtName.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String number = binding.edtMobile.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if (name.isEmpty()) { binding.edtName.setError(getString(R.string.enter_good_name)); return; }
        if (email.isEmpty()) { binding.edtEmail.setError(getString(R.string.enter_valid_email)); return; }
        if (number.isEmpty()) { binding.edtMobile.setError(getString(R.string.enter_valid_mobile)); return; }
        if (password.isEmpty()) { binding.edtPassword.setError(getString(R.string.enter_strong_password)); return; }

        checkEmailAlreadyExists(name, email, number, password);
    }

    private void checkEmailAlreadyExists(String name, String email, String number, String password) {

        progressDialog.show();

        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        String errorMessage = task.getException() != null ? 
                            task.getException().getMessage() : 
                            getString(R.string.generic_error);
                        Toast.makeText(this, getString(R.string.error_message, errorMessage), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean exists = !task.getResult().getSignInMethods().isEmpty();

                    if (exists) {
                        progressDialog.dismiss();
                        Toast.makeText(this,
                                getString(R.string.email_already_registered),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    sendOtpForVerification(name, email, number, password);
                });
    }

    private void sendOtpForVerification(String name, String email, String number, String password) {

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
package com.example.updateapp.views.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.updateapp.Helpers.LocaleHelper;
import com.example.updateapp.MainActivity;
import com.example.updateapp.databinding.ActivityOtpactivityBinding;
import com.example.updateapp.models.UserModel;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    public static PhoneAuthCredential autoCredential = null;

    String name, email, number, password, verificationId;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setTitle("Verifying OTP");
        dialog.setMessage("Please wait...");

        getDataFromIntent();
        setupOtpInputs();

        binding.tbOtpFragment.setNavigationOnClickListener(v -> {
            finish();
        });

        binding.btnLogin.setOnClickListener(v -> {
            String otp = getOtp();

            if (otp.length() < 6) {
                Toast.makeText(this, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.show();

            if (autoCredential != null) {

                verifyCredential(autoCredential);
            } else {

                PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(verificationId, otp);

                verifyCredential(credential);
            }
        });
    }

    private void getDataFromIntent() {
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        number = getIntent().getStringExtra("number");
        password = getIntent().getStringExtra("password");
        verificationId = getIntent().getStringExtra("verificationId");

        if (number == null) number = "";
        binding.tvUserNumber.setText(number);
    }

    private void setupOtpInputs() {

        EditText[] otp = {
                binding.etOtp1, binding.etOtp2, binding.etOtp3,
                binding.etOtp4, binding.etOtp5, binding.etOtp6
        };

        for (int i = 0; i < otp.length; i++) {
            int index = i;

            otp[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.length() == 1) {
                        if (index < otp.length - 1) {
                            otp[index + 1].requestFocus();
                        }
                    }
                    else if (s.length() == 0) {
                        if (index > 0) {
                            otp[index - 1].requestFocus();
                        }
                    }
                }
            });
        }
    }

    private String getOtp() {
        return binding.etOtp1.getText().toString().trim() +
                binding.etOtp2.getText().toString().trim() +
                binding.etOtp3.getText().toString().trim() +
                binding.etOtp4.getText().toString().trim() +
                binding.etOtp5.getText().toString().trim() +
                binding.etOtp6.getText().toString().trim();
    }

    private void verifyCredential(PhoneAuthCredential credential) {
        if (!dialog.isShowing()) dialog.show();

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        String m = task.getException() != null ? task.getException().getMessage() : "OTP verification failed";
                        Toast.makeText(OTPActivity.this, "OTP Error: " + m, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (auth.getCurrentUser() == null) {
                        dialog.dismiss();
                        Toast.makeText(OTPActivity.this, "Authentication error: user not found after sign-in", Toast.LENGTH_LONG).show();
                        return;
                    }

                    final String uid = auth.getCurrentUser().getUid();

                    if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                        saveUserToFirestoreAndContinue(uid, name, email, number, password);
                        return;
                    }

                    AuthCredential emailCredential = EmailAuthProvider.getCredential(email, password);

                    auth.getCurrentUser().linkWithCredential(emailCredential)
                            .addOnCompleteListener(linkTask -> {

                                if (linkTask.isSuccessful()) {
                                    saveUserToFirestoreAndContinue(uid, name, email, number, password);
                                } else {
                                    dialog.dismiss();

                                    Exception e = linkTask.getException();
                                    String msg = e != null ? e.getMessage() : "Unknown linking error";

                                    if (e instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(OTPActivity.this,
                                                "This email is already registered with a different account. Please login with that email or use another email.",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(OTPActivity.this,
                                                "Failed to link email: " + msg,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                });
    }

    private void saveUserToFirestoreAndContinue(String uid, String name, String email, String number, String password) {

        if (name == null) name = "";
        if (email == null) email = "";
        if (number == null) number = "";
        if (password == null) password = "";

        UserModel user = new UserModel(
                name,
                email,
                number,
                password,
                "https://firebasestorage.googleapis.com/v0/b/earning-b8942.firebasestorage.app/o/account.png?alt=media&token=0ef08dd9-6b13-4da2-a39f-500cff3cf4f0"
        );

        firestore.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(unused -> {
                    if (dialog.isShowing()) dialog.dismiss();
                    startActivity(new Intent(OTPActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    if (dialog.isShowing()) dialog.dismiss();
                    Toast.makeText(OTPActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase));
    }
}
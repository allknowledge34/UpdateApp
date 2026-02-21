package com.example.updateapp.views.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.updateapp.MainActivity;
import com.example.updateapp.R;
import com.example.updateapp.databinding.ActivityLoginBinding;
import com.example.updateapp.utils.LocaleHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleBtn;
    ImageView emailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        googleBtn = findViewById(R.id.google_btn);
        emailBtn = findViewById(R.id.email_btn);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.logging_in));
        progressDialog.setMessage(getString(R.string.please_wait));

        binding.emailBtn.setOnClickListener(v -> performEmailLogin());


        binding.btnLogin.setOnClickListener(v -> performEmailLogin());

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        googleBtn.setOnClickListener(v -> signInWithGoogle());
    }

    private void performEmailLogin() {
        String email = binding.edtEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
        String password = binding.edtPassword.getText().toString();

        if (email.isEmpty()) {
            binding.edtEmail.setError(getString(R.string.enter_valid_email));
        } else if (password.isEmpty()) {
            binding.edtPassword.setError(getString(R.string.enter_strong_password));
        } else {
            progressDialog.show();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Exception e = task.getException();
                        String msg = (e != null) ? e.getLocalizedMessage() : getString(R.string.enter_valid_email);
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    void signInWithGoogle() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                auth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);

                                    if (account != null) {

                                        String name = account.getDisplayName();
                                        String email = account.getEmail();
                                        String profile = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

                                        String uid = FirebaseAuth.getInstance().getUid();

                                        Map<String, Object> user = new HashMap<>();
                                        user.put("name", name);
                                        user.put("email", email);
                                        user.put("profile", profile);

                                        FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .document(uid)
                                                .set(user);
                                    }

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            getString(R.string.firebase_auth_failed),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(
                        this,
                        getString(R.string.google_signin_error, e.getStatusCode(), e.getMessage()),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
package com.example.updateapp.views.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.updateapp.Helpers.LocaleHelper;
import com.example.updateapp.Helpers.SaveState;
import com.example.updateapp.R;
import com.example.updateapp.utils.LocaleHelper;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_splash_screen);

        // Check if language has been selected before
        SaveState languageState = new SaveState(this, "LANGUAGE_SELECTED");
        boolean languageSelected = languageState.getState() == 1;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent;
                        if (!languageSelected) {
                            // Show language selection screen on first launch
                            intent = new Intent(SplashScreenActivity.this, LanguageActivity.class);
                            intent.putExtra("isFirstLaunch", true);
                        } else {
                            // Go to onboarding
                            intent = new Intent(SplashScreenActivity.this, OnboardingActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }, 500);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
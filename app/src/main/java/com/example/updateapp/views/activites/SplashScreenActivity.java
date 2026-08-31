package com.example.updateapp.views.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.updateapp.Helpers.SaveState;
import com.example.updateapp.R;
import com.example.updateapp.utils.LocaleHelper;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_splash_screen);

        SaveState languageState = new SaveState(this, "LANGUAGE_SELECTED");
        boolean languageSelected = languageState.getState() == 1;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    Intent intent;
                    if (!languageSelected) {
                        intent = new Intent(SplashScreenActivity.this, LanguageActivity.class);
                        intent.putExtra("isFirstLaunch", true);
                    } else {
                        intent = new Intent(SplashScreenActivity.this, OnboardingActivity.class);
                    }
                    startActivity(intent);
                    finish();
                });
            }
        }, 500);
    }
}

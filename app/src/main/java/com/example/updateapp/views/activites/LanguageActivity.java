package com.example.updateapp.views.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updateapp.Helpers.LocaleHelper;
import com.example.updateapp.Helpers.SaveState;
import com.example.updateapp.R;
import com.example.updateapp.adapters.LanguageAdapter;
import com.example.updateapp.models.LanguageModel;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LanguageAdapter adapter;
    private List<LanguageModel> languageList;
    private String currentLanguageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        recyclerView = findViewById(R.id.rev_langauge);
        TextView titleText = findViewById(R.id.txt_title);
        ImageView doneButton = findViewById(R.id.img_done);

        // Get current language
        currentLanguageCode = LocaleHelper.getSavedLanguage(this);

        // Setup RecyclerView
        languageList = createLanguageList();
        adapter = new LanguageAdapter(languageList, this::onLanguageSelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Update selection UI
        adapter.updateSelection(currentLanguageCode);

        // Done button click handler
        doneButton.setOnClickListener(v -> {
            // Check if this is first launch
            boolean isFirstLaunch = getIntent().getBooleanExtra("isFirstLaunch", false);
            if (isFirstLaunch) {
                // Mark language as selected and go to onboarding
                SaveState languageState = new SaveState(this, "LANGUAGE_SELECTED");
                languageState.setState(1);
                Intent intent = new Intent(LanguageActivity.this, OnboardingActivity.class);
                startActivity(intent);
            }
            finish();
        });

        // Set title
        titleText.setText(getString(R.string.language));
    }

    private List<LanguageModel> createLanguageList() {
        List<LanguageModel> list = new ArrayList<>();
        String[] languageCodes = {"en", "zh", "hi", "es", "ar", "fr", "bn", "pt", "ru", "ur"};
        String[] languageNames = {
            getString(R.string.language_english),
            getString(R.string.language_chinese),
            getString(R.string.language_hindi),
            getString(R.string.language_spanish),
            getString(R.string.language_arabic),
            getString(R.string.language_french),
            getString(R.string.language_bengali),
            getString(R.string.language_portuguese),
            getString(R.string.language_russian),
            getString(R.string.language_urdu)
        };

        for (int i = 0; i < languageCodes.length; i++) {
            boolean isSelected = languageCodes[i].equals(currentLanguageCode);
            list.add(new LanguageModel(languageCodes[i], languageNames[i], isSelected));
        }

        return list;
    }

    private void onLanguageSelected(LanguageModel language) {
        String selectedLanguageCode = language.getLanguageCode();
        
        if (!selectedLanguageCode.equals(currentLanguageCode)) {
            // Save and apply new language
            LocaleHelper.setLocale(this, selectedLanguageCode);
            
            // Mark language as selected (for first launch)
            SaveState languageState = new SaveState(this, "LANGUAGE_SELECTED");
            languageState.setState(1);
            
            // Update current language
            currentLanguageCode = selectedLanguageCode;
            
            // Update adapter selection
            adapter.updateSelection(selectedLanguageCode);
            
            // Restart activity to apply language changes immediately
            recreate();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase));
    }
}
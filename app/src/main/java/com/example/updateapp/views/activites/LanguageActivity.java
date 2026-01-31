package com.example.updateapp.views.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updateapp.MainActivity;
import com.example.updateapp.R;
import com.example.updateapp.adapters.LanguageAdapter;
import com.example.updateapp.models.LanguageModel;
import com.example.updateapp.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity implements LanguageAdapter.OnLanguageSelectedListener {

    private RecyclerView recyclerView;
    private LanguageAdapter languageAdapter;
    private List<LanguageModel> languageList;
    private ImageView imgDone, imgBack;
    private TextView txtTitle;
    private int selectedPosition = 0;

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

        initViews();
        setupLanguageList();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rev_langauge);
        imgDone = findViewById(R.id.img_done);
        imgBack = findViewById(R.id.img_back);
        txtTitle = findViewById(R.id.txt_title);
        
        // Set title from string resource
        txtTitle.setText(getString(R.string.language));
        
        // Show back button
        imgBack.setVisibility(android.view.View.VISIBLE);
    }

    private void setupLanguageList() {
        languageList = new ArrayList<>();
        
        // Add all 10 supported languages
        languageList.add(new LanguageModel(getString(R.string.lang_english), "en", "US"));
        languageList.add(new LanguageModel(getString(R.string.lang_chinese), "zh", "CN"));
        languageList.add(new LanguageModel(getString(R.string.lang_hindi), "hi", "IN"));
        languageList.add(new LanguageModel(getString(R.string.lang_spanish), "es", "ES"));
        languageList.add(new LanguageModel(getString(R.string.lang_arabic), "ar", "SA"));
        languageList.add(new LanguageModel(getString(R.string.lang_french), "fr", "FR"));
        languageList.add(new LanguageModel(getString(R.string.lang_bengali), "bn", "BD"));
        languageList.add(new LanguageModel(getString(R.string.lang_portuguese), "pt", "PT"));
        languageList.add(new LanguageModel(getString(R.string.lang_russian), "ru", "RU"));
        languageList.add(new LanguageModel(getString(R.string.lang_urdu), "ur", "PK"));

        // Set current language as selected
        String currentLanguage = LocaleHelper.getLanguage(this);
        for (int i = 0; i < languageList.size(); i++) {
            if (languageList.get(i).getLanguageCode().equals(currentLanguage)) {
                languageList.get(i).setSelected(true);
                selectedPosition = i;
                break;
            }
        }
        
        // If no match found, default to English (position 0)
        if (selectedPosition == 0 && !currentLanguage.equals("en")) {
            languageList.get(0).setSelected(true);
            selectedPosition = 0;
        }
    }

    private void setupRecyclerView() {
        languageAdapter = new LanguageAdapter(this, languageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(languageAdapter);
    }

    private void setupClickListeners() {
        imgDone.setOnClickListener(v -> applyLanguageChange());
        
        imgBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onLanguageSelected(LanguageModel language, int position) {
        selectedPosition = position;
        languageAdapter.updateSelection(position);
    }

    private void applyLanguageChange() {
        if (selectedPosition >= 0 && selectedPosition < languageList.size()) {
            LanguageModel selectedLanguage = languageList.get(selectedPosition);
            
            // Apply the language change immediately
            LocaleHelper.setLocale(this, selectedLanguage.getLanguageCode(), selectedLanguage.getCountryCode());
            
            // Show success message
            Toast.makeText(this, getString(R.string.language_changed), Toast.LENGTH_SHORT).show();
            
            // Restart the main activity to apply changes completely
            restartApp();
        }
    }

    private void restartApp() {
        // Create intent to restart the main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        
        // Apply transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
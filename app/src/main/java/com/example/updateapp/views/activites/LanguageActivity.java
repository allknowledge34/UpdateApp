package com.example.updateapp.views.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updateapp.Helpers.SaveState;
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
    private ImageView imgDone;
    private ImageView imgBack;
    private TextView txtTitle;
    private TextView txtSubtitle;
    private int selectedPosition = 0;
    private boolean isFirstLaunch;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

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

        isFirstLaunch = getIntent().getBooleanExtra("isFirstLaunch", false);

        initViews();
        setupLanguageList();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rev_language);
        imgDone = findViewById(R.id.img_done);
        imgBack = findViewById(R.id.img_back);
        txtTitle = findViewById(R.id.txt_title);
        txtSubtitle = findViewById(R.id.txt_subtitle);

        txtTitle.setText(getString(R.string.select_language));
        if (txtSubtitle != null) {
            txtSubtitle.setText(getString(R.string.choose_language));
        }

        imgDone.setContentDescription(getString(R.string.done));
        imgBack.setContentDescription(getString(R.string.back));
        imgBack.setVisibility(isFirstLaunch ? View.GONE : View.VISIBLE);
    }

    private void setupLanguageList() {
        languageList = new ArrayList<>();
        languageList.add(new LanguageModel(getString(R.string.lang_english), "en", ""));
        languageList.add(new LanguageModel(getString(R.string.lang_chinese), "zh", "CN"));
        languageList.add(new LanguageModel(getString(R.string.lang_hindi), "hi", "IN"));
        languageList.add(new LanguageModel(getString(R.string.lang_spanish), "es", "ES"));
        languageList.add(new LanguageModel(getString(R.string.lang_arabic), "ar", "SA"));
        languageList.add(new LanguageModel(getString(R.string.lang_french), "fr", "FR"));
        languageList.add(new LanguageModel(getString(R.string.lang_bengali), "bn", "BD"));
        languageList.add(new LanguageModel(getString(R.string.lang_portuguese), "pt", "PT"));
        languageList.add(new LanguageModel(getString(R.string.lang_russian), "ru", "RU"));
        languageList.add(new LanguageModel(getString(R.string.lang_urdu), "ur", "PK"));

        String currentLanguage = LocaleHelper.getLanguage(this);
        selectedPosition = 0;
        for (int i = 0; i < languageList.size(); i++) {
            boolean match = languageList.get(i).getLanguageCode().equalsIgnoreCase(currentLanguage);
            languageList.get(i).setSelected(match);
            if (match) {
                selectedPosition = i;
            }
        }
        if (!languageList.get(selectedPosition).isSelected()) {
            languageList.get(0).setSelected(true);
            selectedPosition = 0;
        }
    }

    private void setupRecyclerView() {
        languageAdapter = new LanguageAdapter(this, languageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(languageAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setupClickListeners() {
        imgDone.setOnClickListener(v -> applyLanguageChange());
        imgBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    public void onLanguageSelected(LanguageModel language, int position) {
        selectedPosition = position;
        languageAdapter.updateSelection(position);
    }

    private void applyLanguageChange() {
        LanguageModel selectedLanguage = languageList.get(selectedPosition);
        LocaleHelper.setLocale(
                this,
                selectedLanguage.getLanguageCode(),
                selectedLanguage.getCountryCode()
        );

        new SaveState(this, "LANGUAGE_SELECTED").setState(1);
        Toast.makeText(this, getString(R.string.language_changed), Toast.LENGTH_SHORT).show();
        restartApp();
    }

    private void restartApp() {
        Intent intent = isFirstLaunch
                ? new Intent(this, OnboardingActivity.class)
                : new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

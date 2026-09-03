package com.example.updateapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private static final String SELECTED_COUNTRY = "Locale.Helper.Selected.Country";
    private static final String PREFS_NAME = "app_prefs";

    public static Context onAttach(Context context) {
        return onAttach(context, "en");
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        String country = getPersistedCountry(context, "");
        return wrap(context, lang, country);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, "en");
    }

    public static String getCountry(Context context) {
        return getPersistedCountry(context, "");
    }

    public static void applyPersistedLocales(Context context) {
        applyAppLocales(getLanguage(context), getCountry(context));
    }

    public static Context setLocale(Context context, String language, String country) {
        if (language == null || language.isEmpty()) {
            language = "en";
        }
        if (country == null || "en".equals(language)) {
            country = "";
        }
        persist(context, language, country);
        applyAppLocales(language, country);
        return wrap(context, language, country);
    }

    private static void applyAppLocales(String language, String country) {
        String tag = (country == null || country.isEmpty())
                ? language
                : language + "-" + country;
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag));
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static String getPersistedCountry(Context context, String defaultCountry) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(SELECTED_COUNTRY, defaultCountry);
    }

    private static void persist(Context context, String language, String country) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.putString(SELECTED_COUNTRY, country == null ? "" : country);
        editor.apply();
    }

    private static Context wrap(Context context, String language, String country) {
        Locale locale;
        if (country != null && !country.isEmpty()) {
            locale = new Locale(language, country);
        } else {
            locale = new Locale(language);
        }
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }
}

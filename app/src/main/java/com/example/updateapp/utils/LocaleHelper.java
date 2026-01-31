package com.example.updateapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private static final String SELECTED_COUNTRY = "Locale.Helper.Selected.Country";
    private static final String PREFS_NAME = "app_prefs";

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, "en");
        String country = getPersistedCountry(context, "");
        return setLocale(context, lang, country);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        String country = getPersistedCountry(context, "");
        return setLocale(context, lang, country);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, "en");
    }

    public static Context setLocale(Context context, String language, String country) {
        persist(context, language, country);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language, country);
        }

        return updateResourcesLegacy(context, language, country);
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
        editor.putString(SELECTED_COUNTRY, country);
        editor.apply();
    }

    private static Context updateResources(Context context, String language, String country) {
        Locale locale;
        if (country != null && !country.isEmpty()) {
            locale = new Locale(language, country);
        } else {
            locale = new Locale(language);
        }
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    private static Context updateResourcesLegacy(Context context, String language, String country) {
        Locale locale;
        if (country != null && !country.isEmpty()) {
            locale = new Locale(language, country);
        } else {
            locale = new Locale(language);
        }
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public static void clearLanguagePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(SELECTED_LANGUAGE);
        editor.remove(SELECTED_COUNTRY);
        editor.apply();
    }
}
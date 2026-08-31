package com.example.updateapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

/**
 * Single source of truth for the in-app language.
 * English is stored and applied as {@code en} with no country, so Android falls
 * back to {@code values/} instead of hunting for {@code values-en-rUS}.
 */
public final class LocaleHelper {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LANGUAGE = "Locale.Helper.Selected.Language";
    private static final String KEY_COUNTRY = "Locale.Helper.Selected.Country";
    public static final String DEFAULT_LANGUAGE = "en";

    private LocaleHelper() {}

    public static Context onAttach(Context context) {
        return onAttach(context, DEFAULT_LANGUAGE);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getLanguage(context, defaultLanguage);
        String country = getCountry(context);
        return updateResources(context, lang, country);
    }

    public static String getLanguage(Context context) {
        return getLanguage(context, DEFAULT_LANGUAGE);
    }

    public static String getLanguage(Context context, String defaultLanguage) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lang = preferences.getString(KEY_LANGUAGE, defaultLanguage);
        if (lang == null || lang.isEmpty()) {
            return defaultLanguage;
        }
        return lang;
    }

    public static String getCountry(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String country = preferences.getString(KEY_COUNTRY, "");
        return country == null ? "" : country;
    }

    public static boolean isEnglish(String language) {
        return language == null || language.isEmpty() || language.equalsIgnoreCase(DEFAULT_LANGUAGE);
    }

    public static Context setLocale(Context context, String language, String country) {
        String lang = isEnglish(language) ? DEFAULT_LANGUAGE : language;
        String region = isEnglish(lang) ? "" : (country == null ? "" : country);
        persist(context, lang, region);
        applyAppLocales(lang, region);
        Context app = context.getApplicationContext();
        if (app != null && app != context) {
            updateResources(app, lang, region);
        }
        return updateResources(context, lang, region);
    }

    public static Locale toLocale(String language, String country) {
        if (isEnglish(language)) {
            return Locale.ENGLISH;
        }
        if (country != null && !country.isEmpty()) {
            return new Locale(language, country);
        }
        return new Locale(language);
    }

    private static void persist(Context context, String language, String country) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LANGUAGE, language);
        editor.putString(KEY_COUNTRY, country == null ? "" : country);
        editor.apply();
    }

    private static void applyAppLocales(String language, String country) {
        String tag = isEnglish(language)
                ? "en"
                : (country != null && !country.isEmpty() ? language + "-" + country : language);
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag));
    }

    private static Context updateResources(Context context, String language, String country) {
        Locale locale = toLocale(language, country);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            configuration.setLocales(new LocaleList(locale));
            configuration.setLayoutDirection(locale);
            return context.createConfigurationContext(configuration);
        }

        configuration.locale = locale;
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}

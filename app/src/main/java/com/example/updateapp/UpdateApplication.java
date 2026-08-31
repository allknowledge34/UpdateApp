package com.example.updateapp;

import android.app.Application;
import android.content.Context;

import com.example.updateapp.utils.LocaleHelper;

public class UpdateApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocaleHelper.setLocale(
                this,
                LocaleHelper.getLanguage(this),
                LocaleHelper.getCountry(this)
        );
    }
}
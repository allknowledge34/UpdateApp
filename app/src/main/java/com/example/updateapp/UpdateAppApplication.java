package com.example.updateapp;

import android.app.Application;
import android.content.Context;

import com.example.updateapp.Helpers.LocaleHelper;

public class UpdateAppApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(base));
    }
}

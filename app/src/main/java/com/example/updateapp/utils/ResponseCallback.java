package com.example.updateapp.utils;

public interface ResponseCallback {

    void onResponse (String response);

    void onError (Throwable throwable);
}

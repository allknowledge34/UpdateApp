package com.example.updateapp.models;

public class LanguageModel {
    private String languageName;
    private String languageCode;
    private String countryCode;
    private boolean isSelected;

    public LanguageModel(String languageName, String languageCode, String countryCode) {
        this.languageName = languageName;
        this.languageCode = languageCode;
        this.countryCode = countryCode;
        this.isSelected = false;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

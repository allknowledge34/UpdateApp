package com.example.updateapp.models;

public class LanguageModel {
    private String languageCode;
    private String languageName;
    private boolean isSelected;

    public LanguageModel(String languageCode, String languageName, boolean isSelected) {
        this.languageCode = languageCode;
        this.languageName = languageName;
        this.isSelected = isSelected;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

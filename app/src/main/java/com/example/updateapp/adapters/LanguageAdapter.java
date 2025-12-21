package com.example.updateapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updateapp.R;
import com.example.updateapp.models.LanguageModel;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private List<LanguageModel> languageList;
    private OnLanguageClickListener listener;

    public interface OnLanguageClickListener {
        void onLanguageClick(LanguageModel language);
    }

    public LanguageAdapter(List<LanguageModel> languageList, OnLanguageClickListener listener) {
        this.languageList = languageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LanguageModel language = languageList.get(position);
        holder.languageName.setText(language.getLanguageName());
        holder.selectedIcon.setVisibility(language.isSelected() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLanguageClick(language);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    public void updateSelection(String selectedLanguageCode) {
        for (LanguageModel language : languageList) {
            language.setSelected(language.getLanguageCode().equals(selectedLanguageCode));
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView languageName;
        ImageView selectedIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            languageName = itemView.findViewById(R.id.txt_language_name);
            selectedIcon = itemView.findViewById(R.id.img_selected);
        }
    }
}

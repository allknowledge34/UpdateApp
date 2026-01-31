package com.example.updateapp.adapters;

import android.content.Context;
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

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private Context context;
    private List<LanguageModel> languageList;
    private OnLanguageSelectedListener listener;

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(LanguageModel language, int position);
    }

    public LanguageAdapter(Context context, List<LanguageModel> languageList, OnLanguageSelectedListener listener) {
        this.context = context;
        this.languageList = languageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        LanguageModel language = languageList.get(position);
        
        holder.txtLanguageName.setText(language.getLanguageName());
        
        if (language.isSelected()) {
            holder.imgSelected.setVisibility(View.VISIBLE);
        } else {
            holder.imgSelected.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLanguageSelected(language, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    public void updateSelection(int selectedPosition) {
        for (int i = 0; i < languageList.size(); i++) {
            languageList.get(i).setSelected(i == selectedPosition);
        }
        notifyDataSetChanged();
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        TextView txtLanguageName;
        ImageView imgSelected;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLanguageName = itemView.findViewById(R.id.txt_language_name);
            imgSelected = itemView.findViewById(R.id.img_selected);
        }
    }
}

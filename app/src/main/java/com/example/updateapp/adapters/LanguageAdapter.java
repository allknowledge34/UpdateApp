package com.example.updateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updateapp.R;
import com.example.updateapp.models.LanguageModel;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private final Context context;
    private final List<LanguageModel> languageList;
    private final OnLanguageSelectedListener listener;

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

        boolean selected = language.isSelected();
        holder.itemView.setBackgroundResource(
                selected ? R.drawable.bg_language_item_selected : R.drawable.bg_language_item
        );
        holder.imgSelected.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        holder.imgSelected.setContentDescription(
                selected ? context.getString(R.string.selected_language) : null
        );
        holder.txtLanguageName.setTextColor(
                ContextCompat.getColor(context, selected ? R.color.themeColor : R.color.black)
        );
        holder.itemView.setSelected(selected);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLanguageSelected(language, holder.getBindingAdapterPosition());
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

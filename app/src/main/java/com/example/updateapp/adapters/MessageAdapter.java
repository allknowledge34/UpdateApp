package com.example.updateapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.updateapp.R;
import com.example.updateapp.models.MessageModel;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {

    List<MessageModel> modelList;

    public MessageAdapter(List<MessageModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        MessageModel model = modelList.get(position);

        if (model.getSentBy().equals(MessageModel.SENT_BY_ME)){

            holder.leftChat.setVisibility(View.GONE);
            holder.rightChat.setVisibility(View.VISIBLE);
            holder.rightText.setText(model.getMessage());
        }
        else {
            holder.rightChat.setVisibility(View.GONE);
            holder.leftChat.setVisibility(View.VISIBLE);
            holder.leftText.setText(model.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout leftChat, rightChat;
        TextView leftText, rightText;
        public viewHolder(@NonNull View itemView){
            super(itemView);

            leftChat = itemView.findViewById(R.id.left_chat);
            rightChat = itemView.findViewById(R.id.right_chat);
            leftText = itemView.findViewById(R.id.eft_txt);
            rightText = itemView.findViewById(R.id.right_txt);
        }
    }
}

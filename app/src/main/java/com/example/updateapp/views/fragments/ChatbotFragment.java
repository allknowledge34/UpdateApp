package com.example.updateapp.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.updateapp.adapters.MessageAdapter;
import com.example.updateapp.databinding.FragmentChatbotBinding;
import com.example.updateapp.models.MessageModel;
import com.example.updateapp.utils.GeminiResp;
import com.example.updateapp.utils.ResponseCallback;
import com.google.ai.client.generativeai.java.ChatFutures;

import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {

    FragmentChatbotBinding binding;
    private EditText messageEt;
    private ImageView sendBtn;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<MessageModel> messageList = new ArrayList<>();
    private ChatFutures chatModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        messageEt = binding.message;
        sendBtn = binding.send;
        recyclerView = binding.recyclerView;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        GeminiResp geminiResp = new GeminiResp();
        chatModel = geminiResp.getModel().startChat();

        binding.mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        binding.file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        sendBtn.setOnClickListener(v -> {
            String query = messageEt.getText().toString().trim();
            if (!query.isEmpty()) {
                addToChat(query, MessageModel.SENT_BY_ME);
                messageEt.setText("");


                GeminiResp.getResponse(chatModel, query, new ResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                addToChat(response, MessageModel.SENT_BY_BOT);
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                addToChat("Error: " + throwable.getMessage(), MessageModel.SENT_BY_BOT);
                            });
                        }
                    }
                });
            }
            else {
                Toast.makeText(getContext(), "write something", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    private void addToChat(String message, String sentBy) {
        requireActivity().runOnUiThread(() -> {
            messageList.add(new MessageModel(message, sentBy));
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        });
    }
}
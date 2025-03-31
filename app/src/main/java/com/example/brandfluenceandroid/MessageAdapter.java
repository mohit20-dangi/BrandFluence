package com.example.brandfluenceandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<com.example.brandfluenceandroid.Message> messageList;  // Adjusted import path

    // Constructor to initialize the message list
    public MessageAdapter(List<com.example.brandfluenceandroid.Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each chat item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item_1, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        // Retrieve and bind message data
        com.example.brandfluenceandroid.Message message = messageList.get(position);
        holder.textMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Inner ViewHolder class to bind views for each message item
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);  // Link the TextView from XML
        }
    }
}

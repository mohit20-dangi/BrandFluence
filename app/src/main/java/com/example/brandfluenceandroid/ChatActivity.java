package com.example.brandfluenceandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference chatDatabase;
    private List<com.example.brandfluenceandroid.Message> messageList;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase Database
        chatDatabase = FirebaseDatabase.getInstance().getReference("chats");

        RecyclerView recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        @SuppressLint("WrongViewCast") ImageButton buttonSend = findViewById(R.id.buttonSend);

        // Initialize message list and adapter

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Fetch messages from Firebase in real-time
        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    com.example.brandfluenceandroid.Message message = data.getValue(com.example.brandfluenceandroid.Message.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });

        // Send message when button is clicked
        buttonSend.setOnClickListener(view -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // Create a unique message ID and store the message in Firebase
                String messageId = chatDatabase.push().getKey();
                HashMap<String, Object> messageData = new HashMap<>();
                messageData.put("text", messageText);
                messageData.put("sender", "Influencer");  // This could be dynamic based on user role
                messageData.put("timestamp", System.currentTimeMillis());

                assert messageId != null;
                chatDatabase.child(messageId).setValue(messageData);
                editTextMessage.setText("");  // Clear input field
            }
        });
    }
}

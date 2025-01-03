package com.example.brainpath.ui.interaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText messageInput;
    private ImageButton sendButton;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_chat);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the chatId from the previous activity (via Intent)
        chatId = getIntent().getStringExtra("chatId");

        // Log the received chatId for debugging purposes
        Log.d("ChatActivity", "Received chatId: " + chatId);

        // Check if the chatId is null
        if (chatId == null) {
            Toast.makeText(this, "Error: chatId is null", Toast.LENGTH_SHORT).show();
            return;  // If chatId is null, return early to avoid further errors
        }

        // Initialize RecyclerView
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Bind the message input and send button
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Real-time listener to fetch messages
        db.collection("chats")
                .document(chatId) // Using the chatId to get specific chat messages
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error loading messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    chatMessages.clear(); // Clear the list before adding new messages
                    for (DocumentSnapshot doc : snapshot) {
                        ChatMessage message = doc.toObject(ChatMessage.class);
                        chatMessages.add(message);
                    }
                    chatAdapter.setMessages(chatMessages);
                });

        // Send message on button click
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageInput.setText(""); // Clear the input after sending
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String messageText) {
        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a new message object
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", currentUserId);
        messageData.put("message", messageText);
        messageData.put("timestamp", Timestamp.now()); // Using Timestamp.now()

        // Add the message to the Firestore collection
        db.collection("chats")
                .document(chatId)  // Ensure the correct chatId is used
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

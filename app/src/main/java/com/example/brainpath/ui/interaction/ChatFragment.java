package com.example.brainpath.ui.interaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class ChatFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText messageInput;
    private ImageButton sendButton;
    private String chatId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.activity_interaction_chat, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the chatId from arguments
        Bundle args = getArguments();
        if (args != null) {
            chatId = args.getString("chatId");
        }

        // Log the received chatId for debugging purposes
        Log.d("ChatFragment", "Received chatId: " + chatId);

        if (chatId == null) {
            Toast.makeText(requireContext(), "Error: chatId is null", Toast.LENGTH_SHORT).show();
            return view;  // Early exit if chatId is null
        }

        // Initialize RecyclerView
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(requireContext(), chatMessages);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Bind the message input and send button
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        // Set up real-time listener to fetch messages
        setupMessageListener();

        // Send message on button click
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageInput.setText(""); // Clear the input after sending
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setupMessageListener() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(requireContext(), "Error loading messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    chatMessages.clear(); // Clear the list before adding new messages
                    for (DocumentSnapshot doc : snapshot) {
                        ChatMessage message = doc.toObject(ChatMessage.class);
                        chatMessages.add(message);
                    }
                    chatAdapter.setMessages(chatMessages);
                });
    }

    private void sendMessage(String messageText) {
        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a new message object
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", currentUserId);
        messageData.put("message", messageText);
        messageData.put("timestamp", Timestamp.now());

        // Add the message to the Firestore collection
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

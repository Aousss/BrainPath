package com.example.brainpath.ui.interaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    private ImageView profileImageView;
    private TextView usernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.activity_interaction_chat, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get references to UI elements
        profileImageView = view.findViewById(R.id.profilePic);
        usernameTextView = view.findViewById(R.id.usernamePic);

        // Get the chatId from arguments
        Bundle args = getArguments();

        if (args != null) {
            Friend friend = args.getParcelable("friend");
            chatId = args.getString("chatId");

            if (friend != null) {
                // Set the username
                usernameTextView.setText(friend.getUsername());

                // Load the profile image using Glide
                Glide.with(requireContext())
                        .load(friend.getProfile())
                        .placeholder(R.drawable.ic_profile_placeholder) // Placeholder
                        .error(R.drawable.ic_profile_placeholder) // Fallback image
                        .circleCrop() // Crop to a circle
                        .into(profileImageView);
            }
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

                    if (snapshot != null && !snapshot.isEmpty()) {
                        chatMessages.clear(); // Clear existing messages
                        for (DocumentSnapshot doc : snapshot) {
                            ChatMessage message = doc.toObject(ChatMessage.class);
                            chatMessages.add(message);
                        }
                        chatAdapter.setMessages(chatMessages);
                    } else {
                        // Handle empty chat gracefully
                        Toast.makeText(requireContext(), "No messages yet. Start the conversation!", Toast.LENGTH_SHORT).show();
                        chatMessages.clear();
                        chatAdapter.setMessages(chatMessages);
                    }
                });
    }


    private void sendMessage(String messageText) {
        sendButton.setEnabled(false); // Disable send button
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", currentUserId);
        messageData.put("message", messageText);
        messageData.put("timestamp", Timestamp.now());

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(true); // Re-enable send button
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(true); // Re-enable send button
                });
    }

}

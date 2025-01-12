package com.example.brainpath.ui.interaction;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    private String senderId;
    private String message;
    private Timestamp timestamp;
    private boolean isRead;

    // No-argument constructor required by Firestore
    public ChatMessage() {}

    // Constructor with parameters
    public ChatMessage(String senderId, String message, Timestamp timestamp, boolean isRead) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters
    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Method to fetch data from Firestore
    public static void fetchMessagesFromFirestore(String chatId, FirebaseFirestore db, final ChatMessagesCallback callback) {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ChatMessage> messages = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Convert Firestore document to ChatMessage
                        ChatMessage chatMessage = documentSnapshot.toObject(ChatMessage.class);
                        if (chatMessage != null) {
                            messages.add(chatMessage);
                        }
                    }
                    callback.onMessagesFetched(messages); // Callback when data is fetched
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage()); // Callback on error
                });
    }

    // Callback interface for handling fetched messages
    public interface ChatMessagesCallback {
        void onMessagesFetched(List<ChatMessage> messages);
        void onError(String errorMessage);
    }

    public boolean isRead() {
        return isRead;
    }
}

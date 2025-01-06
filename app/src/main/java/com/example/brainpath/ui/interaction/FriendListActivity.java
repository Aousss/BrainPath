package com.example.brainpath.ui.interaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendsList;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = findViewById(R.id.chatRecyclerView);
        friendsList = new ArrayList<>();

        // Pass the OnItemClickListener to the adapter
        friendAdapter = new FriendAdapter(this, friendsList, friend -> {
            // Handle item click
            Log.d("FriendListActivity", "Friend clicked: " + friend.getUserId());  // Log the chatId

            // Open the chat with the selected friend
            Intent intent = new Intent(FriendListActivity.this, ChatActivity.class);
            intent.putExtra("chatId", friend.getUserId());  // Pass the userId (chat ID)
            startActivity(intent);
        });

        recyclerView.setAdapter(friendAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch friends for the current logged-in user
        fetchFriends();
    }

    private void fetchFriends() {
        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the current user's document
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Fetch friendIds for the current user
                    List<String> friendIds = (List<String>) documentSnapshot.get("friendIds");

                    if (friendIds != null && !friendIds.isEmpty()) {
                        // Fetch details of each friend
                        for (String friendId : friendIds) {
                            db.collection("users")
                                    .document(friendId)
                                    .get()
                                    .addOnSuccessListener(friendDocument -> {
                                        // Fetch friend data (username, userId)
                                        String friendUsername = friendDocument.getString("username");  // Changed to username
                                        String friendUserId = friendDocument.getId();

                                        // Create a new Friend object
                                        Friend friend = new Friend(friendUsername, friendUserId);

                                        // Add friend to the list and notify the adapter
                                        friendsList.add(friend);
                                        friendAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle error when fetching friend data
                                        Log.e("FriendListActivity", "Error fetching friend: " + e.getMessage());
                                    });
                        }
                    } else {
                        Toast.makeText(FriendListActivity.this, "No friends found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error when fetching the current user's data
                    Toast.makeText(FriendListActivity.this, "Error fetching friends: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}

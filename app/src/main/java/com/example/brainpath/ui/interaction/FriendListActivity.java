package com.example.brainpath.ui.interaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brainpath.R;
import com.example.brainpath.ui.profile.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the friends list and adapter locally
        List<Friend> friendsList = new ArrayList<>();
        friendAdapter = new FriendAdapter(this, friendsList, friend -> {
            // Handle friend item click: start a chat
            Intent intent = new Intent(FriendListActivity.this, ChatActivity.class);
            intent.putExtra("friendId", friend.getUserId());  // Pass the friend ID to ChatActivity
            startActivity(intent);
        });
        recyclerView.setAdapter(friendAdapter);

        // Fetch and display the user's friends
        fetchFriends(friendsList);
    }

    private void fetchFriends(List<Friend> friendsList) {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(FriendListActivity.this, SignInActivity.class));
            finish();
            return;
        }

        String currentUserId = mAuth.getCurrentUser().getUid();
        firestore.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Safely cast the friendIds list
                        List<String> friendIds = (List<String>) documentSnapshot.get("friendIds");
                        if (friendIds != null && !friendIds.isEmpty()) {
                            for (String friendId : friendIds) {
                                // Fetch details of each friend
                                fetchFriendDetails(friendId, friendsList);
                            }
                        } else {
                            Toast.makeText(this, "No friends found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FriendListActivity", "Error fetching user data", e);
                    Toast.makeText(this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchFriendDetails(String friendId, List<Friend> friendsList) {
        firestore.collection("users").document(friendId)
                .get()
                .addOnSuccessListener(friendDocument -> {
                    if (friendDocument.exists()) {
                        String friendUsername = friendDocument.getString("username");
                        String friendUserId = friendDocument.getId();
                        if (friendUsername != null) {
                            Friend friend = new Friend(friendUsername, friendUserId);
                            friendsList.add(friend);

                            // Update RecyclerView efficiently
                            friendAdapter.notifyItemInserted(friendsList.size() - 1);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FriendListActivity", "Error fetching friend data", e);
                    Toast.makeText(this, "Error fetching friend data.", Toast.LENGTH_SHORT).show();
                });
    }
}


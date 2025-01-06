package com.example.brainpath.ui.interaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendsList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_interaction_friend, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.friend_recycler_view);
        friendsList = new ArrayList<>();

        // Pass the OnItemClickListener to the adapter
        friendAdapter = new FriendAdapter(requireContext(), friendsList, friend -> {
            // Handle item click
            Log.d("FriendListFragment", "Friend clicked: " + friend.getUserId()); // Log the chatId

            // Implement navigation to the chat screen (use NavController or callback to activity)
            Toast.makeText(requireContext(), "Clicked on: " + friend.getUsername(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(friendAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Fetch friends for the current logged-in user
        fetchFriends();

        return view;
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
                                        // Fetch friend data (name, userId)
                                        String friendName = friendDocument.getString("name");
                                        String friendUserId = friendDocument.getId();

                                        // Create a new Friend object
                                        Friend friend = new Friend(friendName, friendUserId);

                                        // Add friend to the list and notify the adapter
                                        friendsList.add(friend);
                                        friendAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle error when fetching friend data
                                        Log.e("FriendListFragment", "Error fetching friend: " + e.getMessage());
                                    });
                        }
                    } else {
                        Toast.makeText(requireContext(), "No friends found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error when fetching the current user's data
                    Toast.makeText(requireContext(), "Error fetching friends: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

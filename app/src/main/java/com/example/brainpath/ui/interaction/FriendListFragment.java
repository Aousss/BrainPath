package com.example.brainpath.ui.interaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendsList;  // List<Friend> instead of List<String>
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
        friendsList = new ArrayList<>();  // Initialize friends list with Friend objects

        // Initialize the adapter with the friends list (List<Friend>)
        friendAdapter = new FriendAdapter(requireContext(), friendsList, friend -> {
            // Handle friend item click
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String friendId = friend.getUserId();

            // Create a chatId by combining the current user's ID and friend's ID (order doesn't matter)
            String chatId = currentUserId.compareTo(friendId) < 0 ?
                    currentUserId + "_" + friendId :
                    friendId + "_" + currentUserId;

            // Log the chatId for debugging
            Log.d("FriendListFragment", "Generated chatId: " + chatId);

            // Create a Bundle to pass the chatId to the ChatFragment
            Bundle bundle = new Bundle();
            bundle.putString("chatId", chatId);

            // Navigate to ChatFragment with the chatId
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_friendListFragment_to_chatFragment, bundle);
        });

        recyclerView.setAdapter(friendAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Fetch friends for the current logged-in user
        fetchFriends();

        // Add navigation to InteractionAddFragment
        ImageButton addButton = view.findViewById(R.id.addInteractionButton);
        addButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_friendListFragment_to_interactionAddFragment);
        });

        // Setup FloatingActionButton
        FloatingActionButton gotoForumButton = view.findViewById(R.id.gotoForum);
        gotoForumButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_friendListFragment_to_forumFragment);
        });

        return view;
    }

    private void fetchFriends() {
        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the current user's document to retrieve friendIds
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> friendIds = (List<String>) documentSnapshot.get("friendIds");

                        if (friendIds != null && !friendIds.isEmpty()) {
                            Log.d("FriendListFragment", "Friend IDs: " + friendIds);
                            fetchFriendDetails(friendIds); // Fetch details for each friend ID
                        } else {
                            Log.d("FriendListFragment", "No friends found.");
                            friendsList.clear();  // Make sure to clear any previous list data
                            friendAdapter.notifyDataSetChanged(); // Update UI to show empty state
                        }
                    } else {
                        Log.e("FriendListFragment", "User document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FriendListFragment", "Error fetching user data: " + e.getMessage());
                    Toast.makeText(requireContext(), "Error fetching friends.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchFriendDetails(List<String> friendIds) {
        friendsList.clear(); // Clear the list to avoid duplication

        for (String friendId : friendIds) {
            db.collection("users")
                    .document(friendId)
                    .get()
                    .addOnSuccessListener(friendDocument -> {
                        if (friendDocument.exists()) {
                            // Extract friend's username
                            String friendUsername = friendDocument.getString("username");
                            if (friendUsername == null || friendUsername.isEmpty()) {
                                friendUsername = "Unknown Friend"; // Fallback if no username is found
                            }

                            // Log the friend's username
                            Log.d("FriendListFragment", "Friend username: " + friendUsername);

                            // Create a Friend object with the retrieved username and userId
                            Friend friend = new Friend(friendUsername, friendId);
                            friendsList.add(friend);

                            // Notify the adapter after adding a new friend
                            friendAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("FriendListFragment", "Friend document does not exist for ID: " + friendId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FriendListFragment", "Error fetching friend data: " + e.getMessage());
                    });
        }
    }
}


package com.example.brainpath.ui.interaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.brainpath.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendsList;
    private FirebaseFirestore db;
    private EditText searchFriendField;
    private ImageButton searchButton;



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

        // Initialize the adapter with the friends list
        friendAdapter = new FriendAdapter(requireContext(), friendsList, friend -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String friendId = friend.getUserId();

            // Create a chatId
            String chatId = currentUserId.compareTo(friendId) < 0 ?
                    currentUserId + "_" + friendId :
                    friendId + "_" + currentUserId;

            // Log the chatId for debugging
            Log.d("FriendListFragment", "Generated chatId: " + chatId);

            // Pass the chatId to ChatFragment
            Bundle bundle = new Bundle();
            bundle.putString("chatId", chatId);

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_friendListFragment_to_chatFragment, bundle);
        });

        recyclerView.setAdapter(friendAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Fetch friends for the current logged-in user
        fetchFriends();

        // Set up Add Friend button
        ImageButton addButton = view.findViewById(R.id.addInteractionButton);
        addButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_friendListFragment_to_interactionAddFragment);
        });

        // Set up FloatingActionButton for Forum navigation
        FloatingActionButton gotoForumButton = view.findViewById(R.id.gotoForum);
        gotoForumButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_friendListFragment_to_forumFragment);
        });

        // Initialize Search Components
        searchFriendField = view.findViewById(R.id.search_friend);
        searchButton = view.findViewById(R.id.searchButton);

        // Set up Search Button Click Listener
        searchButton.setOnClickListener(v -> {
            String query = searchFriendField.getText().toString().trim();
            if (!query.isEmpty()) {
                filterFriendsByUsername(query);
            } else {
                // Show the full list if the search field is empty
                friendAdapter.updateFriendsList(friendsList);
            }
        });

        return view;
    }

    private void fetchFriends() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the current user's friend IDs
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> friendIds = (List<String>) documentSnapshot.get("friendIds");

                        if (friendIds != null && !friendIds.isEmpty()) {
                            Log.d("FriendListFragment", "Friend IDs: " + friendIds);
                            fetchFriendDetails(friendIds);
                        } else {
                            Log.d("FriendListFragment", "No friends found.");
                            friendsList.clear();
                            friendAdapter.notifyDataSetChanged();
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
        friendsList.clear();

        for (String friendId : friendIds) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Create the chat ID
            String chatId = currentUserId.compareTo(friendId) < 0 ?
                    currentUserId + "_" + friendId :
                    friendId + "_" + currentUserId;

            // Fetch the latest message for this chat
            db.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        String lastMessage;
                        String lastSeen;

                        if (!querySnapshot.isEmpty()) {
                            ChatMessage latestMessage = querySnapshot.getDocuments().get(0).toObject(ChatMessage.class);
                            if (latestMessage != null) {
                                lastMessage = latestMessage.getMessage();

                                if (latestMessage.getTimestamp() != null) {
                                    Date timestampDate = latestMessage.getTimestamp().toDate();
                                    lastSeen = DateTimeUtils.formatTimestamp(timestampDate); // Format the timestamp
                                } else {
                                    lastSeen = ""; // Fallback if the timestamp is null
                                }

                            } else {
                                lastSeen = "";
                                lastMessage = "No recent messages";
                            }
                        } else {
                            lastSeen = "";
                            lastMessage = "No recent messages";
                        }

                        // Fetch friend's username and profile
                        db.collection("users")
                                .document(friendId)
                                .get()
                                .addOnSuccessListener(friendDocument -> {
                                    if (friendDocument.exists()) {
                                        String username = friendDocument.getString("username");
                                        String profile = friendDocument.getString("profileImageUrl");

                                        Friend friend = new Friend(
                                                username != null ? username : "Unknown Friend",
                                                friendId,
                                                profile != null ? profile : "",
                                                lastMessage,
                                                lastSeen
                                        );

                                        friendsList.add(friend);
                                        friendAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.w("FriendListFragment", "Friend document does not exist for ID: " + friendId);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("FriendListFragment", "Error fetching friend data: " + e.getMessage()));
                    })
                    .addOnFailureListener(e -> Log.e("FriendListFragment", "Error fetching chat messages: " + e.getMessage()));
        }
    }

    private void filterFriendsByUsername(String query) {
        List<Friend> filteredList = new ArrayList<>();

        for (Friend friend : friendsList) {
            if (friend.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(friend);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "No friends found matching your search.", Toast.LENGTH_SHORT).show();
        }

        // Update the adapter with the filtered list
        friendAdapter.updateFriendsList(filteredList);
    }


}

package com.example.brainpath.ui.interaction;

import android.content.DialogInterface;
import android.widget.EditText;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.brainpath.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InteractionAddActivity extends AppCompatActivity {

    // Declare Firestore instance
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_add);

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Bind buttons
        Button btnAddFriend = findViewById(R.id.btnAddFriend);
        Button btnAddGroup = findViewById(R.id.btnAddGroup);
        Button btnAddCommunity = findViewById(R.id.btnAddCommunity);

        // Set click listeners
        btnAddFriend.setOnClickListener(v -> showAddDialog("friend"));
        btnAddGroup.setOnClickListener(v -> showAddDialog("group"));
        btnAddCommunity.setOnClickListener(v -> showAddDialog("community"));
    }

    private void showAddDialog(String type) {
        // Create an input dialog for adding friend or community
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + capitalize(type));

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_input, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Retrieve input from dialog (username/group name)
            String inputName = ((EditText) customLayout.findViewById(R.id.input_name)).getText().toString();

            if (inputName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if it's a friend or community
            if (type.equals("friend")) {
                addFriend(inputName);
            } else if (type.equals("community")) {
                addCommunity(inputName);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void addFriend(String inputName) {
        // Get the current user's ID dynamically from Firebase Authentication
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Query Firestore to find the user by username
        db.collection("users")
                .whereEqualTo("username", inputName)  // Search by the entered username
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Assuming only one user with that username
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String friendUserId = document.getId();  // Get the userId of the friend

                        // Update both the current user's and the friend's friendIds array in the users collection
                        db.collection("users")
                                .document(currentUserId)
                                .update("friendIds", FieldValue.arrayUnion(friendUserId))  // Add friend to current user
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("users")
                                            .document(friendUserId)
                                            .update("friendIds", FieldValue.arrayUnion(currentUserId))  // Add current user to friend's friend list
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(this, "Friend added successfully!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error adding friend to friend's list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error adding friend to current user's list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addCommunity(String communityName) {
        // Get the current user's ID
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Retrieve the groups the current user is a part of
        db.collection("groups")
                .whereArrayContains("members", currentUserId)  // Get groups the current user is a member of
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "You are not part of any groups", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create a list to hold selected groups
                    ArrayList<String> selectedGroups = new ArrayList<>();
                    ArrayList<String> groupNames = new ArrayList<>();
                    final int[] count = {0};

                    // Fetch group names and add them to the list
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String groupName = document.getString("groupName");
                        if (groupName != null) {
                            groupNames.add(groupName);
                            selectedGroups.add(document.getId());  // Add group ID to the selected list
                        }
                        count[0]++;
                    }

                    // Now show the dialog for adding groups to the community
                    AlertDialog.Builder groupSelectionBuilder = new AlertDialog.Builder(this);
                    groupSelectionBuilder.setTitle("Select Groups to Add")
                            .setMultiChoiceItems(groupNames.toArray(new CharSequence[0]), null, (dialog, which, isChecked) -> {
                                if (isChecked) {
                                    selectedGroups.add(queryDocumentSnapshots.getDocuments().get(which).getId());  // Add selected group
                                } else {
                                    selectedGroups.remove(queryDocumentSnapshots.getDocuments().get(which).getId());  // Remove unselected group
                                }
                            })
                            .setPositiveButton("Create Community", (dialog, which) -> {
                                // Create the community with selected groups
                                Map<String, Object> communityData = new HashMap<>();
                                communityData.put("communityName", communityName);
                                communityData.put("groups", selectedGroups);  // Add selected groups to the community

                                // Add community data to the "communities" collection
                                db.collection("communities")
                                        .add(communityData)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(this, "Community created successfully!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error creating community: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                    groupSelectionBuilder.create().show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error retrieving groups: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

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
        // Create an input dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add " + capitalize(type));

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_input, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Retrieve input from dialog
            String inputName = ((EditText) customLayout.findViewById(R.id.input_name)).getText().toString();

            if (inputName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the current user's ID dynamically from Firebase Authentication
            String currentUserId = mAuth.getCurrentUser().getUid();

            // Here, you can choose how you want to handle the "friend" user ID (e.g., input or another logic)
            String friendUserId = "testFriendId457";  // For testing purposes, you can ask for this or select it dynamically

            // Create data for the new friend
            Map<String, Object> friendData = new HashMap<>();
            friendData.put("name", inputName);
            friendData.put("userId", friendUserId);  // Store the friend's ID

            // Add the new friend to the "friends" collection
            db.collection(type + "s")  // Adds to "friends", "groups", or "communities" collection
                    .add(friendData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, capitalize(type) + " added successfully!", Toast.LENGTH_SHORT).show();

                        // Clear input after adding friend
                        ((EditText) customLayout.findViewById(R.id.input_name)).setText("");

                        // Get the friend document ID
                        String friendId = documentReference.getId();

                        // Update the current user's friend list (using the dynamic currentUserId)
                        db.collection("users")
                                .document(currentUserId)
                                .update("friendIds", FieldValue.arrayUnion(friendId))  // Add friendId to current user
                                .addOnSuccessListener(aVoid -> {
                                    // Optionally, also update the new friend's friend list (using dynamic friendUserId)
                                    db.collection("users")
                                            .document(friendUserId)
                                            .update("friendIds", FieldValue.arrayUnion(currentUserId));  // Add currentUserId to new friend's friend list
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error updating current user's friend list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

package com.example.brainpath.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.brainpath.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private TextView currentName, currentUsername;
    private EditText nameChange, usernameChange;
    private Uri imageUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize UI components
        profileImageView = view.findViewById(R.id.profileImageView);
        currentName = view.findViewById(R.id.currentName);
        currentUsername = view.findViewById(R.id.currentUsername);
        nameChange = view.findViewById(R.id.nameChange);
        usernameChange = view.findViewById(R.id.usernameChange);
        Button uploadImageButton = view.findViewById(R.id.uploadImageButton);
        Button saveChangesButton = view.findViewById(R.id.saveChangesButton);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // Fetch and display current user data
        fetchUserDetails();

        // Image upload logic
        uploadImageButton.setOnClickListener(v -> openFileChooser());

        // Save changes logic
        saveChangesButton.setOnClickListener(v -> validateAndSaveChanges(view));

        return view;
    }

    private void fetchUserDetails() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        firebaseFirestore.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        String username = documentSnapshot.getString("username");
                        String name = documentSnapshot.getString("name");

                        // Set current user data
                        if (profileImageUrl != null) {
                            Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.profile_placeholder)
                                    .circleCrop() // Ensures the round shape
                                    .into(profileImageView);
                        }
                        currentUsername.setText(username != null ? username : "No Username");
                        currentName.setText(name != null ? name : "No Name");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri); // Display the selected image
        }
    }

    private void validateAndSaveChanges(View view) {
        String newUsername = usernameChange.getText().toString().trim();
        String newName = nameChange.getText().toString().trim();

        if (!newUsername.isEmpty()) {
            // Check for unique username
            firebaseFirestore.collection("users")
                    .whereEqualTo("username", newUsername)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            Toast.makeText(getContext(), "Username is already taken", Toast.LENGTH_SHORT).show();
                        } else {
                            saveAllChanges(newUsername, newName, view);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to validate username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            saveAllChanges(null, newName, view);
        }
    }

    private void saveAllChanges(String newUsername, String newName, View view) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        Map<String, Object> updates = new HashMap<>();

        // Add updated fields to the map
        if (newUsername != null) {
            updates.put("username", newUsername);
        }
        if (!newName.isEmpty()) {
            updates.put("name", newName);
        }

        // Handle image upload if a new image is selected
        if (imageUri != null) {
            StorageReference storageReference = firebaseStorage.getReference("profile_images/" + uid + ".jpg");
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        updates.put("profileImageUrl", uri.toString());
                        updateFirestore(uid, updates, view);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            updateFirestore(uid, updates, view);
        }
    }

    private void updateFirestore(String uid, Map<String, Object> updates, View view) {
        if (updates.isEmpty()) {
            Toast.makeText(getContext(), "No changes to save", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseFirestore.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();

                    // Navigate back to ProfileFragment
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_navigation_settings_to_navigation_profile);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save changes: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

package com.example.brainpath.ui.interaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.brainpath.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class PostForumFragment extends Fragment {

    private static final String TAG = "PostForumFragment";
    private EditText forumTitle, forumDesc;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri selectedImageUri;
    private Uri selectedFileUri;
    private TextView attachmentPreview;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    handleFilePreview();
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    handleImagePreview();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_interaction_forum_post, container, false);

        forumTitle = view.findViewById(R.id.forumTitle);
        forumDesc = view.findViewById(R.id.forumDesc);
        attachmentPreview = view.findViewById(R.id.attachmentPreview);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        ImageButton cancelButton = view.findViewById(R.id.cancelButton);
        ImageButton postButton = view.findViewById(R.id.postButton);
        ImageButton attachmentButton = view.findViewById(R.id.attachmentButton);
        ImageButton photoButton = view.findViewById(R.id.photoButton);

        postButton.setOnClickListener(v -> addPost());
        attachmentButton.setOnClickListener(v -> openFilePicker());
        photoButton.setOnClickListener(v -> openImageGallery());

        cancelButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigateUp();
        });

        return view;
    }

    private void addPost() {
        String title = forumTitle.getText().toString().trim();
        String description = forumDesc.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = mAuth.getCurrentUser().getUid();
        firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                        String timestamp = sdf.format(new Date());

                        HashMap<String, Object> post = new HashMap<>();
                        post.put("username", username);
                        post.put("title", title);
                        post.put("description", description);
                        post.put("timestamp", timestamp);

                        if (selectedImageUri != null) {
                            uploadImageAndCreatePost(post);
                        } else if (selectedFileUri != null) {
                            uploadFileAndCreatePost(post);
                        } else {
                            createPostInFirestore(post);
                        }
                    } else {
                        Toast.makeText(requireContext(), "User not found. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user details: " + e.getMessage());
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void uploadImageAndCreatePost(HashMap<String, Object> post) {
        StorageReference fileReference = storageRef.child("forum/images/" + System.currentTimeMillis() + ".jpg");

        fileReference.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Save file metadata to "forumFiles"
                            HashMap<String, Object> fileMetadata = new HashMap<>();
                            fileMetadata.put("type", "image");
                            fileMetadata.put("fileName", fileReference.getName());
                            fileMetadata.put("fileUrl", uri.toString());

                            firestore.collection("forumFiles").add(fileMetadata)
                                    .addOnSuccessListener(documentReference -> {
                                        String fileDocId = documentReference.getId();
                                        Log.d(TAG, "Image metadata saved with ID: " + fileDocId);

                                        // Add fileDocId to the post and save to "forumPosts"
                                        post.put("fileDocId", fileDocId);
                                        createPostInFirestore(post);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to save image metadata: " + e.getMessage());
                                        Toast.makeText(requireContext(), "Failed to save image metadata", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error getting image URL: " + e.getMessage());
                            Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading image: " + e.getMessage());
                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadFileAndCreatePost(HashMap<String, Object> post) {
        String fileExtension = getFileExtension(selectedFileUri);
        String storagePath = fileExtension.equals(".pdf") ? "forum/pdf_files/" : "forum/other_files/";
        StorageReference fileReference = storageRef.child(storagePath + System.currentTimeMillis() + fileExtension);

        fileReference.putFile(selectedFileUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Save file metadata to "forumFiles"
                            HashMap<String, Object> fileMetadata = new HashMap<>();
                            fileMetadata.put("type", "file");
                            fileMetadata.put("fileName", fileReference.getName());
                            fileMetadata.put("fileUrl", uri.toString());

                            firestore.collection("forumFiles").add(fileMetadata)
                                    .addOnSuccessListener(documentReference -> {
                                        String fileDocId = documentReference.getId();
                                        Log.d(TAG, "File metadata saved with ID: " + fileDocId);

                                        // Add fileDocId to the post and save to "forumPosts"
                                        post.put("fileDocId", fileDocId);
                                        createPostInFirestore(post);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to save file metadata: " + e.getMessage());
                                        Toast.makeText(requireContext(), "Failed to save file metadata", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error getting file URL: " + e.getMessage());
                            Toast.makeText(requireContext(), "File upload failed", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading file: " + e.getMessage());
                    Toast.makeText(requireContext(), "File upload failed", Toast.LENGTH_SHORT).show();
                });
    }



    private void createPostInFirestore(HashMap<String, Object> post) {
        firestore.collection("forumPosts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Post added successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Post added with ID: " + documentReference.getId());

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigateUp();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating post: " + e.getMessage());
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void handleFilePreview() {
        if (selectedFileUri != null) {
            String fileName = selectedFileUri.getLastPathSegment();
            attachmentPreview.setText(fileName != null ? "Selected file: " + fileName : "File selection error");
        }
    }

    private void handleImagePreview() {
        if (selectedImageUri != null) {
            String imageName = selectedImageUri.getLastPathSegment();
            attachmentPreview.setText(imageName != null ? "Selected image: " + imageName : "Image selection error");
        }
    }

    private String getFileExtension(Uri uri) {
        String fileName = uri.getLastPathSegment();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}

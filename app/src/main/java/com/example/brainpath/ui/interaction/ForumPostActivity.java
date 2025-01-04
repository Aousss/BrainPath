package com.example.brainpath.ui.interaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class ForumPostActivity extends AppCompatActivity {

    private EditText forumTitle, forumDesc;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Uri selectedImageUri;
    private Uri selectedFileUri;
    private TextView attachmentPreview;

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    handleFilePreview();  // Handle file preview
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    handleImagePreview();  // Handle image preview
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_forum_post);

        // Check and request permissions if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }

        forumTitle = findViewById(R.id.forumTitle);
        forumDesc = findViewById(R.id.forumDesc);
        attachmentPreview = findViewById(R.id.attachmentPreview);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        ImageButton postButton = findViewById(R.id.postButton);
        ImageButton attachmentButton = findViewById(R.id.attachmentButton);
        ImageButton photoButton = findViewById(R.id.photoButton);

        postButton.setOnClickListener(v -> addPost());
        attachmentButton.setOnClickListener(v -> openFilePicker());
        photoButton.setOnClickListener(v -> openImageGallery());
    }

    private void addPost() {
        String title = forumTitle.getText().toString().trim();
        String description = forumDesc.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
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
                            uploadImageAndCreatePost(post); // Upload image and add the imageUrl
                        } else if (selectedFileUri != null) {
                            uploadFileAndCreatePost(post); // Handle file upload if no image
                        } else {
                            createPostInFirestore(post);  // If no image or file, just create the post
                        }
                    } else {
                        Toast.makeText(this, "User not found. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageAndCreatePost(HashMap<String, Object> post) {
        // Use a unique name for the image and save it to Firebase Storage
        StorageReference fileReference = storageRef.child("forum/images/" + System.currentTimeMillis() + ".jpg");

        fileReference.putFile(selectedImageUri)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    attachmentPreview.setText(getString(R.string.uploading_image_progress, (int) progress));
                    Log.d("ForumPostActivity", "Upload progress: " + progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("ForumPostActivity", "Image upload successful!");

                    // Now, we will try to get the download URL of the uploaded image
                    fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Log the image URL for debugging
                                Log.d("ForumPostActivity", "Image uploaded. URL: " + uri.toString());

                                // Add the imageUrl to the post
                                post.put("imageUrl", uri.toString());

                                // Now that the image URL is added, create the post in Firestore
                                createPostInFirestore(post);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ForumPostActivity", "Failed to get image URL: " + e.getMessage());
                                attachmentPreview.setText(getString(R.string.upload_failed));
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("ForumPostActivity", "Image upload failed: " + e.getMessage());
                    attachmentPreview.setText(getString(R.string.upload_failed));
                });
    }




    private void uploadFileAndCreatePost(HashMap<String, Object> post) {
        String fileExtension = getFileExtension(selectedFileUri);
        String storagePath = fileExtension.equals(".pdf") ? "forum/pdf_files/" : "forum/other_files/";

        StorageReference fileReference = storageRef.child(storagePath + System.currentTimeMillis() + fileExtension);

        fileReference.putFile(selectedFileUri)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    attachmentPreview.setText(getString(R.string.uploading_file_progress, (int) progress));
                })
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Store file URL in forumFiles collection
                            HashMap<String, Object> fileData = new HashMap<>();
                            fileData.put("fileUrl", uri.toString());

                            firestore.collection("forumFiles").add(fileData)
                                    .addOnSuccessListener(fileDocRef -> {
                                        // Save file document ID in the post
                                        post.put("fileDocId", fileDocRef.getId());
                                        createPostInFirestore(post);
                                    })
                                    .addOnFailureListener(e -> {
                                        attachmentPreview.setText(getString(R.string.upload_failed));
                                    });
                        })
                        .addOnFailureListener(e -> {
                            attachmentPreview.setText(getString(R.string.upload_failed));
                        }))
                .addOnFailureListener(e -> {
                    attachmentPreview.setText(getString(R.string.upload_failed));
                });
    }

    private void createPostInFirestore(HashMap<String, Object> post) {
        firestore.collection("forumPosts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ForumPostActivity", "Post created successfully with ID: " + documentReference.getId());
                    Toast.makeText(this, "Post added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("ForumPostActivity", "Error creating post: " + e.getMessage());
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        String fileName = selectedFileUri.getLastPathSegment();
        if (fileName != null) {
            attachmentPreview.setText(getString(R.string.selected_file, fileName));
        }
    }

    private void handleImagePreview() {
        String imageName = selectedImageUri.getLastPathSegment();
        if (imageName != null) {
            attachmentPreview.setText(getString(R.string.selected_image, imageName));
        }
    }

    private String getFileExtension(Uri uri) {
        String fileName = uri.getLastPathSegment();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

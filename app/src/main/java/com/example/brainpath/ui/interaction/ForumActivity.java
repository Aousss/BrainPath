package com.example.brainpath.ui.interaction;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView forumRecyclerView;
    private ForumPostAdapter forumPostAdapter;
    private List<ForumPost> forumPostList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_forum);

        firestore = FirebaseFirestore.getInstance();
        forumRecyclerView = findViewById(R.id.forumLists_RecyclerView);
        forumRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        forumPostList = new ArrayList<>();
        forumPostAdapter = new ForumPostAdapter(this, forumPostList);
        forumRecyclerView.setAdapter(forumPostAdapter);

        // Load forum posts from Firestore
        loadForumPosts();
    }

    private void loadForumPosts() {
        firestore.collection("forumPosts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<ForumPost> posts = queryDocumentSnapshots.toObjects(ForumPost.class);

                        for (ForumPost post : posts) {
                            String fileDocId = post.getFileDocId();  // This should now work

                            if (fileDocId != null) {
                                // Fetch the file URL using the fileDocId from the forumFiles collection
                                firestore.collection("forumFiles")
                                        .document(fileDocId)
                                        .get()
                                        .addOnSuccessListener(fileDocSnapshot -> {
                                            String fileUrl = fileDocSnapshot.getString("fileUrl");
                                            post.setFileUrl(fileUrl);  // Set the file URL (image URL) in the post object
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ForumActivity.this, "Error fetching image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }

                        forumPostList.clear();
                        forumPostList.addAll(posts);
                        forumPostAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ForumActivity.this, "Error loading posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}

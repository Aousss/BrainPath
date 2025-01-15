package com.example.brainpath.ui.interaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumFragment extends Fragment {

    private RecyclerView forumRecyclerView;
    private ForumPostAdapter forumPostAdapter;
    private List<ForumPost> forumPostList;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_interaction_forum, container, false);

        firestore = FirebaseFirestore.getInstance();
        forumRecyclerView = view.findViewById(R.id.forumLists_RecyclerView);
        forumRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        forumPostList = new ArrayList<>();
        forumPostAdapter = new ForumPostAdapter(requireContext(), forumPostList);
        forumRecyclerView.setAdapter(forumPostAdapter);

        // Load forum posts from Firestore
        loadForumPosts();

        FloatingActionButton gotoForumButton = view.findViewById(R.id.addNewForum);
        gotoForumButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_forumFragment_to_postForumFragment);
        });

        return view;
    }

    private void loadForumPosts() {
        firestore.collection("forumPosts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, exception) -> {
                    if (exception != null) {
                        Toast.makeText(requireContext(), "Error loading posts: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<ForumPost> posts = queryDocumentSnapshots.toObjects(ForumPost.class);
                        List<String> fileDocIds = extractFileDocIds(posts);

                        if (!fileDocIds.isEmpty()) {
                            fetchFileUrls(fileDocIds, posts);
                        } else {
                            forumPostAdapter.updateData(posts); // No files, just update posts
                        }
                    }
                });
    }

    private List<String> extractFileDocIds(List<ForumPost> posts) {
        List<String> fileDocIds = new ArrayList<>();
        for (ForumPost post : posts) {
            if (post.getFileDocId() != null) {
                fileDocIds.add(post.getFileDocId());
            }
        }
        return fileDocIds;
    }

    private void fetchFileUrls(List<String> fileDocIds, List<ForumPost> posts) {
        firestore.collection("forumFiles")
                .whereIn(FieldPath.documentId(), fileDocIds)
                .get()
                .addOnSuccessListener(fileQuerySnapshot -> {
                    Map<String, String> fileUrls = new HashMap<>();
                    for (DocumentSnapshot doc : fileQuerySnapshot.getDocuments()) {
                        fileUrls.put(doc.getId(), doc.getString("fileUrl"));
                    }

                    // Associate file URLs with their respective posts
                    for (ForumPost post : posts) {
                        String fileUrl = fileUrls.get(post.getFileDocId());
                        if (fileUrl != null) {
                            post.setImageUrl(fileUrl);
                        }
                    }

                    // Update the adapter with posts
                    forumPostAdapter.updateData(posts);
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(requireContext(), "Error fetching file URLs: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    // Update adapter without file URLs
                    forumPostAdapter.updateData(posts);
                });
    }
}

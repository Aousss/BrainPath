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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

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
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp (latest first)
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
                                            post.setImageUrl(fileUrl);  // Set the file URL (image URL) in the post object
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(requireContext(), "Error fetching image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }

                        forumPostList.clear();
                        forumPostList.addAll(posts);
                        forumPostAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error loading posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

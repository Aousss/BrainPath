package com.example.brainpath.ui.interaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.brainpath.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.ViewHolder> {

    private Context context;
    private List<ForumPost> postList;
    private FirebaseFirestore firestore;

    // Constructor
    public ForumPostAdapter(Context context, List<ForumPost> postList) {
        this.context = context;
        this.postList = postList;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ForumPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual forum post items
        View view = LayoutInflater.from(context).inflate(R.layout.item_forum_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumPostAdapter.ViewHolder holder, int position) {
        ForumPost post = postList.get(position);

        holder.usernameText.setText(post.getUsername());
        holder.titleText.setText(post.getTitle());
        holder.descriptionText.setText(post.getDescription());
        holder.timestampText.setText(post.getTimestamp());

        // Check if the image URL is available and load the image
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            // Load image if URL is available
            Glide.with(context)
                    .load(post.getImageUrl()) // Pass the URL stored in Firestore
                    .into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE); // Make sure the ImageView is visible
        } else {
            holder.imageView.setVisibility(View.GONE); // Hide if no image URL
        }

        // Handle file URL preview
        if (post.getFileUrl() != null && !post.getFileUrl().isEmpty()) {
            holder.filePreviewText.setVisibility(View.VISIBLE);
            holder.filePreviewText.setText("File attached: " + post.getFileUrl());
        } else {
            holder.filePreviewText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            // Handle item click if needed (open full post details, etc.)
            Toast.makeText(context, "Post clicked: " + post.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    // Handle file URL preview (

    @Override
    public int getItemCount() {
        // Return the size of the post list
        return postList.size();
    }

    // Method to update the adapter data
    public void updateData(List<ForumPost> newPosts) {
        postList.clear();
        postList.addAll(newPosts);
        notifyDataSetChanged();
    }

    // ViewHolder class to hold the views for each forum post item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText, titleText, descriptionText, timestampText, filePreviewText;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views by finding them by ID
            usernameText = itemView.findViewById(R.id.usernameText);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            timestampText = itemView.findViewById(R.id.timestampText);
            filePreviewText = itemView.findViewById(R.id.filePreviewText);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

package com.example.brainpath.ui.interaction;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.brainpath.R;

import java.util.List;

public class ForumPostAdapter extends RecyclerView.Adapter<ForumPostAdapter.ViewHolder> {

    private Context context;
    private List<ForumPost> postList;

    // Constructor
    public ForumPostAdapter(Context context, List<ForumPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, titleText, descriptionText, timestampText;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            timestampText = itemView.findViewById(R.id.timestampText);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forum_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForumPost post = postList.get(position);

        holder.usernameText.setText(post.getUsername());
        holder.titleText.setText(post.getTitle());
        holder.descriptionText.setText(post.getDescription());
        holder.timestampText.setText(post.getTimestamp());

        // Load image if URL is provided
        if (!TextUtils.isEmpty(post.getImageUrl())) {
            Glide.with(context)
                    .load(post.getImageUrl())
                    .into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);  // Show image view if URL exists
        } else {
            holder.imageView.setVisibility(View.GONE);  // Hide image view if no URL
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Update data using DiffUtil
    public void updateData(List<ForumPost> newPosts) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(postList, newPosts));
        postList.clear();
        postList.addAll(newPosts);
        diffResult.dispatchUpdatesTo(this);
    }

    // Static DiffCallback class
    private static class DiffCallback extends DiffUtil.Callback {

        private final List<ForumPost> oldList;
        private final List<ForumPost> newList;

        public DiffCallback(List<ForumPost> oldList, List<ForumPost> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // Assuming timestamp uniquely identifies a post
            return oldList.get(oldItemPosition).getTimestamp()
                    .equals(newList.get(newItemPosition).getTimestamp());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            // Compare content of posts
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}

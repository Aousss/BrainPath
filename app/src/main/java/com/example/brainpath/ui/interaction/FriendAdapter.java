package com.example.brainpath.ui.interaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.brainpath.R;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friendsList;
    private OnItemClickListener onItemClickListener;

    public FriendAdapter(Context context, List<Friend> friendsList, OnItemClickListener listener) {
        this.context = context;
        this.friendsList = friendsList;
        this.onItemClickListener = listener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.interaction_friend_lists, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);

        holder.friendNameTextView.setText(friend.getUsername());
        holder.lastMessageTextView.setText(friend.getLastMessage());
        holder.lastSeenTextView.setText(friend.getLastSeen());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(friend));

        // Load the profile image using Glide
        Glide.with(context)
                .load(friend.getProfile()) // Profile image URL
                .placeholder(R.drawable.ic_profile_placeholder) // Default placeholder
                .error(R.drawable.ic_profile_placeholder) // Fallback image
                .circleCrop() // Transform the image into a circle
                .into(holder.profileImageURL);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView, lastMessageTextView, lastSeenTextView;
        ImageView profileImageURL;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.friend_name);
            lastMessageTextView = itemView.findViewById(R.id.last_chat_message);
            lastSeenTextView = itemView.findViewById(R.id.last_chat_date);
            profileImageURL = itemView.findViewById(R.id.profile_image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Friend friend);
    }
}

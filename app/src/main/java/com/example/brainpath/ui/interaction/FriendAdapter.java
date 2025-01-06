package com.example.brainpath.ui.interaction;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friendsList;
    private OnItemClickListener onItemClickListener;

    // Constructor
    public FriendAdapter(Context context, List<Friend> friendsList, OnItemClickListener listener) {
        this.context = context;
        this.friendsList = friendsList;
        this.onItemClickListener = listener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout for each friend
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);

        // Fetch username (change from 'name' to 'username')
        String friendUsername = friend.getUsername();  // Updated to use username

        // Debugging log: check if the username is correct
        Log.d("FriendAdapter", "Binding friend at position " + position + ": " + friendUsername);

        if (friendUsername != null && !friendUsername.isEmpty()) {
            holder.friendNameTextView.setText(friendUsername);  // Display the username
        } else {
            // Log error if the username is missing or empty
            Log.e("FriendAdapter", "Friend username is null or empty at position " + position);
            holder.friendNameTextView.setText("Unknown Friend");
        }

        // Set click listener on the item to trigger action in FriendListActivity
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(friend));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    // ViewHolder for the Friend item
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView;

        public FriendViewHolder(View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.friendNameTextView);
        }
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Friend friend);
    }
}

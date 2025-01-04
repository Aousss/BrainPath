package com.example.brainpath.ui.interaction;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friendList;
    private OnFriendClickListener onFriendClickListener;

    public FriendAdapter(Context context, List<Friend> friendList, OnFriendClickListener onFriendClickListener) {
        this.context = context;
        this.friendList = friendList;
        this.onFriendClickListener = onFriendClickListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.usernameTextView.setText(friend.getUsername());

        holder.itemView.setOnClickListener(v -> onFriendClickListener.onFriendClick(friend));
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.friendNameTextView);  // Adjust ID as needed
        }
    }
}


package com.example.brainpath.ui.interaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;
    private Context context;

    public ChatAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        // Set the message text
        holder.messageText.setText(message.getMessage());

        // Format the timestamp in Malaysia Time (MYT)
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur")); // Set timezone to Malaysia

        // Format the timestamp to string
        String timestamp = sdf.format(message.getTimestamp().toDate()); // Convert Timestamp to Date

        // Set the timestamp in the TextView
        holder.timestampText.setText(timestamp);
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<ChatMessage> newMessages) {
        messages = newMessages;
        notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestampText; // Add reference for timestamp TextView

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText); // Bind timestamp TextView
        }
    }

}

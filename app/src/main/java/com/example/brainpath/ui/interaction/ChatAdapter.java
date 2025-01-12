package com.example.brainpath.ui.interaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    private List<ChatMessage> messages;
    private Context context;
    private String currentUserId;

    public ChatAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        return message.getSenderId().equals(currentUserId) ? VIEW_TYPE_SENDER : VIEW_TYPE_RECEIVER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        // Convert Firestore Timestamp to Date
        Date timestamp = message.getTimestamp().toDate();

        // Format the timestamp
        String formattedTimestamp = DateTimeUtils.formatTimestamp(timestamp);

        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).bind(message, formattedTimestamp);
        } else if (holder instanceof ReceiverViewHolder) {
            ((ReceiverViewHolder) holder).bind(message, formattedTimestamp);
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<ChatMessage> newMessages) {
        messages = newMessages;
        notifyDataSetChanged();
    }

    // Sender ViewHolder
    static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestampText;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }

        void bind(ChatMessage message, String timestamp) {
            messageText.setText(message.getMessage());
            timestampText.setText(timestamp);
        }
    }

    // Receiver ViewHolder
    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestampText;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }

        void bind(ChatMessage message, String timestamp) {
            messageText.setText(message.getMessage());
            timestampText.setText(timestamp);
        }
    }
}

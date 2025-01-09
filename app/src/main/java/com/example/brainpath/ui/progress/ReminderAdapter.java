package com.example.brainpath.ui.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;

import java.util.List;
import com.example.brainpath.ui.progress.Reminder;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> events;

    public ReminderAdapter(List<Reminder> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_reminder_lists, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder event = events.get(position);
        holder.reminderTitle.setText(event.getTitle());
        holder.dueDate.setText(event.getDate());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminderTitle, dueDate;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderTitle = itemView.findViewById(R.id.reminderTitle);
            dueDate = itemView.findViewById(R.id.dueDate);
        }
    }
}

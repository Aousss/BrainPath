package com.example.brainpath.ui.progress;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brainpath.ui.progress.Subject;

import com.example.brainpath.R;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<Subject> subjects;

    public SubjectAdapter(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.subjectName.setText(subject.getName());
        holder.subjectProgress.setProgress(subject.getProgress());
        holder.studyTime.setText(subject.getFormattedStudyTime());
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public void updateData(List<Subject> subjects) {
        this.subjects = subjects;
        notifyDataSetChanged();
    }


    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, studyTime;
        ProgressBar subjectProgress;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectName);
            studyTime = itemView.findViewById(R.id.studyTime);
            subjectProgress = itemView.findViewById(R.id.subjectProgress);
        }
    }
}

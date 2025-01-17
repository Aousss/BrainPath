package com.example.brainpath.ui.assessment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private final List<QuizResult> resultList;

    public ResultAdapter(List<QuizResult> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_row, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        QuizResult result = resultList.get(position);
        holder.subject.setText(result.getSubject());
        holder.title.setText(result.getTitle());
        holder.marks.setText(result.getMarks());
        holder.status.setText(result.getStatus());
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView subject, title, marks, status;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.result_subject);
            title = itemView.findViewById(R.id.result_title);
            marks = itemView.findViewById(R.id.result_marks);
            status = itemView.findViewById(R.id.result_status);
        }
    }
}

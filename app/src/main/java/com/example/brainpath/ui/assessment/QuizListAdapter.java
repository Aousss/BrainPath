package com.example.brainpath.ui.assessment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {

    private List<QuizModel> quizModelList;
    private OnQuizClickListener listener;

    public QuizListAdapter(List<QuizModel> quizModelList, OnQuizClickListener listener) {
        this.quizModelList = quizModelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item_recycler_row, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizModel quiz = quizModelList.get(position);

        // Bind data to the view
        holder.title.setText(quiz.getTitle());
        holder.subtitle.setText(quiz.getSubtitle());
        holder.time.setText(quiz.getTime() + " mins");

        // Set the click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClick(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizModelList.size();
    }

    // ViewHolder for RecyclerView
    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, time;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.quiz_title_text);
            subtitle = itemView.findViewById(R.id.quiz_subtitle_text);
            time = itemView.findViewById(R.id.quiz_time_text);
        }
    }

    // Listener interface for click events
    public interface OnQuizClickListener {
        void onQuizClick(QuizModel quiz);
    }
}

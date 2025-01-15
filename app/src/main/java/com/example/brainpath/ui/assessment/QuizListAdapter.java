package com.example.brainpath.ui.assessment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.example.brainpath.R;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {

    private List<QuizModel> quizModelList;

    public QuizListAdapter(List<QuizModel> quizModelList) {
        this.quizModelList = quizModelList;
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
        holder.title.setText(quiz.getTitle());
        holder.subtitle.setText(quiz.getSubtitle());
        holder.time.setText(quiz.getTime() + " mins");

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("quiz", quiz);

            // Navigate to QuizFragment
            Navigation.findNavController(v).navigate(R.id.navigation_quizFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return quizModelList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, time;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.quiz_title_text);
            subtitle = itemView.findViewById(R.id.quiz_subtitle_text);
            time = itemView.findViewById(R.id.quiz_time_text);
        }
    }
}




//////////////////////////////////////
//package com.example.brainpath.ui.assessment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.brainpath.R;
//
//import java.util.List;
//
//public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {
//    private List<QuizModel> quizModelList;
//
//    public QuizListAdapter(List<QuizModel> quizModelList) {
//        this.quizModelList = quizModelList;
//    }
//
//    @NonNull
//    @Override
//    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item_recycler_row, parent, false);
//        return new QuizViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
//        QuizModel quiz = quizModelList.get(position);
//        holder.title.setText(quiz.getTitle());
//        holder.subtitle.setText(quiz.getSubtitle());
//        holder.time.setText(quiz.getTime() + " mins");
//
////        holder.itemView.setOnClickListener(v -> {
////            Intent intent = new Intent(v.getContext(), QuizFragment.class);
////            intent.putExtra("quiz", quiz);
////            v.getContext().startActivity(intent);
////        });
//
//        holder.itemView.setOnClickListener(v -> {
//            Navigation.findNavController(v).navigate(R.id.navigation_quizFragment);
//        });
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return quizModelList.size();
//    }
//
//    public static class QuizViewHolder extends RecyclerView.ViewHolder {
//        TextView title, subtitle, time;
//
//        public QuizViewHolder(@NonNull View itemView) {
//            super(itemView);
//            title = itemView.findViewById(R.id.quiz_title_text);
//            subtitle = itemView.findViewById(R.id.quiz_subtitle_text);
//            time = itemView.findViewById(R.id.quiz_time_text);
//        }
//    }
//}

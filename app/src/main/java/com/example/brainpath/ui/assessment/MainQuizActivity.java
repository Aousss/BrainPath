package com.example.brainpath.ui.assessment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.brainpath.databinding.ActivityMainQuizBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainQuizActivity extends AppCompatActivity {
    private ActivityMainQuizBinding binding;
    private List<QuizModel> quizList;
    private QuizListAdapter adapter;
    private String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get subject from intent
        subject = getIntent().getStringExtra("subject");

        // Initialize list and adapter
        quizList = new ArrayList<>();
        adapter = new QuizListAdapter(quizList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Fetch quizzes for the subject
        fetchQuizzesForSubject(subject);
    }

    private void fetchQuizzesForSubject(String subject) {
        FirebaseDatabase.getInstance().getReference("quizzes")
                .orderByChild("subject")
                .equalTo(subject)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        quizList.clear();
                        for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                            QuizModel quiz = quizSnapshot.getValue(QuizModel.class);
                            quizList.add(quiz);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }
}



////////////////////////////////////////////////////////////////

//package com.example.brainpath.ui.assessment;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.example.brainpath.R;
//import com.example.brainpath.databinding.ActivityMainQuizBinding;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainQuizActivity extends AppCompatActivity {
//    private ActivityMainQuizBinding binding;
//    private List<QuizModel> quizList;
//    private QuizListAdapter adapter;
//    private String subject;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityMainQuizBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Get subject from intent
//        subject = getIntent().getStringExtra("subject");
//
//        // Initialize list and adapter
//        quizList = new ArrayList<>();
//        adapter = new QuizListAdapter(quizList);
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        binding.recyclerView.setAdapter(adapter);
//
//        // Fetch quizzes for the subject
//        fetchQuizzesForSubject(subject);
//    }
//
//    private void fetchQuizzesForSubject(String subject) {
//        FirebaseDatabase.getInstance().getReference("quizzes")
//                .orderByChild("subject")
//                .equalTo(subject)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        quizList.clear();
//                        for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
//                            QuizModel quiz = quizSnapshot.getValue(QuizModel.class);
//                            quizList.add(quiz);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        // Handle error
//                    }
//                });
//    }
//}

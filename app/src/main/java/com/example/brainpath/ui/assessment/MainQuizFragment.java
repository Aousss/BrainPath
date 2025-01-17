package com.example.brainpath.ui.assessment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.brainpath.R;
import com.example.brainpath.databinding.ActivityMainQuizBinding;  // Correct binding for activity_main_quiz.xml
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainQuizFragment extends Fragment {

    private ActivityMainQuizBinding binding;  // Use the binding for activity_main_quiz.xml
    private List<QuizModel> quizList;
    private QuizListAdapter adapter;
    private String subject;

    public MainQuizFragment() {
        super(R.layout.activity_main_quiz);  // Reference the layout activity_main_quiz.xml
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subject = getArguments().getString("subject");
            Toast.makeText(getContext(),subject, Toast.LENGTH_SHORT).show();
        }
        quizList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding for activity_main_quiz.xml
        binding = ActivityMainQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Update the title dynamically
        if (subject != null && !subject.isEmpty()) {
            // Capitalize the first letter and make the rest lowercase
            String formattedSubject = subject.substring(0, 1).toUpperCase() + subject.substring(1).toLowerCase();
            binding.textViewTitle.setText(formattedSubject + " Quiz Center");
        }


        fetchQuizzesForSubject(subject);
        adapter = new QuizListAdapter(quizList, quiz -> {
            // Create a new Bundle to pass data to QuizFragment
            Bundle bundle = new Bundle();
            bundle.putString("quizId", quiz.getId());  // Pass quizId
            bundle.putString("quizTitle", quiz.getTitle());  // Pass quizTitle
            bundle.putString("quizTime", quiz.getTime()); // Pass time

            // Navigate to QuizFragment with the bundle containing quizId and quizTitle
            Navigation.findNavController(view)
                    .navigate(R.id.action_mainQuizFragment_to_quizFragment, bundle);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
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
                            if (quiz != null) {
                                quiz.setId(quizSnapshot.getKey()); // Set the Firebase key as the ID
                                quizList.add(quiz);
                            }
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

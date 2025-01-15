package com.example.brainpath.ui.assessment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.brainpath.R;
import com.example.brainpath.databinding.FragmentQuizBinding;
import com.example.brainpath.databinding.ScoreDialogBinding;
import com.example.brainpath.ui.assessment.QuizModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;
    private String subject;
    private List<QuestionModel> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        // Inflate the binding for the fragment
//        binding = FragmentQuizBinding.inflate(inflater, container, false);
//
//        // Retrieve the subject passed via arguments
//        if (getArguments() != null) {
//            subject = getArguments().getString("subject");
//            Log.d("QuizFragment", "Subject received: " + subject);
//        } else {
//            Log.e("QuizFragment", "No subject passed to QuizFragment");
//            throw new IllegalStateException("Subject must be passed to QuizFragment");
//        }
//
//        // Fetch questions based on the subject
//        fetchQuestionsForSubject();
//
//        // Setup UI listeners (e.g., button clicks)
//        setupListeners();
//
//        return binding.getRoot();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            subject = getArguments().getString("subject");
        }

        fetchQuestionsForSubject();

        setupListeners();

        return binding.getRoot();
    }

//    private void fetchQuestionsForSubject() {
//        FirebaseDatabase.getInstance().getReference("quizzes")
//                .orderByChild("subject")
//                .equalTo(subject)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        questionList.clear(); // Ensure the list is cleared before adding new data
//                        for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
//                            QuizModel quiz = quizSnapshot.getValue(QuizModel.class);
//                            if (quiz != null && quiz.getQuestionList() != null) {
//                                questionList.addAll(quiz.getQuestionList());
//                            }
//                        }
//
//                        if (!questionList.isEmpty()) {
//                            Log.d("QuizFragment", "Questions loaded: " + questionList.size());
//                            loadFirstQuestion();
//                        } else {
//                            Log.w("QuizFragment", "No questions found for subject: " + subject);
//                            showNoQuestionsMessage();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Log.e("QuizFragment", "Error fetching questions: " + databaseError.getMessage());
//                    }
//                });
//    }
//
//    private void setupListeners() {
//        // Example listener for answer buttons
//        binding.btn0.setOnClickListener(v -> checkAnswer(binding.btn0.getText().toString()));
//        binding.btn1.setOnClickListener(v -> checkAnswer(binding.btn1.getText().toString()));
//        binding.btn2.setOnClickListener(v -> checkAnswer(binding.btn2.getText().toString()));
//        binding.btn3.setOnClickListener(v -> checkAnswer(binding.btn3.getText().toString()));
//
//        // Listener for a retry button in case of no questions
//        binding.retryButton.setOnClickListener(v -> fetchQuestionsForSubject());
//    }
//
//    private void loadFirstQuestion() {
//        if (!questionList.isEmpty()) {
//            currentQuestionIndex = 0;
//            updateQuestionUI();
//        }
//    }
//
//    private void updateQuestionUI() {
//        if (currentQuestionIndex < questionList.size()) {
//            QuestionModel question = questionList.get(currentQuestionIndex);
//            binding.questionTextview.setText(question.getQuestion());
//            binding.btn0.setText(question.getOptions().get(0));
//            binding.btn1.setText(question.getOptions().get(1));
//            binding.btn2.setText(question.getOptions().get(2));
//            binding.btn3.setText(question.getOptions().get(3));
//        } else {
//            finishQuiz();
//        }
//    }
//
//    private void showNoQuestionsMessage() {
//        binding.quizLayout.setVisibility(View.GONE);
//        binding.noQuestionsLayout.setVisibility(View.VISIBLE);
//    }







    private void fetchQuestionsForSubject() {
        FirebaseDatabase.getInstance().getReference("quizzes")
                .orderByChild("subject")
                .equalTo(subject)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                            QuizModel quiz = quizSnapshot.getValue(QuizModel.class);
                            if (quiz != null && quiz.getQuestionList() != null) {
                                questionList.addAll(quiz.getQuestionList());
                            }
                        }
                        loadQuestion();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle Firebase error
                    }
                });
    }


    private void loadQuestion() {
        if (currentQuestionIndex >= questionList.size()) {
            finishQuiz();
            return;
        }

        QuestionModel question = questionList.get(currentQuestionIndex);
        binding.questionTextview.setText(question.getQuestion());
        binding.btn0.setText(question.getOptions().get(0));
        binding.btn1.setText(question.getOptions().get(1));
        binding.btn2.setText(question.getOptions().get(2));
        binding.btn3.setText(question.getOptions().get(3));
    }

    private void setupListeners() {
        View.OnClickListener optionClickListener = v -> {
            String selectedAnswer = ((android.widget.Button) v).getText().toString();
            checkAnswer(selectedAnswer);
        };

        binding.btn0.setOnClickListener(optionClickListener);
        binding.btn1.setOnClickListener(optionClickListener);
        binding.btn2.setOnClickListener(optionClickListener);
        binding.btn3.setOnClickListener(optionClickListener);

        binding.nextBtn.setOnClickListener(v -> {
            currentQuestionIndex++;
            loadQuestion();
        });
    }

    private void checkAnswer(String selectedAnswer) {
        QuestionModel question = questionList.get(currentQuestionIndex);
        if (question.getCorrect().equals(selectedAnswer)) {
            score++;
        }
    }

    private void finishQuiz() {
        int totalQuestions = questionList.size();
        int percentage = (int) (((float) score / totalQuestions) * 100);

        // Inflate the custom score dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ScoreDialogBinding dialogBinding = ScoreDialogBinding.inflate(inflater);

        // Set up the dialog UI
        dialogBinding.scoreProgressIndicator.setProgress(percentage);
        dialogBinding.scoreProgressText.setText(percentage + " %");

        if (percentage > 60) {
            dialogBinding.scoreTitle.setText("Congrats! You have passed");
            dialogBinding.scoreTitle.setTextColor(Color.BLUE);
        } else {
            dialogBinding.scoreTitle.setText("Oops! You have failed");
            dialogBinding.scoreTitle.setTextColor(Color.RED);
        }

        dialogBinding.scoreSubtitle.setText(score + " out of " + totalQuestions + " are correct");

        // Create and display the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        dialogBinding.finishBtn.setOnClickListener(v -> {
            dialog.dismiss();
            // Navigate back to the assessment screen or close the activity
            requireActivity().onBackPressed();
        });

        dialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

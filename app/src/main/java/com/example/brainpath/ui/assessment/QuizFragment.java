package com.example.brainpath.ui.assessment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.brainpath.R;
import com.example.brainpath.databinding.FragmentQuizBinding;
import com.example.brainpath.databinding.ScoreDialogBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;
    private String quizId;
    private String quizTitle;
    private List<QuestionModel> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    public QuizFragment() {
        super(R.layout.fragment_quiz);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            quizId = getArguments().getString("quizId");
            quizTitle = getArguments().getString("quizTitle");
        }

        // Ensure that quizId and quizTitle are not null
        if (quizId == null || quizTitle == null) {
            // Handle the error or show a message
            return binding.getRoot();  // You can also return an error view or exit
        }

        // Set quiz title in UI
        binding.quizTitleTextview.setText(quizTitle);  // Ensure the ID matches the one in your XML

        fetchQuestionsForQuiz();
        setupListeners();

        return binding.getRoot();
    }


    private void fetchQuestionsForQuiz() {
        FirebaseDatabase.getInstance().getReference("quizzes")
                .child(quizId)
                .child("questions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                            QuestionModel question = questionSnapshot.getValue(QuestionModel.class);
                            if (question != null) {
                                questionList.add(question);
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

        // Set question text and options
        binding.questionTextview.setText(question.getQuestion());
        binding.questionIndicatorTextview.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());
        binding.timerIndicatorTextview.setText("5:46");  // Set the timer or update as per your logic

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


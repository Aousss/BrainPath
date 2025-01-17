package com.example.brainpath.ui.assessment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
    private String quizTime;
    private List<QuestionModel> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private CountDownTimer countDownTimer;

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
            quizTime = getArguments().getString("quizTime");
            Toast.makeText(getContext(), quizId + " & " + quizTitle + " & " + quizTime, Toast.LENGTH_SHORT).show();
        }

        if (quizId == null || quizTitle == null) {
            Toast.makeText(getContext(), "Quiz data is missing!", Toast.LENGTH_SHORT).show();
            return binding.getRoot();
        }

        binding.quizTitleTextview.setText(quizTitle);

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
                        questionList.clear();
                        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                            QuestionModel question = questionSnapshot.getValue(QuestionModel.class);
                            if (question != null) {
                                questionList.add(question);
                            }
                        }

                        if (questionList.isEmpty()) {
                            binding.questionTextview.setText("No questions available for this quiz.");
                            binding.nextBtn.setVisibility(View.GONE);
                        } else {
                            loadQuestion();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to load quiz: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        binding.questionIndicatorTextview.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());

        // Set the maximum value for the progress indicator (once)
        if (binding.questionProgressIndicator.getMax() != questionList.size()) {
            binding.questionProgressIndicator.setMax(questionList.size());
        }

        // Update the progress value
        binding.questionProgressIndicator.setProgress(currentQuestionIndex + 1);


        binding.btn0.setText(question.getOptions().get(0));
        binding.btn1.setText(question.getOptions().get(1));
        binding.btn2.setText(question.getOptions().get(2));
        binding.btn3.setText(question.getOptions().get(3));

        resetButtonColors();
        binding.nextBtn.setEnabled(false); // Disable next button until an answer is selected

        // Cancel any previous timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Start a new timer with proper formatting for MM:SS
        countDownTimer = new CountDownTimer(Long.parseLong(quizTime) * 60 * 1000, 1000) { // Convert minutes to milliseconds
            @Override
            public void onTick(long millisUntilFinished) {
                long minutesRemaining = (millisUntilFinished / 1000) / 60;
                long secondsRemaining = (millisUntilFinished / 1000) % 60;
                String timeFormatted = String.format("%02d:%02d", minutesRemaining, secondsRemaining);
                binding.timerIndicatorTextview.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                showTimesUpDialog();
            }
        };

        countDownTimer.start();
    }

    private void showTimesUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Time's Up!")
                .setMessage("You ran out of time for this question.")
                .setCancelable(false)

                .setNegativeButton("End Quiz", (dialog, which) -> {
                    dialog.dismiss();
                    finishQuiz();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupListeners() {
        View.OnClickListener optionClickListener = v -> {
            String selectedAnswer = ((android.widget.Button) v).getText().toString();
            checkAnswer(selectedAnswer, v);
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

    private void checkAnswer(String selectedAnswer, View selectedButton) {
        // Reset all button colors to white
        resetButtonColors();
        QuestionModel question = questionList.get(currentQuestionIndex);
        // Highlight the selected button in blue
        selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.soft_blue_1));

        if (question.getCorrect().equals(selectedAnswer)) {
            score++;
        }
        binding.nextBtn.setEnabled(true); // Enable next button after answer selection
    }
    private void resetButtonColors() {
        binding.btn0.setBackgroundColor(Color.WHITE);
        binding.btn1.setBackgroundColor(Color.WHITE);
        binding.btn2.setBackgroundColor(Color.WHITE);
        binding.btn3.setBackgroundColor(Color.WHITE);
    }

    private void finishQuiz() {
        int totalQuestions = questionList.size();
        int percentage = (int) (((float) score / totalQuestions) * 100);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        ScoreDialogBinding dialogBinding = ScoreDialogBinding.inflate(inflater);

        dialogBinding.scoreProgressIndicator.setProgress(percentage);
        dialogBinding.scoreProgressText.setText(percentage + " %");

        if (percentage >= 60) {
            dialogBinding.scoreTitle.setText("Congrats! You have passed");
            dialogBinding.scoreTitle.setTextColor(ContextCompat.getColor(requireContext(),R.color.green));
        } else {
            dialogBinding.scoreTitle.setText("Oops! You have failed");
            dialogBinding.scoreTitle.setTextColor(Color.RED);
        }

        dialogBinding.scoreSubtitle.setText(score + " out of " + totalQuestions + " are correct");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        dialogBinding.finishBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_navigation_quizFragment_to_navigation_assessment);
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}
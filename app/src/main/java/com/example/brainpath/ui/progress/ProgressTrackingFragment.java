package com.example.brainpath.ui.progress;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProgressTrackingFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView motivationQuote, progressPercentageDisplay, progressBarMessage;
    private FirebaseFirestore db;
    private DocumentReference progressRef;

    private ProgressTrackingViewModel progressViewModel;
    private SubjectAdapter subjectAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.activity_progress_and_tracking, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        progressRef = db.collection("course").document("subject");

        progressViewModel = new ViewModelProvider(requireActivity()).get(ProgressTrackingViewModel.class);

        progressViewModel.getSubjectsList().observe(getViewLifecycleOwner(), subjects -> {
            // Update UI based on the shared data
        });

        // Bind views
        motivationQuote = rootView.findViewById(R.id.motivationQuote);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressPercentageDisplay = rootView.findViewById(R.id.progressPercentageDisplay);
        progressBarMessage = rootView.findViewById(R.id.progressBarMessage);

        // Navigation
        LinearLayout buttonProgressBar = rootView.findViewById(R.id.buttonProgressBar);
        buttonProgressBar.setOnClickListener(v -> navigateToYourProgress());

        LinearLayout buttonLearningGoals = rootView.findViewById(R.id.buttonLearningGoals);
        buttonLearningGoals.setOnClickListener(v -> navigateToLearningGoals());

        LinearLayout buttonReminder = rootView.findViewById(R.id.buttonReminder);
        buttonReminder.setOnClickListener(v -> navigateToReminder());

        // Fetch progress data
        fetchProgressData();

        return rootView;
    }

    private void navigateToYourProgress() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_progressTrackingFragment_to_progressFragment);
    }

    private void navigateToLearningGoals() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_progressTrackingFragment_to_learningFragment);
    }

    private void navigateToReminder() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_progressTrackingFragment_to_reminderFragment);
    }

    private void fetchProgressData() {
        progressRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("ProgressTrackingFragment", "Error fetching progress", e);
                Toast.makeText(requireContext(), "Error fetching progress", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Get progress percentage
                Long progress = snapshot.getLong("completion");
                if (progress != null) {
                    updateProgressUI(progress.intValue());
                } else {
                    Log.e("ProgressTrackingFragment", "Progress percentage is missing.");
                }

                // Get user name
                String userName = snapshot.getString("name");
                if (userName != null) {
                    updateMotivationQuote(userName);
                } else {
                    Log.e("ProgressTrackingFragment", "User name is missing.");
                }
            } else {
                Log.e("ProgressTrackingFragment", "No progress data found.");
            }
        });
    }

    private void updateProgressUI(int progress) {
        progressBar.setProgress(progress);
        progressPercentageDisplay.setText(progress + "%");

        if (progress < 50) {
            progressBarMessage.setText("Keep going, you're doing great!");
        } else if (progress < 80) {
            progressBarMessage.setText("You're more than halfway there!");
        } else if (progress < 100) {
            progressBarMessage.setText("Almost there, keep pushing!");
        } else {
            progressBarMessage.setText("Congratulations! You've achieved your goal!");
        }
    }

    private void updateMotivationQuote(String userName) {
        motivationQuote.setText("Keep it up, " + userName + "!");
    }
}

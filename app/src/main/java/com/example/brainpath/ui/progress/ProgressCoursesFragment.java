package com.example.brainpath.ui.progress;

import android.os.Bundle;
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

import com.example.brainpath.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProgressCoursesFragment extends Fragment {

    private TextView courseTitle;
    private TextView courseProgressText;
    private ProgressBar courseProgressBar;

    // Quiz performance views
    private ProgressBar quizProgressBar;
    private TextView percentageCorrectText;
    private TextView percentageWrongText;

    // Improvement topics container
    private LinearLayout improvementTopicsContainer;

    // Firebase instance
    private FirebaseFirestore db;

    private String courseName;
    private int courseProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.activity_progress_courses, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        courseTitle = rootView.findViewById(R.id.courseTitle);
        courseProgressText = rootView.findViewById(R.id.prgressPercentageDisplay);
        courseProgressBar = rootView.findViewById(R.id.progressBar);

        // Quiz performance progress
        quizProgressBar = rootView.findViewById(R.id.percentageCorrectWrongBar);
        percentageCorrectText = rootView.findViewById(R.id.percentageCorrectText);
        percentageWrongText = rootView.findViewById(R.id.percentageWrongText);

        // Improvement topics container
        improvementTopicsContainer = rootView.findViewById(R.id.improvementTopicsContainer);

        // Retrieve data passed via arguments
        if (getArguments() != null) {
            courseName = getArguments().getString("courseName");
            courseProgress = getArguments().getInt("courseProgress", 0);
        }

        // Set course title and progress
        courseTitle.setText(courseName);
        courseProgressText.setText(courseProgress + "%");
        courseProgressBar.setProgress(courseProgress);

        // Fetch quiz performance and improvement topics
        fetchQuizPerformance();
        fetchImprovementTopics();

        return rootView;
    }

    private void fetchQuizPerformance() {
        db.collection("course")
                .document("subject")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Long correct = document.getLong("quiz_correct");
                            Long wrong = document.getLong("quiz_wrong");

                            if (correct != null && wrong != null) {
                                int total = correct.intValue() + wrong.intValue();
                                int correctPercentage = (total > 0) ? (correct.intValue() * 100) / total : 0;
                                int wrongPercentage = 100 - correctPercentage;

                                quizProgressBar.setProgress(correctPercentage);
                                percentageCorrectText.setText(correctPercentage + "%");
                                percentageWrongText.setText(wrongPercentage + "%");
                            }
                        } else {
                            Toast.makeText(requireContext(), "Subject data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error getting quiz performance data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchImprovementTopics() {
        db.collection("course")
                .document("subject")
                .collection("improvement_topic")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                String topicName = document.getString("name");
                                int progress = document.getLong("progress").intValue();

                                createImprovementTopicView(topicName, progress);
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error getting improvement topics", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createImprovementTopicView(String topicName, int progress) {
        View improvementTopicView = LayoutInflater.from(getContext()).inflate(R.layout.item_improvement_topic, improvementTopicsContainer, false);

        TextView topicText = improvementTopicView.findViewById(R.id.topicText);
        ProgressBar topicProgressBar = improvementTopicView.findViewById(R.id.topicProgressBar);
        TextView topicProgressPercentage = improvementTopicView.findViewById(R.id.topicProgressPercentage);

        topicText.setText(topicName);
        topicProgressBar.setProgress(progress);
        topicProgressPercentage.setText(progress + "%");

        improvementTopicsContainer.addView(improvementTopicView);
    }
}

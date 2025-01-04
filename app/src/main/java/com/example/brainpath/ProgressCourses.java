package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProgressCourses extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_courses); // Make sure this matches your XML layout

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        courseTitle = findViewById(R.id.courseTitle);
        courseProgressText = findViewById(R.id.prgressPercentageDisplay);
        courseProgressBar = findViewById(R.id.progressBar);

        // Quiz performance progress
        quizProgressBar = findViewById(R.id.percentageCorrectWrongBar);
        percentageCorrectText = findViewById(R.id.percentageCorrectText);
        percentageWrongText = findViewById(R.id.percentageWrongText);

        // Improvement topics container
        improvementTopicsContainer = findViewById(R.id.improvementTopicsContainer);

        // Retrieve data passed from the previous activity
        Intent intent = getIntent();
        String courseName = intent.getStringExtra("courseName");
        int courseProgress = intent.getIntExtra("courseProgress", 0);

        // Set course title and progress
        courseTitle.setText(courseName);
        courseProgressText.setText(courseProgress + "%");
        courseProgressBar.setProgress(courseProgress);

        fetchQuizPerformance();
        // Fetch improvement topics data from Firestore
        fetchImprovementTopics();
    }

    private void fetchQuizPerformance() {
        // Navigate to the subject document in the specific course
        db.collection("course")
                .document("subject")  // Fetch the subject document (subject is a document, not a collection)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Fetch the correct and wrong fields directly from the subject document
                            Long correct = document.getLong("quiz_correct");
                            Long wrong = document.getLong("quiz_wrong");

                            if (correct != null && wrong != null) {
                                // Calculate percentages
                                int total = correct.intValue() + wrong.intValue();
                                int correctPercentage = (total > 0) ? (correct.intValue() * 100) / total : 0;
                                int wrongPercentage = 100 - correctPercentage;

                                // Update the quiz performance UI
                                quizProgressBar.setProgress(correctPercentage);
                                percentageCorrectText.setText(correctPercentage + "%");
                                percentageWrongText.setText(wrongPercentage + "%");
                            }
                        } else {
                            // Handle case when subject document does not exist or is missing data
                            Toast.makeText(this, "Subject data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle failure
                        Toast.makeText(this, "Error getting quiz performance data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchImprovementTopics() {
        // Fetch the improvement_topic subcollection under a specific subject document in a specific course
        db.collection("course")  // Navigate to the 'course' collection
                .document("subject")  // Fetch the document with the subjectId
                .collection("improvement_topic")  // Navigate to the 'improvement_topic' subcollection under the subject
                .get()  // Fetch all improvement topics for that subject
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // Iterate through the results to get the topics
                            for (DocumentSnapshot document : querySnapshot) {
                                String topicName = document.getString("name");
                                int progress = document.getLong("progress").intValue();

                                // Create a new view for each improvement topic
                                createImprovementTopicView(topicName, progress);
                            }
                        }
                    } else {
                        // Handle failure
                        Toast.makeText(this, "Error getting improvement topics", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void createImprovementTopicView(String topicName, int progress) {
        // Inflate the custom improvement topic layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View improvementTopicView = inflater.inflate(R.layout.item_improvement_topic, null);

        // Initialize views for the current improvement topic
        TextView topicText = improvementTopicView.findViewById(R.id.topicText);
        ProgressBar topicProgressBar = improvementTopicView.findViewById(R.id.topicProgressBar);
        TextView topicProgressPercentage = improvementTopicView.findViewById(R.id.topicProgressPercentage);

        // Set the data for the current topic
        topicText.setText(topicName);
        topicProgressBar.setProgress(progress);
        topicProgressPercentage.setText(progress + "%");

        // Add the new view to the container
        improvementTopicsContainer.addView(improvementTopicView);
    }
}

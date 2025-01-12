package com.example.brainpath.ui.progress;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.brainpath.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProgressCoursesListsFragment extends Fragment {
    // Firestore instance
    private FirebaseFirestore db;
    private LinearLayout courseContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.activity_progress_courses_lists, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the course container
        courseContainer = rootView.findViewById(R.id.courseContainer);

        // Update courses from Firestore
        updateCoursesFromFirebase();


        return rootView;
    }

    private void updateCoursesFromFirebase() {
        db.collection("course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("name");
                            int progress = document.getLong("completion").intValue();

                            // Match course titles and update their progress
                            if ("Mathematics".equalsIgnoreCase(title)) {
                                updateCourseProgress(R.id.firstProgessBar, R.id.firstPercentage, R.id.firstCourseTitle, progress, title);
                            } else if ("Science".equalsIgnoreCase(title)) {
                                updateCourseProgress(R.id.secondProgressBar, R.id.secondPercentage,R.id.secondTitle, progress, title);
                            } else if ("English".equalsIgnoreCase(title)) {
                                updateCourseProgress(R.id.thirdProgressBar, R.id.thirdPercentage,R.id.thirdTitle, progress, title);
                            }
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching courses", task.getException());
                    }
                });
    }

    private void updateCourseProgress(int progressBarId, int percentageId, int nameId, int progress, String title) {
        // Find and update the ProgressBar
        ProgressBar progressBar = courseContainer.findViewById(progressBarId);
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }

        // Update the course title
        TextView name = courseContainer.findViewById(nameId);
        if (name != null) {
            name.setText(title);
        }

        // Update the percentage TextView
        TextView percentage = courseContainer.findViewById(percentageId);
        if (percentage != null) {
            percentage.setText(progress + "%");
        }

        // Add click listener to navigate to another fragment
        View courseCard = (View) courseContainer.findViewById(progressBarId).getParent(); // Assuming parent is the course card
        if (courseCard != null) {
            courseCard.setOnClickListener(v -> navigateToSubjectDetails(title, progress));
        }
    }

    private void navigateToSubjectDetails(String subjectTitle, int progress) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

        // Create a Bundle to pass data
        Bundle bundle = new Bundle();
        bundle.putString("subjectTitle", subjectTitle);
        bundle.putInt("progress", progress);

        // Navigate to the target fragment with the Bundle
        navController.navigate(R.id.action_navigation_course_lists_to_navigation_progress_course, bundle);
    }


//    private void updateCourseProgress(int progressBarId, int percentageId, int nameId, int progress, String title) {
//        // Find and update the ProgressBar
//        ProgressBar progressBar = courseContainer.findViewById(progressBarId);
//        if (progressBar != null) {
//            progressBar.setProgress(progress);
//        }
//
//        TextView name = courseContainer.findViewById(nameId);
//        if (name != null) {
//            name.setText(title);
//        }
//
//        // Find and update the percentage TextView
//        TextView percentage = courseContainer.findViewById(percentageId);
//        if (percentage != null) {
//            percentage.setText(progress + "%");
//        }
//    }

}

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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.brainpath.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

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

        // Fetch courses from Firestore
        fetchCoursesFromFirebase();

        return rootView;
    }

    private void fetchCoursesFromFirebase() {
        db.collection("course") // Replace "course" with your actual collection name in Firestore
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("name");
                            int progress = document.getLong("completion").intValue();

                            // Create course data
                            HashMap<String, Object> course = createCourse(title, progress);

                            // Dynamically add course card
                            addCourseCard(courseContainer, course);
                        }
                    } else {
                        // Log error
                        Log.e("FirestoreError", "Error fetching courses", task.getException());
                    }
                });
    }

    private HashMap<String, Object> createCourse(String title, int progress) {
        HashMap<String, Object> course = new HashMap<>();
        course.put("title", title);
        course.put("progress", progress);
        return course;
    }

    private void addCourseCard(LinearLayout parent, HashMap<String, Object> course) {
        // Inflate course_card layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View courseCard = inflater.inflate(R.layout.course_card, parent, false);
        CardView cardView = courseCard.findViewById(R.id.cardView);

        // Set course title
        TextView courseTitle = courseCard.findViewById(R.id.courseTitle);
        courseTitle.setText((String) course.get("title"));

        // Set progress bar
        ProgressBar progressBar = courseCard.findViewById(R.id.progressBar);
        progressBar.setProgress((int) course.get("progress"));

        // Set percentage
        TextView percentage = courseCard.findViewById(R.id.percentage);
        percentage.setText(course.get("progress") + "%");

        // Retrieve course data
        String name = (String) course.get("title");
        int progress = (int) course.get("progress");

        // Set click listener for navigation
        cardView.setOnClickListener(v -> {
            // Navigate to ProgressCoursesFragment with arguments
            Bundle args = new Bundle();
            args.putString("courseName", name);
            args.putInt("courseProgress", progress);

            ProgressCoursesFragment progressCoursesFragment = new ProgressCoursesFragment();
            progressCoursesFragment.setArguments(args);

            // Replace the fragment (use your FragmentManager)
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, progressCoursesFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Add card to parent
        parent.addView(courseCard);
    }
}

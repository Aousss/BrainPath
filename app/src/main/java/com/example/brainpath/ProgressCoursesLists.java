package com.example.brainpath;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgressCoursesLists extends AppCompatActivity {
    // Firestore instance
    private FirebaseFirestore db;
    private LinearLayout courseContainer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_courses_lists);

//        // Dummy data: List of courses with progress
//        ArrayList<HashMap<String, Object>> registeredCourses = new ArrayList<>();
//        registeredCourses.add(createCourse("Mathematics", 65));
//        registeredCourses.add(createCourse("Science", 45));
//        registeredCourses.add(createCourse("English", 35));
//
//        // Dynamically add cards for registered courses
//        for (HashMap<String, Object> course : registeredCourses) {
//            addCourseCard(courseContainer, course);
//        }

        // Parent layout where cards will be added
        courseContainer = findViewById(R.id.courseContainer);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch courses from Firebase
        fetchCoursesFromFirebase();
    }

    private void fetchCoursesFromFirebase() {
        db.collection("course") // Replace "courses" with your actual collection name in Firestore
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
        LayoutInflater inflater = LayoutInflater.from(this);
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

        // Set data
        String name = (String) course.get("title");
        int progress = (int) course.get("progress");

        // Set click listener for navigation
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(ProgressCoursesLists.this, ProgressCourses.class);
            intent.putExtra("courseName", name);
            intent.putExtra("courseProgress", progress);
            startActivity(intent);
        });

        // Add card to parent
        parent.addView(courseCard);
    }
}
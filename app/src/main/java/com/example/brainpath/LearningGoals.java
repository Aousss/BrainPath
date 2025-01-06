package com.example.brainpath;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class LearningGoals extends AppCompatActivity {
    ArrayList<String> registeredSubjects = new ArrayList<>();
    LinearLayout subjectsLayout;
    FirebaseFirestore db; // Firestore instance
    TextView currentGoals, totalAchieved, average;
    // Variable for the bar chart
    BarChart barChart;
    // Variable for bar data set
    BarDataSet barDataSet;
    // ArrayList for storing bar entries
    ArrayList<BarEntry> barEntries;
    // Array to hold bar lengths (can be updated dynamically)
    float[] barLengths = {4, 6, 8, 2, 4, 1,24};
    // String array for displaying days
    String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_learning_goals);
        currentGoals=findViewById(R.id.goalHours);
        totalAchieved=findViewById(R.id.achieved);
        average=findViewById(R.id.average);
        subjectsLayout = findViewById(R.id.subjLayout);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        // Initialize the bar chart
        barChart = findViewById(R.id.barChart);
        // Fetch and update subjects dynamically
        fetchSubjectsFromFirebase();
        // Set up the bar chart
        setupBarChart();
        // Fetch barchart data from firebase
        fetchBarChartData();
        // Update the achieved times and average hours
        updateText();
    }

    private void fetchSubjectsFromFirebase() {
        // Clear existing views
        subjectsLayout.removeAllViews();

        db.collection("course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Subject> subjectsList = new ArrayList<>(); // List to hold subjects data

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String subjectName = document.getString("name");
                            int progress = document.getLong("progress").intValue();
                            String studyTime = document.getString("study_time");

                            // Parse study time into minutes or other comparable format for sorting
                            int studyTimeInMinutes = parseStudyTimeToMinutes(studyTime);

                            subjectsList.add(new Subject(subjectName, progress, studyTime, studyTimeInMinutes));
                        }

                        // Sort the subjects list by study time in descending order
                        subjectsList.sort((s1, s2) -> Integer.compare(s2.getStudyTimeInMinutes(), s1.getStudyTimeInMinutes()));

                        // Update the UI
                        for (Subject subject : subjectsList) {
                            updateSubjectView(subject.getName(), subject.getProgress(), subject.getFormattedStudyTime());
                        }
                    } else {
                        // Handle error
                        Log.e("FirestoreError", "Error fetching data", task.getException());
                    }
                });
    }

    private void updateSubjectView(String subjectName, int progress, String studyTime) {
        View subjectView = getLayoutInflater().inflate(R.layout.subject_item, subjectsLayout, false);

        TextView subjectNameView = subjectView.findViewById(R.id.subjectName);
        ProgressBar subjectProgress = subjectView.findViewById(R.id.subjectProgress);
        TextView studyTimeView = subjectView.findViewById(R.id.studyTime);

        subjectNameView.setText(subjectName);
        subjectProgress.setProgress(progress);
        studyTimeView.setText(studyTime);

        subjectsLayout.addView(subjectView); // Add to layout
    }
    private void updateText() {
        // Validate input goal
        String temp = currentGoals.getText().toString();
        if (temp.isEmpty()) {
            currentGoals.setError("Goal cannot be empty");
            return;
        }

        int goal;
        try {
            goal = Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            currentGoals.setError("Invalid goal format");
            return;
        }

        // Ensure barLengths has valid data
        if (barLengths.length < 7) {
            Log.e("UpdateText", "barLengths does not have 7 elements");
            totalAchieved.setText("0 Times");
            average.setText("0.0 Hours");
            return;
        }

        int count = 0;
        float total = 0;

        // Calculate total and count
        for (float barLength : barLengths) {
            total += barLength;
            if (barLength >= goal) {
                count++;
            }
        }

        // Calculate average
        float ave = total / barLengths.length;

        // Update UI
        totalAchieved.setText(count + " Times");
        average.setText(String.format("%.1f Hours", ave));
    }


    // Method to set up the bar chart
    private void setupBarChart() {
        // Removing the description label
        barChart.getDescription().setEnabled(false);

        // Getting the x-axis of the bar chart
        XAxis xAxis = barChart.getXAxis();

        // Setting value formatter to the x-axis
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));

        // Setting center axis labels for the bar chart
        xAxis.setCenterAxisLabels(false);

        // Setting the position of the x-axis to bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Setting granularity for the x-axis labels
        xAxis.setGranularity(1);

        // Enabling granularity for the x-axis
        xAxis.setGranularityEnabled(true);

        // Making the bar chart draggable
        barChart.setDragEnabled(true);

        // Setting the visible range for the bar chart
        barChart.setVisibleXRangeMaximum(3);
    }

    private void fetchBarChartData() {
        db.collection("time")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Float> barLengthsList = new ArrayList<>();
                        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

                        // Assuming only one document contains the week's data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            for (String day : daysOfWeek) {
                                if (document.contains(day)) {
                                    float studyHours = document.getDouble(day).floatValue();
                                    barLengthsList.add(studyHours);
                                } else {
                                    // Default to 0 if a day is missing in the document
                                    barLengthsList.add(0f);
                                }
                            }
                        }

                        // Convert list to array and update chart
                        float[] newBarLengths = new float[barLengthsList.size()];
                        for (int i = 0; i < barLengthsList.size(); i++) {
                            newBarLengths[i] = barLengthsList.get(i);
                        }

                        // Update the global barLengths and refresh the chart
                        barLengths = newBarLengths;
                        updateBarChart(barLengths);

                        // Call updateText() to refresh the total, average, and count
                        updateText();
                    } else {
                        // Handle error
                        Log.e("FirestoreError", "Error fetching bar chart data", task.getException());
                    }
                });
    }




    // Method to update the bar chart dynamically
    public void updateBarChart(float[] newBarLengths) {
        // Update the bar lengths
        barLengths = newBarLengths;

        // Create a new ArrayList for bar entries
        barEntries = new ArrayList<>();

        // Add entries to the bar chart
        for (int i = 0; i < barLengths.length; i++) {
            barEntries.add(new BarEntry(i , barLengths[i]));
        }

        // Create a new bar data set
        barDataSet = new BarDataSet(barEntries, "Learning Goals Progress");
        barDataSet.setColor(getApplicationContext().getResources().getColor(R.color.light_blue));

        // Create a new BarData object and set it to the chart
        BarData data = new BarData(barDataSet);

        // Setting the width of the bars
        data.setBarWidth(0.4f);

        // Set the data to the chart
        barChart.setData(data);

        // Refresh the chart
        barChart.invalidate();
    }
    // Helper method to parse study time string into minutes (or another comparable format)
    private int parseStudyTimeToMinutes(String studyTime) {
        // Example: "2h 30m" -> 150 minutes
        int totalMinutes = 0;
        if (studyTime.contains("h")) {
            String[] parts = studyTime.split("h");
            totalMinutes += Integer.parseInt(parts[0].trim()) * 60; // Convert hours to minutes
            if (parts.length > 1 && parts[1].contains("m")) {
                totalMinutes += Integer.parseInt(parts[1].replace("m", "").trim()); // Add minutes
            }
        } else if (studyTime.contains("m")) {
            totalMinutes += Integer.parseInt(studyTime.replace("m", "").trim());
        }
        return totalMinutes;
    }
}

class Subject {
    private String name;
    private int progress;
    private String formattedStudyTime;
    private int studyTimeInMinutes;

    public Subject(String name, int progress, String formattedStudyTime, int studyTimeInMinutes) {
        this.name = name;
        this.progress = progress;
        this.formattedStudyTime = formattedStudyTime;
        this.studyTimeInMinutes = studyTimeInMinutes;
    }

    public String getName() {
        return name;
    }

    public int getProgress() {
        return progress;
    }

    public String getFormattedStudyTime() {
        return formattedStudyTime;
    }

    public int getStudyTimeInMinutes() {
        return studyTimeInMinutes;
    }
}
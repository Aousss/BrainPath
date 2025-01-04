package com.example.brainpath;

import android.os.Bundle;
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

import java.util.ArrayList;

public class LearningGoals extends AppCompatActivity {

    ArrayList<String> registeredSubjects = new ArrayList<>();
    LinearLayout subjectsLayout;

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
        //FIREBASEEEEEEEEEEEEEEEEEEEEEEEEE THISSSSSSSSSSSSSSSSSSSSSSSS
        registeredSubjects.add("Mathematics");
        registeredSubjects.add("Science");
        registeredSubjects.add("English");

//        setupSubjectLayout();
        updateSubjects();

        // Initialize the bar chart
        barChart = findViewById(R.id.barChart);

        // Set up the bar chart
        setupBarChart();

        // Populate the chart with initial data
        updateBarChart(barLengths);

        // Update the achieved times and average hours
        updateText();
    }

    private void updateText(){
        String temp = currentGoals.getText().toString();
        int goal = Integer.parseInt(temp);
        int count=0;
        float ave=0;
        float total=0;
        for(int i=0;i<7;i++){
            total+=barLengths[i];
        }
        ave=total/7;
        for(int i=0;i<7;i++){
            if(barLengths[i]>=goal){
                count++;
            }
        }
        totalAchieved.setText(count+" Times");
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

    // Method to update the bar chart dynamically FIREBASEEEEEEEEEEEEEEEE THISSSSSSSSSSSSSSSSSSSSSSs
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

//    public void setupSubjectLayout(){
//        for (String subject : registeredSubjects) {
//            View subjectView = getLayoutInflater().inflate(R.layout.subject_item, null);
//
//            TextView subjectName = subjectView.findViewById(R.id.subjectName);
//            ProgressBar subjectProgress = subjectView.findViewById(R.id.subjectProgress);
//            TextView studyTime = subjectView.findViewById(R.id.studyTime);
//
//            // Set subject details
//            subjectName.setText(subject);
//            int progress = getSubjectProgress(subject); // Custom method to fetch progress
//            subjectProgress.setProgress(progress);
//            studyTime.setText(getStudyTime(subject)); // Custom method to fetch study time
//
//            // Add the view to the layout
//            subjectsLayout.addView(subjectView);
//        }
//    }

    private void updateSubjects() {
        // Clear existing views
        subjectsLayout.removeAllViews();

        // Dynamically add views for each registered subject
        for (String subject : registeredSubjects) {
            // Inflate the correct layout for a subject item
            View subjectView = getLayoutInflater().inflate(R.layout.subject_item, subjectsLayout, false);

            // Find the views in the inflated layout
            TextView subjectName = subjectView.findViewById(R.id.subjectName);
            ProgressBar subjectProgress = subjectView.findViewById(R.id.subjectProgress);
            TextView studyTime = subjectView.findViewById(R.id.studyTime);

            // Set dynamic values for the subject
            subjectName.setText(subject);
            int progress = getSubjectProgress(subject); // Custom method to fetch progress
            subjectProgress.setProgress(progress);
            studyTime.setText(getStudyTime(subject)); // Custom method to fetch study time

            // Add the inflated view to the subjects layout
            subjectsLayout.addView(subjectView);
        }
    }

    //FIREBASEEEEEEEEEEEEEEEE THISSSSSSSSSSSSSSSSSSSSSSSS
    private String getStudyTime(String subject) {
        // Example: Return dummy data
        switch (subject) {
            case "Mathematics": return "4h 10m";
            case "Science": return "3h 11m";
            case "English": return "3h 12m";
            default: return "0h 0m";
        }
    }
    //FIREBASEEEEEEEEEEEEEEEE THISSSSSSSSSSSSSSSSSSSSSSSS
    private int getSubjectProgress(String subject) {
        // Example: Return dummy progress
        switch (subject) {
            case "Mathematics": return 75; // 75% progress
            case "Science": return 50;    // 50% progress
            case "English": return 40;    // 40% progress
            default: return 0;
        }
    }
    public void removeSubject(String subject) {
        registeredSubjects.remove(subject);
        updateSubjects(); // Refresh the UI
    }
}

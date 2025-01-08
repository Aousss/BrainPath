package com.example.brainpath.ui.progress;

import com.example.brainpath.ui.progress.Subject;

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

import com.example.brainpath.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class LearningGoalsFragment extends Fragment {

    private ArrayList<String> registeredSubjects = new ArrayList<>();
    private LinearLayout subjectsLayout;
    private FirebaseFirestore db;
    private TextView currentGoals, totalAchieved, average;

    private BarChart barChart;
    private BarDataSet barDataSet;
    private ArrayList<BarEntry> barEntries;
    private float[] barLengths = {4, 6, 8, 2, 4, 1, 24};
    private String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.activity_progress_learning_goals, container, false);

        currentGoals = rootView.findViewById(R.id.goalHours);
        totalAchieved = rootView.findViewById(R.id.achieved);
        average = rootView.findViewById(R.id.average);
        subjectsLayout = rootView.findViewById(R.id.subjLayout);
        barChart = rootView.findViewById(R.id.barChart);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch and update subjects dynamically
        fetchSubjectsFromFirebase();
        setupBarChart();
        fetchBarChartData();
        updateText();

        return rootView;
    }

    private void fetchSubjectsFromFirebase() {
        subjectsLayout.removeAllViews();
        db.collection("course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Subject> subjectsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String subjectName = document.getString("name");
                            int progress = document.getLong("progress").intValue();
                            String studyTime = document.getString("study_time");
                            int studyTimeInMinutes = parseStudyTimeToMinutes(studyTime);
                            subjectsList.add(new Subject(subjectName, progress, studyTime, studyTimeInMinutes));
                        }

                        subjectsList.sort((s1, s2) -> Integer.compare(s2.getStudyTimeInMinutes(), s1.getStudyTimeInMinutes()));

                        for (Subject subject : subjectsList) {
                            updateSubjectView(subject.getName(), subject.getProgress(), subject.getFormattedStudyTime());
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching data", task.getException());
                    }
                });
    }

    private void updateSubjectView(String subjectName, int progress, String studyTime) {
        View subjectView = LayoutInflater.from(getContext()).inflate(R.layout.subject_item, subjectsLayout, false);

        TextView subjectNameView = subjectView.findViewById(R.id.subjectName);
        ProgressBar subjectProgress = subjectView.findViewById(R.id.subjectProgress);
        TextView studyTimeView = subjectView.findViewById(R.id.studyTime);

        subjectNameView.setText(subjectName);
        subjectProgress.setProgress(progress);
        studyTimeView.setText(studyTime);

        subjectsLayout.addView(subjectView);
    }

    private void updateText() {
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

        if (barLengths.length < 7) {
            Log.e("UpdateText", "barLengths does not have 7 elements");
            totalAchieved.setText("0 Times");
            average.setText("0.0 Hours");
            return;
        }

        int count = 0;
        float total = 0;
        for (float barLength : barLengths) {
            total += barLength;
            if (barLength >= goal) {
                count++;
            }
        }

        float ave = total / barLengths.length;
        totalAchieved.setText(count + " Times");
        average.setText(String.format("%.1f Hours", ave));
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);
    }

    private void fetchBarChartData() {
        db.collection("time")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Float> barLengthsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            for (String day : days) {
                                if (document.contains(day)) {
                                    float studyHours = document.getDouble(day).floatValue();
                                    barLengthsList.add(studyHours);
                                } else {
                                    barLengthsList.add(0f);
                                }
                            }
                        }

                        float[] newBarLengths = new float[barLengthsList.size()];
                        for (int i = 0; i < barLengthsList.size(); i++) {
                            newBarLengths[i] = barLengthsList.get(i);
                        }

                        barLengths = newBarLengths;
                        updateBarChart(barLengths);
                        updateText();
                    } else {
                        Log.e("FirestoreError", "Error fetching bar chart data", task.getException());
                    }
                });
    }

    private void updateBarChart(float[] newBarLengths) {
        barLengths = newBarLengths;
        barEntries = new ArrayList<>();
        for (int i = 0; i < barLengths.length; i++) {
            barEntries.add(new BarEntry(i, barLengths[i]));
        }

        barDataSet = new BarDataSet(barEntries, "Learning Goals Progress");
        barDataSet.setColor(requireContext().getResources().getColor(R.color.light_blue));
        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.4f);
        barChart.setData(data);
        barChart.invalidate();
    }

    private int parseStudyTimeToMinutes(String studyTime) {
        int totalMinutes = 0;
        if (studyTime.contains("h")) {
            String[] parts = studyTime.split("h");
            totalMinutes += Integer.parseInt(parts[0].trim()) * 60;
            if (parts.length > 1 && parts[1].contains("m")) {
                totalMinutes += Integer.parseInt(parts[1].replace("m", "").trim());
            }
        } else if (studyTime.contains("m")) {
            totalMinutes += Integer.parseInt(studyTime.replace("m", "").trim());
        }
        return totalMinutes;
    }
}

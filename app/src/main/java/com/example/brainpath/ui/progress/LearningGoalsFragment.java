package com.example.brainpath.ui.progress;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

import com.example.brainpath.ui.progress.Subject;

public class LearningGoalsFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView subjectsRecyclerView;
    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectsList;

    private BarChart barChart;
    private BarDataSet barDataSet;
    private ArrayList<BarEntry> barEntries;
    private float[] barLengths = {4, 6, 8, 2, 4, 1, 24};
    private String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_progress_learning_goals, container, false);

        db = FirebaseFirestore.getInstance();
        subjectsRecyclerView = rootView.findViewById(R.id.subjectsRecyclerView);
        barChart = rootView.findViewById(R.id.barChart);

        subjectsList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjectsList);
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        subjectsRecyclerView.setAdapter(subjectAdapter);

        fetchSubjectsFromFirebase();
        setupBarChart();
        fetchBarChartData();

        return rootView;
    }

    private void fetchSubjectsFromFirebase() {
        db.collection("course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        subjectsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String subjectName = document.getString("name");
                            int progress = document.getLong("progress").intValue();
                            String studyTime = document.getString("study_time");
                            int studyTimeInMinutes = parseStudyTimeToMinutes(studyTime);

                            subjectsList.add(new Subject(subjectName, progress, studyTime, studyTimeInMinutes));
                        }

                        subjectAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreError", "Error fetching data", task.getException());
                    }
                });
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
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
                        barEntries = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            for (String day : days) {
                                if (document.contains(day)) {
                                    float studyHours = document.getDouble(day).floatValue();
                                    barEntries.add(new BarEntry(barEntries.size(), studyHours));
                                }
                            }
                        }

                        barDataSet = new BarDataSet(barEntries, "Learning Goals Progress");
                        BarData data = new BarData(barDataSet);
                        data.setBarWidth(0.4f);
                        barChart.setData(data);
                        barChart.invalidate();
                    } else {
                        Log.e("FirestoreError", "Error fetching bar chart data", task.getException());
                    }
                });
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

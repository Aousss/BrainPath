package com.example.brainpath.ui.progress;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.brainpath.ui.progress.Subject;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProgressTrackingViewModel extends ViewModel {

    private final MutableLiveData<List<Subject>> subjectsList = new MutableLiveData<>();
    private final MutableLiveData<List<Reminder>> remindersList = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Subject>> getSubjectsList() {
        return subjectsList;
    }

    public LiveData<List<Reminder>> getRemindersList() {
        return remindersList;
    }

    public void fetchSubjects() {
        db.collection("course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Subject> subjects = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String subjectName = document.getString("name");
                            int progress = document.getLong("progress").intValue();
                            String studyTime = document.getString("study_time");
                            int studyTimeInMinutes = parseStudyTimeToMinutes(studyTime);

                            subjects.add(new Subject(subjectName, progress, studyTime, studyTimeInMinutes));
                        }
                        subjectsList.setValue(subjects);
                    }
                });
    }

    public void fetchReminders() {
        db.collection("reminder")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Reminder> reminders = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String date = document.getString("date");
                            reminders.add(new Reminder(title, date));
                        }
                        remindersList.setValue(reminders);
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

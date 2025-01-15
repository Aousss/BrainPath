package com.example.brainpath.ui.assessment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.brainpath.R;

public class AssessmentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_assessment_diagnostic, container, false);

        view.findViewById(R.id.mathCard).setOnClickListener(v -> navigateToQuizList("MATH"));
        view.findViewById(R.id.scienceCard).setOnClickListener(v -> navigateToQuizList("SCIENCE"));
        view.findViewById(R.id.englishCard).setOnClickListener(v -> navigateToQuizList("ENGLISH"));

        return view;
    }

    private void navigateToQuizList(String subject) {
        Intent intent = new Intent(getContext(), MainQuizActivity.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }
}

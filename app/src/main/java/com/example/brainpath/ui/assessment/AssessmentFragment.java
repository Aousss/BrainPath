package com.example.brainpath.ui.assessment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.brainpath.R;

public class AssessmentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_assessment_diagnostic, container, false);

        // Set click listeners for each card
        view.findViewById(R.id.mathCard).setOnClickListener(v -> navigateToQuizList("MATH"));
        view.findViewById(R.id.scienceCard).setOnClickListener(v -> navigateToQuizList("SCIENCE"));
        view.findViewById(R.id.englishCard).setOnClickListener(v -> navigateToQuizList("ENGLISH"));

        // Add click listener for the ImageView
        ImageView assessmentResult = view.findViewById(R.id.assessmentResult);
        assessmentResult.setOnClickListener(v -> {
            // Navigate to the ResultFragment
            Navigation.findNavController(view).navigate(R.id.action_assessmentFragment_to_resultFragment);
        });

        return view;
    }

    // This method navigates to MainQuizFragment with the subject as an argument
    private void navigateToQuizList(String subject) {
        // Create a bundle to pass the subject to the next fragment
        Bundle bundle = new Bundle();
        bundle.putString("subject", subject);

        // Use the NavController to navigate to MainQuizFragment
        Navigation.findNavController(getView()).navigate(R.id.action_assessmentFragment_to_mainQuizFragment, bundle);
    }
}

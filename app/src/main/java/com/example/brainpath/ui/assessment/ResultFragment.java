package com.example.brainpath.ui.assessment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.brainpath.databinding.FragmentResultBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;
    private ResultAdapter adapter;
    private List<QuizResult> resultList = new ArrayList<>();

    private String currentUserId; // User ID from Firebase Auth

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);

        // Get the current user's ID from Firebase Auth
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        binding.resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ResultAdapter(resultList);
        binding.resultRecyclerView.setAdapter(adapter);

        // Load results from Firebase
        loadResults();
    }

    private void loadResults() {
        // Get the current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://brightpath-d4c0c-ef61d.asia-southeast1.firebasedatabase.app/")
                    .getReference("quiz_results").child(userId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    resultList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        QuizResult result = dataSnapshot.getValue(QuizResult.class);
                        if (result != null) {
                            resultList.add(result); // Add the result to the list
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ResultFragment", "Database Error: " + error.getMessage());
                }
            });
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

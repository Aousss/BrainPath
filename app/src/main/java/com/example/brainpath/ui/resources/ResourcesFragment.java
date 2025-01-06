package com.example.brainpath.ui.resources;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brainpath.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ResourcesFragment extends Fragment {

    private RecyclerView resourcesRecyclerView;
    private ResourcesAdapter adapter;
    private FirebaseFirestore db;
    private ResourcesViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(ResourcesViewModel.class);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_resources, container, false);

        // Initialize RecyclerView
        resourcesRecyclerView = view.findViewById(R.id.resourcesActivity_recyclerView);
        resourcesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter
        adapter = new ResourcesAdapter(getContext(), new ArrayList<>(), resource -> {
            // Navigate to DetailsFragment with arguments
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            Bundle args = new Bundle();
            args.putString("RESOURCE_TITLE", resource.getResTitle());
            args.putString("RESOURCE_DESC", resource.getResDesc());
            args.putString("RESOURCE_PREVIEW_URL", resource.getResPreview());
            navController.navigate(R.id.action_resourcesFragment_to_detailsFragment, args);
        });
        resourcesRecyclerView.setAdapter(adapter);

        // Set "All" as default selection
        RadioButton radioAll = view.findViewById(R.id.radioAll);
        radioAll.setChecked(true);  // Select "All" by default

        // Display all resources initially
        displayAllResources();


        // Observe resources in ViewModel
        viewModel.getResources().observe(getViewLifecycleOwner(), resources -> {
            if (resources != null) {
                adapter.updateResources(resources);
            }
        });

        // Apply the filtered functions
        RadioGroup topicRadioGroup = view.findViewById(R.id.topicRadioGroup);
        topicRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioAll) {
                displayAllResources();
            } else {
                int selectedTopicID = 0;
                if (checkedId == R.id.radioMath) {
                    selectedTopicID = 1;
                } else if (checkedId == R.id.radioScience) {
                    selectedTopicID = 2;
                } else if (checkedId == R.id.radioEnglish) {
                    selectedTopicID = 3;
                }
                applyFilter(selectedTopicID);
            }
        });

        fetchResources();

        return view;
    }

    private void applyFilter(int topicID) {
        List<Resources> filteredResources = new ArrayList<>();
        for (Resources resource : viewModel.getResources().getValue()) {
            if (resource.getResID() == topicID) {
                filteredResources.add(resource);
            }
        }
        adapter.updateResources(filteredResources);
    }

    private void displayAllResources() {
        List<Resources> allResources = viewModel.getResources().getValue();
        adapter.updateResources(allResources);
    }

    private void fetchResources() {
        db.collection("resources")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Resources> resourcesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Resources resource = new Resources(
                                    document.getString("title"),
                                    document.getString("desc"),
                                    document.getString("fullDesc"),
                                    document.getString("preview"),
                                    document.getLong("resID").intValue()
                            );
                            resourcesList.add(resource);
                        }
                        viewModel.setResources(resourcesList);
                    } else {
                        Log.e("ResourcesFragment", "Error fetching resources", task.getException());
                    }
                });
    }
}

package com.example.brainpath.ui.resources;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    private List<Resources> resourceList;
    private FirebaseFirestore db;
    private ResourcesViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Initialize ViewModel to persist data
        viewModel = new ViewModelProvider(requireActivity()).get(ResourcesViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_resources, container, false);

        // Initialize RecyclerView
        resourcesRecyclerView = view.findViewById(R.id.resourcesActivity_recyclerView);
        resourcesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter and attach to RecyclerView
        if (resourceList == null) {
            resourceList = new ArrayList<>();
        }
        if (adapter == null) {
            adapter = new ResourcesAdapter(getContext(), resourceList, resource -> {
                // Navigate to details fragment
                ResourceDetailsFragment detailsFragment = new ResourceDetailsFragment();
                Bundle args = new Bundle();
                args.putString("RESOURCE_TITLE", resource.getResTitle());
                args.putString("RESOURCE_DESC", resource.getResDesc()); // Use desc for ResourceDetailsFragment
                args.putString("RESOURCE_PREVIEW_URL", resource.getResPreview());
                detailsFragment.setArguments(args);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, detailsFragment)
                        .addToBackStack(null)
                        .commit();
            });

        }
        resourcesRecyclerView.setAdapter(adapter);

        // Fetch resources if needed
        if (resourceList.isEmpty()) {
            fetchResources();
        }

        return view;
    }

    private void openResource(Resources resource) {
        Intent intent = new Intent(getContext(), ResourceDetailsFragment.class);
        intent.putExtra("RESOURCE_TITLE", resource.getResTitle());
        intent.putExtra("RESOURCE_DESC", resource.getResDesc());
        intent.putExtra("RESOURCE_PREVIEW_URL", resource.getResPreview());
        startActivity(intent);
    }

    private void fetchResources() {
        if (db == null) {
            Log.e("ResourcesFragment", "Firestore instance is null");
            return;
        }

        Log.d("ResourcesFragment", "Fetching resources from Firestore...");

        db.collection("resources")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ResourcesFragment", "Resources fetch successful");
                        resourceList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("ResourcesFragment", "Document data: " + document.getData());

                            Resources resource = new Resources(
                                    document.getString("title"),
                                    document.getString("desc"),        // Use short desc for details
                                    document.getString("fullDesc"),   // Use fullDesc for list
                                    document.getString("preview")
                            );
                            resourceList.add(resource);

                        }

                        // Update ViewModel with the fetched data
                        viewModel.setResources(resourceList);

                        // Notify adapter about the changes
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("ResourcesFragment", "Error fetching resources", task.getException());
                    }
                });
    }

}


package com.example.brainpath.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.brainpath.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;

    private TextView nameTextView, emailTextView;
    private ImageView profileImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        nameTextView = view.findViewById(R.id.textView);
        emailTextView = view.findViewById(R.id.textView2);
        profileImageView = view.findViewById(R.id.imageView2);

        // Set user information
        if (currentUser != null) {
            nameTextView.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Name");
            emailTextView.setText(currentUser.getEmail());
            fetchProfileImage();
        }

        // Navigate to SettingsFragment
        view.findViewById(R.id.button).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_profile_to_navigation_settings);
        });

        // Handle Logout
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void fetchProfileImage() {
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        // Retrieve the profile image URL from Firestore
        firebaseFirestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            // Load the image using Glide
                            Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.profile) // Default placeholder
                                    .into(profileImageView);
                        } else {
                            // If no image URL, load the default image
                            profileImageView.setImageResource(R.drawable.profile);
                        }
                    } else {
                        // No user document exists, load default image
                        profileImageView.setImageResource(R.drawable.profile);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    profileImageView.setImageResource(R.drawable.profile);
                });
    }
}

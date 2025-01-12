package com.example.brainpath.ui.profile;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.brainpath.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private EditText currentPassword, newPassword;
    private TextView showCurrentPassword, showNewPassword;
    private Button saveNewPassword;

    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        currentPassword = view.findViewById(R.id.currentPassword);
        newPassword = view.findViewById(R.id.newPassword);
        showCurrentPassword = view.findViewById(R.id.showCurrentPassword);
        showNewPassword = view.findViewById(R.id.showNewPassword);
        saveNewPassword = view.findViewById(R.id.saveNewPassword);

        // Handle password visibility toggle
        setupPasswordVisibilityToggle(showCurrentPassword, currentPassword);
        setupPasswordVisibilityToggle(showNewPassword, newPassword);

        // Handle save new password logic
        saveNewPassword.setOnClickListener(v -> validateAndChangePassword());

        return view;
    }

    private void setupPasswordVisibilityToggle(TextView toggle, EditText passwordField) {
        toggle.setOnClickListener(v -> {
            if (passwordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
                passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                toggle.setText("hide");
            } else {
                passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                toggle.setText("show");
            }
            passwordField.setSelection(passwordField.getText().length());
        });
    }

    private void validateAndChangePassword() {
        String currentPasswordText = currentPassword.getText().toString().trim();
        String newPasswordText = newPassword.getText().toString().trim();

        if (currentPasswordText.isEmpty() || newPasswordText.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = user.getEmail();
        if (userEmail == null) {
            Toast.makeText(getContext(), "Failed to retrieve user email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reauthenticate user with current password
        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPasswordText);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Check if the new password meets criteria
                if (newPasswordText.length() < 6) {
                    Toast.makeText(getContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update password
                user.updatePassword(newPasswordText).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                        // Navigate to another page or perform additional actions
                    } else {
                        Toast.makeText(getContext(), "Failed to update password: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Reauthentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}

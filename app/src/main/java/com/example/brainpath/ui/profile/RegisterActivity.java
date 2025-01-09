package com.example.brainpath.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.brainpath.MainActivity;
import com.example.brainpath.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    // Firestore instance
    private FirebaseFirestore db;

    // Google Sign-In client
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100; // Request code for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // REMOVE THE TOP ACTION BAR
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("878637047128-dgtagtba3s9i0n9kbr5eevlipbmmevjl.apps.googleusercontent.com") // Web Client ID
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // UI Components
        EditText usernameField = findViewById(R.id.usernameField);
        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);
        EditText confirmPasswordField = findViewById(R.id.conformPasswordField);
        TextView showPassword = findViewById(R.id.showPassword);
        TextView showConfirmPassword = findViewById(R.id.showConfirmPassword);
        Button registerButton = findViewById(R.id.registerBtn);
        Button googleSignInButton = findViewById(R.id.googleSignInButton);
        TextView signInNow = findViewById(R.id.signinNow);

        // Toggle Password Visibility
        showPassword.setOnClickListener(v -> togglePasswordVisibility(passwordField, showPassword));
        showConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordField, showConfirmPassword));

        // Handle Register Button Click
        registerButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                checkUsernameUniquenessAndRegister(username, email, password);
            }
        });

        // Handle Google Sign-In Button Click
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        // Navigate to SignInActivity
        signInNow.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Toggle Password Visibility
    private void togglePasswordVisibility(EditText passwordField, TextView toggleButton) {
        if (passwordField.getTransformationMethod() == null) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleButton.setText("Show");
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordField.setTransformationMethod(null);
            toggleButton.setText("Hide");
        }
        passwordField.setSelection(passwordField.length());
    }

    // Check Username Uniqueness Before Registration
    private void checkUsernameUniquenessAndRegister(String username, String email, String password) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        registerUser(username, email, password);
                    } else {
                        Toast.makeText(this, "Username is already taken. Please choose another one.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void registerUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("email", email);
                            userData.put("friendIds", new ArrayList<>());

                            db.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        navigateToMainActivity();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Google Sign-In Logic
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult();
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkAndPromptForUsername(user);
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndPromptForUsername(FirebaseUser user) {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        if (isDuplicateUsername(username, user.getUid())) {
                            promptForUsername(user); // Prompt if duplicate
                        } else {
                            navigateToMainActivity();
                        }
                    } else {
                        promptForUsername(user); // Prompt if no username
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean isDuplicateUsername(String username, String currentUid) {
        // Simulated logic for duplication detection (implement server-side query if needed)
        return false;
    }

    private void promptForUsername(FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Your Username");

        EditText input = new EditText(this);
        input.setHint("Enter username");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String username = input.getText().toString().trim();
            if (!username.isEmpty()) {
                checkUsernameUniquenessAndSave(user, username);
            } else {
                Toast.makeText(this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                promptForUsername(user);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(this, "Username is required.", Toast.LENGTH_SHORT).show();
            promptForUsername(user);
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void checkUsernameUniquenessAndSave(FirebaseUser user, String username) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        saveUsernameToFirestore(user, username);
                    } else {
                        Toast.makeText(this, "Username is already taken. Please choose another one.", Toast.LENGTH_SHORT).show();
                        promptForUsername(user);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveUsernameToFirestore(FirebaseUser user, String username) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", user.getEmail());
        userData.put("friendIds", new ArrayList<>());

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Username saved successfully.", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

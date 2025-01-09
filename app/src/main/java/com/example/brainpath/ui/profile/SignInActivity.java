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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private GoogleSignInClient googleSignInClient; // Google Sign-In client
    private static final int RC_SIGN_IN = 100; // Request code for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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

        // Initialize UI elements
        Button loginBtn = findViewById(R.id.signInButton);
        Button googleSignInBtn = findViewById(R.id.googleSignInButton);
        EditText emailField = findViewById(R.id.usernameField);
        EditText passwordField = findViewById(R.id.passwordField);
        TextView showPassword = findViewById(R.id.showPassword);
        TextView registerNow = findViewById(R.id.registerNow); // "Join Now" TextView
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        // Login Button Logic
        loginBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Google Sign-In Button Logic
        googleSignInBtn.setOnClickListener(v -> signOutAndSignInWithGoogle());

        // Toggle Password Visibility
        showPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPassword.setText("Show");
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordField.setTransformationMethod(null);
                showPassword.setText("Hide");
            }
            passwordField.setSelection(passwordField.length());
            isPasswordVisible = !isPasswordVisible;
        });

        // Forgot Password Logic
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPassword.class);
            startActivity(intent);
        });

        // Navigate to RegisterActivity when "Join Now" is clicked
        registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Handle Firebase Email and Password Login
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(SignInActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Sign out from Google Sign-In to display the account picker dialog
    private void signOutAndSignInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            // After signing out, prompt the user to choose a Google account
            signInWithGoogle();
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("username")) {
                        // Username exists, navigate to MainActivity
                        navigateToMainActivity();
                    } else {
                        // Username is missing, prompt the user to set one
                        promptForUsername(user);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void promptForUsername(FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Your Username");

        // Input field for username
        EditText input = new EditText(this);
        input.setHint("Enter your username");
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
            Toast.makeText(this, "Username is required to proceed.", Toast.LENGTH_SHORT).show();
            promptForUsername(user);
        });

        builder.setCancelable(false); // Prevent dismissal
        builder.show();
    }

    private void checkUsernameUniquenessAndSave(FirebaseUser user, String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", user.getEmail());

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Username saved successfully.", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

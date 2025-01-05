package com.example.module2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.module2.databinding.ActivityLoginBinding;
import com.example.module2.utilities.Constant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseFirestore database;
    private com.example.module2.PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();

        // Check if user is already signed in
        if (preferenceManager.getBoolean(Constant.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        }

        setListeners();
    }

    private void setListeners() {
        binding.registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
        });

        binding.btnlogin.setOnClickListener(v -> {
            if (isValidSignDetails()) {
                signInWithFirestore();
            }
        });
    }

    private void signInWithFirestore() {
        loading(true);

        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        // Query Firestore for the user
        database.collection(Constant.KEY_COLLECTION_USER)
                .whereEqualTo(Constant.KEY_EMAIL, email)
                .whereEqualTo(Constant.KEY_PASSWORD, password)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null &&
                            task.getResult().getDocuments().size() > 0) {
                        // Retrieve user data
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constant.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constant.KEY_NAME, documentSnapshot.getString(Constant.KEY_NAME));
                        preferenceManager.putString(Constant.KEY_IMAGE, documentSnapshot.getString(Constant.KEY_IMAGE));

                        // Navigate to MainActivity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        showToast("Invalid email or password");
                    }
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showToast("Something went wrong: " + e.getMessage());
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.btnlogin.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnlogin.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignDetails() {
        if (binding.email.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (binding.password.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else {
            return true;
        }
    }
}

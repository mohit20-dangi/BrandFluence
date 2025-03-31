package com.example.brandfluenceandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView loginErrorTextView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        loginErrorTextView = findViewById(R.id.loginErrorTextView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        loginButton.setOnClickListener(v -> {
            loginErrorTextView.setVisibility(TextView.GONE);
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Enter a valid email.");
                return;
            }
            if (password.isEmpty()) {
                showError("Please enter your password.");
                return;
            }

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        progressDialog.show();
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        checkUserTypeAndRedirect(user.getUid());
                    } else {
                        progressDialog.dismiss();
                        showError("User not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showError("Login failed: " + e.getMessage());
                });
    }

    private void checkUserTypeAndRedirect(String uid) {
        db.collection("Influencers").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        progressDialog.dismiss();
                        redirectToDashboard(InfluencerDashboardActivity.class);
                    } else {
                        checkBrandType(uid);
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showError("Error checking user type: " + e.getMessage());
                });
    }

    private void checkBrandType(String uid) {
        db.collection("Brands").document(uid).get()
                .addOnSuccessListener(brandDoc -> {
                    progressDialog.dismiss();
                    if (brandDoc.exists()) {
                        redirectToDashboard(BrandDashboardActivity.class);
                    } else {
                        showError("User type not found.");
                        FirebaseAuth.getInstance().signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showError("Error checking user type: " + e.getMessage());
                });
    }

    private void redirectToDashboard(Class<?> dashboardClass) {
        startActivity(new Intent(LoginActivity.this, dashboardClass));
        finish();
    }

    private void showError(String message) {
        loginErrorTextView.setText(message);
        loginErrorTextView.setVisibility(TextView.VISIBLE);
    }
}

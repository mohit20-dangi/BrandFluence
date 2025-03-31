package com.example.brandfluenceandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterBrandActivity extends AppCompatActivity {

    private EditText brandName, brandUsername, brandEmail, brandMobile, brandPassword;
    private Button registerBrandBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_brand);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind UI Elements
        brandName = findViewById(R.id.brandName);
        brandUsername = findViewById(R.id.brandUsername);
        brandEmail = findViewById(R.id.brandEmail);
        brandMobile = findViewById(R.id.brandMobile);
        brandPassword = findViewById(R.id.brandPassword);
        registerBrandBtn = findViewById(R.id.registerBrandBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        registerBrandBtn.setOnClickListener(v -> registerBrand());
    }

    private void registerBrand() {
        String name = brandName.getText().toString().trim();
        String username = brandUsername.getText().toString().trim();
        String email = brandEmail.getText().toString().trim();
        String mobile = brandMobile.getText().toString().trim();
        String password = brandPassword.getText().toString().trim();

        // Input Validations
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Firebase Auth Registration
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();

                    // Prepare Brand Data
                    Map<String, Object> brand = new HashMap<>();
                    brand.put("uid", uid);
                    brand.put("name", name);
                    brand.put("username", username);
                    brand.put("email", email);
                    brand.put("mobile", mobile);
                    brand.put("role", "brand");

                    // Save to Firestore
                    db.collection("Brands").document(uid).set(brand)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Brand Registered Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, BrandDashboardActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Auth Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

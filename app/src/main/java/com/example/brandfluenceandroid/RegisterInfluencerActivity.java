package com.example.brandfluenceandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterInfluencerActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText name, email, mobile, password, gender, followers, dob, category, socialLink;
    private Spinner platformSpinner;
    private Button registerBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private ImageView profileImageView;
    private Uri selectedImageUri;
    private String profileImageBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_influencer);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        // Bind views
        name = findViewById(R.id.influencerName);
        email = findViewById(R.id.influencerEmail);
        mobile = findViewById(R.id.influencerMobile);
        password = findViewById(R.id.influencerPassword);
        gender = findViewById(R.id.influencerGender);
        platformSpinner = findViewById(R.id.influencerPlatformSpinner);
        followers = findViewById(R.id.influencerFollowers);
        dob = findViewById(R.id.influencerDob);
        category = findViewById(R.id.influencerCategory);
        socialLink = findViewById(R.id.influencerSocialLink);
        registerBtn = findViewById(R.id.registerInfluencerBtn);
        profileImageView = findViewById(R.id.profileImageView);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.platforms_array_select,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(adapter);

        // Image picker
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Register influencer
        registerBtn.setOnClickListener(v -> registerInfluencer());
    }

    private void registerInfluencer() {
        String nameText = name.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String mobileText = mobile.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String genderText = gender.getText().toString().trim();
        String platformsText = platformSpinner.getSelectedItem().toString();
        String followersText = followers.getText().toString().trim();
        String dobText = dob.getText().toString().trim();
        String categoryText = category.getText().toString().trim();
        String socialLinkText = socialLink.getText().toString().trim();

        if (nameText.isEmpty() || emailText.isEmpty() || mobileText.isEmpty() ||
                passwordText.isEmpty() || genderText.isEmpty() || platformsText.isEmpty() ||
                followersText.isEmpty() || dobText.isEmpty() || categoryText.isEmpty() || socialLinkText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordText.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Register with Firebase Auth
        auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();

                    saveInfluencerData(uid, nameText, emailText, mobileText, genderText, platformsText,
                            followersText, dobText, categoryText, socialLinkText);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Auth Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveInfluencerData(String uid, String name, String email, String mobile, String gender,
                                    String platforms, String followers, String dob, String category,
                                    String socialLink) {

        Map<String, Object> influencer = new HashMap<>();
        influencer.put("name", name);
        influencer.put("email", email);
        influencer.put("phone", mobile);
        influencer.put("gender", gender);
        influencer.put("platforms", platforms);
        influencer.put("followers", followers);
        influencer.put("dob", dob);
        influencer.put("category", category);
        influencer.put("socialMediaLink", socialLink);
        influencer.put("profileImageBase64", profileImageBase64);

        db.collection("Influencers").document(uid).set(influencer)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Influencer Registered!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, InfluencerDashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Encode image to base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();
                profileImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                // Display preview
                profileImageView.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

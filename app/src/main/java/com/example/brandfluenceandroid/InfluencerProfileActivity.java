package com.example.brandfluenceandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InfluencerProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private EditText editTextName, editTextEmail, editTextFollowers, editTextDOB,
            editTextGender, editTextMobile, editTextCategory, editTextSocial;
    private Spinner spinnerPlatform;
    private Button btnSelectImage, buttonUpdate;

    private Uri imageUri;
    private String base64Image;

    private FirebaseFirestore db;
    private FirebaseUser user;

    private String selectedPlatform = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influencer_profile);

        profileImageView = findViewById(R.id.profileImageView);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextFollowers = findViewById(R.id.editTextFollowers);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextGender = findViewById(R.id.editTextGender);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextSocial = findViewById(R.id.editTextSocial);
        spinnerPlatform = findViewById(R.id.spinnerPlatform);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        setupPlatformSpinner();
        loadUserProfile();

        btnSelectImage.setOnClickListener(v -> openImageChooser());
        buttonUpdate.setOnClickListener(v -> updateProfile());
    }

    private void setupPlatformSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.platforms_array_select,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlatform.setAdapter(adapter);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                base64Image = encodeImageToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap decodeBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void loadUserProfile() {
        if (user == null) return;

        db.collection("Influencers").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editTextName.setText(documentSnapshot.getString("name"));
                        editTextEmail.setText(documentSnapshot.getString("email"));
                        editTextFollowers.setText(documentSnapshot.getString("followers"));
                        editTextDOB.setText(documentSnapshot.getString("dob"));
                        editTextGender.setText(documentSnapshot.getString("gender"));
                        editTextMobile.setText(documentSnapshot.getString("phone"));
                        editTextCategory.setText(documentSnapshot.getString("category"));
                        editTextSocial.setText(documentSnapshot.getString("socialMediaLink"));

                        String imageBase64 = documentSnapshot.getString("profileImageBase64");
                        if (imageBase64 != null && !imageBase64.isEmpty()) {
                            Bitmap bitmap = decodeBase64ToBitmap(imageBase64);
                            profileImageView.setImageBitmap(bitmap);
                        }

                        String platform = documentSnapshot.getString("platforms");
                        if (platform != null) {
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerPlatform.getAdapter();
                            int pos = adapter.getPosition(platform);
                            if (pos >= 0) {
                                spinnerPlatform.setSelection(pos);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private void updateProfile() {
        if (user == null) return;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        selectedPlatform = spinnerPlatform.getSelectedItem().toString();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", editTextName.getText().toString().trim());
        updatedData.put("email", editTextEmail.getText().toString().trim());
        updatedData.put("followers", editTextFollowers.getText().toString().trim());
        updatedData.put("dob", editTextDOB.getText().toString().trim());
        updatedData.put("gender", editTextGender.getText().toString().trim());
        updatedData.put("phone", editTextMobile.getText().toString().trim());
        updatedData.put("platforms", selectedPlatform);
        updatedData.put("category", editTextCategory.getText().toString().trim());
        updatedData.put("socialMediaLink", editTextSocial.getText().toString().trim());

        if (base64Image != null) {
            updatedData.put("profileImageBase64", base64Image);
        }

        db.collection("Influencers").document(user.getUid())
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.brand_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            return true;
        } else if (id == R.id.action_chat) {
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(InfluencerProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

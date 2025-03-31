package com.example.brandfluenceandroid;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CreateCampaignActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etBudget, etDeadline;
    private Spinner spinnerPlatform;
    private Button btnSubmit;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_campaign);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        etTitle = findViewById(R.id.etCampaignTitle);
        etDescription = findViewById(R.id.etCampaignDescription);
        etBudget = findViewById(R.id.etBudget);
        etDeadline = findViewById(R.id.etDeadline);
        spinnerPlatform = findViewById(R.id.spinnerPlatform);
        btnSubmit = findViewById(R.id.btnSubmitCampaign);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.platform_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlatform.setAdapter(adapter);

        etDeadline.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> submitCampaign());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(CreateCampaignActivity.this,
                (view, year1, month1, dayOfMonth) ->
                        etDeadline.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day);
        datePickerDialog.show();
    }

    private void submitCampaign() {
        Log.d("CreateCampaign", "Button clicked!");
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String platform = spinnerPlatform.getSelectedItem().toString();
        String budgetStr = etBudget.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();

        // Validate all fields
        if (title.isEmpty() || description.isEmpty() || budgetStr.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            resetSubmitButton();
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid budget format!", Toast.LENGTH_SHORT).show();
            resetSubmitButton();
            return;
        }

        String brandId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create Campaign object (without campaignId initially)
        Campaign campaign = new Campaign(title, description, platform, budget, deadline, brandId, null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Campaigns")
                .add(campaign)
                .addOnSuccessListener(documentReference -> {
                    // Update the campaign with its Firestore-generated ID
                    String campaignId = documentReference.getId();
                    documentReference.update("campaignId", campaignId);

                    Log.d("CreateCampaign", "Campaign created! ID: " + campaignId);
                    Toast.makeText(this, "Campaign created successfully!", Toast.LENGTH_SHORT).show();

                    // Redirect to Dashboard
                    Intent intent = new Intent(CreateCampaignActivity.this, BrandDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("CreateCampaign", "Error adding campaign: " + e.getMessage());
                    Toast.makeText(this, "Failed to create campaign: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    resetSubmitButton();
                });
    }

    private void resetSubmitButton() {
        btnSubmit.setEnabled(true);
        btnSubmit.setText("Submit");
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
            //startActivity(new Intent(this, NotificationActivity.class));
            return true;
        } else if (id == R.id.action_chat) {
            //startActivity(new Intent(this, ChatActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut(); // Logout user
        Intent intent = new Intent(CreateCampaignActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
        startActivity(intent);
        finish();
    }
}

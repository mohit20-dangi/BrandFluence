package com.example.brandfluenceandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CampaignDetailsInfluencerActivity extends AppCompatActivity {

    private TextView titleText, descriptionText, platformText, budgetText, deadlineText;
    private Button applyButton;
    private FirebaseFirestore db;
    private String campaignId, influencerId;
    private Campaign campaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_details_influencer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        influencerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        platformText = findViewById(R.id.platformText);
        budgetText = findViewById(R.id.budgetText);
        deadlineText = findViewById(R.id.deadlineText);
        applyButton = findViewById(R.id.applyButton);



        if (getIntent().hasExtra("campaign")) {
            campaign = (Campaign) getIntent().getSerializableExtra("campaign");
            campaignId = campaign.getCampaignId();

            titleText.setText(campaign.getTitle());
            descriptionText.setText(campaign.getDescription());
            platformText.setText("Platform: " + campaign.getPlatforms());
            budgetText.setText("Budget: $" + campaign.getBudget());
            deadlineText.setText("Deadline: " + campaign.getDeadline());
        }

        applyButton.setOnClickListener(v -> applyToCampaign());
    }

    private void applyToCampaign() {
        db.collection("Campaigns").document(campaignId)
                .update("interestedInfluencers", com.google.firebase.firestore.FieldValue.arrayUnion(influencerId))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Applied successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to apply.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            // startActivity(new Intent(this, NotificationActivity.class));
            return true;
        } else if (id == R.id.action_chat) {
//            Intent intent = new Intent(CampaignDetailsInfluencerActivity.this, ChatActivity.class);
//            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
            return true;
        } else if (id == R.id.action_profile) {
            Intent intent = new Intent(CampaignDetailsInfluencerActivity.this, InfluencerProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(CampaignDetailsInfluencerActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

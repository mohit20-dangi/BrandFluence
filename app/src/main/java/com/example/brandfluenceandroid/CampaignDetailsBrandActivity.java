package com.example.brandfluenceandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// Remove imports & package for clarity

public class CampaignDetailsBrandActivity extends AppCompatActivity {

    private TextView titleTextView, descriptionTextView, platformTextView, budgetTextView, deadlineTextView;
    private Button btnDelete;
    private ListView listApplied, listApproved;
    private FirebaseFirestore firestore;
    private String campaignId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_details_brand);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // Removed back button
            getSupportActionBar().setTitle("Campaign Details");
        }

        // Initialize UI elements
        titleTextView = findViewById(R.id.title);
        descriptionTextView = findViewById(R.id.description);
        platformTextView = findViewById(R.id.platform);
        budgetTextView = findViewById(R.id.budget);
        deadlineTextView = findViewById(R.id.deadline);
        btnDelete = findViewById(R.id.btnDelete);
        listApplied = findViewById(R.id.listApplied);
        listApproved = findViewById(R.id.listApproved);

        campaignId = getIntent().getStringExtra("campaignId");
        if (campaignId == null || campaignId.isEmpty()) {
            Toast.makeText(this, "Invalid campaign ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firestore = FirebaseFirestore.getInstance();
        fetchCampaignDetails(campaignId);

        btnDelete.setOnClickListener(v -> confirmDeleteCampaign());
    }

    private void fetchCampaignDetails(String campaignId) {
        firestore.collection("Campaigns").document(campaignId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        titleTextView.setText(documentSnapshot.getString("title"));
                        descriptionTextView.setText(documentSnapshot.getString("description"));
                        platformTextView.setText(documentSnapshot.getString("platform"));
                        deadlineTextView.setText(documentSnapshot.getString("deadline"));

                        Double budgetValue = documentSnapshot.getDouble("budget");
                        budgetTextView.setText(budgetValue != null ? String.valueOf(budgetValue) : "N/A");

                        List<String> interested = (List<String>) documentSnapshot.get("interestedInfluencers");
                        loadInfluencers(interested, listApplied, true); // true: can approve

                        List<String> approved = (List<String>) documentSnapshot.get("approvedInfluencers");
                        loadInfluencers(approved, listApproved, false); // false: no approval on click
                    } else {
                        Toast.makeText(this, "Campaign not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadInfluencers(List<String> influencerIds, ListView listView, boolean allowApprove) {
        if (influencerIds == null || influencerIds.isEmpty()) {
            listView.setAdapter(null);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Influencer> influencers = new ArrayList<>();
        List<String> pendingIds = new ArrayList<>(influencerIds); // to track what's left

        for (String influencerId : influencerIds) {
            if (influencerId == null || influencerId.trim().isEmpty()) {
                pendingIds.remove(influencerId); // clean up empty ID
                continue;
            }

            db.collection("Influencers").document(influencerId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            Influencer inf = snapshot.toObject(Influencer.class);
                            if (inf != null) {
                                inf.setInfluencerId(snapshot.getId());
                                influencers.add(inf);
                            }
                        }

                        pendingIds.remove(influencerId);
                        if (pendingIds.isEmpty()) {
                            // All done loading
                            runOnUiThread(() -> {
                                InfluencerListAdapter adapter = new InfluencerListAdapter(
                                        CampaignDetailsBrandActivity.this,
                                        influencers,
                                        influencer -> {
                                            if (allowApprove) approveInfluencer(influencer.getInfluencerId());
                                        }
                                );
                                listView.setAdapter(adapter);
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        pendingIds.remove(influencerId);
                        if (pendingIds.isEmpty()) {
                            runOnUiThread(() -> {
                                InfluencerListAdapter adapter = new InfluencerListAdapter(
                                        CampaignDetailsBrandActivity.this,
                                        influencers,
                                        influencer -> {
                                            if (allowApprove) approveInfluencer(influencer.getInfluencerId());
                                        }
                                );
                                listView.setAdapter(adapter);
                            });
                        }
                    });
        }
    }




    private void approveInfluencer(String influencerId) {
        if (influencerId == null || influencerId.trim().isEmpty()) {
            Toast.makeText(this, "Invalid influencer ID", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("Campaigns").document(campaignId)
                .update(
                        "approvedInfluencers", FieldValue.arrayUnion(influencerId),
                        "interestedInfluencers", FieldValue.arrayRemove(influencerId)
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Influencer approved", Toast.LENGTH_SHORT).show();
                    fetchCampaignDetails(campaignId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to approve", Toast.LENGTH_SHORT).show();
                });
    }





    private void confirmDeleteCampaign() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Campaign")
                .setMessage("Are you sure you want to delete this campaign?")
                .setPositiveButton("Delete", (dialog, which) -> deleteCampaign())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCampaign() {
        firestore.collection("Campaigns").document(campaignId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Campaign deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete campaign", Toast.LENGTH_SHORT).show();
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
            // startActivity(new Intent(this, NotificationActivity.class));
            return true;
        } else if (id == R.id.action_chat) {
            // startActivity(new Intent(this, BrandChatListActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(CampaignDetailsBrandActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

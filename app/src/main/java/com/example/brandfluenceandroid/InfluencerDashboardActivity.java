package com.example.brandfluenceandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InfluencerDashboardActivity extends AppCompatActivity {

    private RecyclerView appliedRecyclerView, availableRecyclerView;
    private CampaignAdapter appliedAdapter, availableAdapter;
    private List<Campaign> appliedCampaigns = new ArrayList<>();
    private List<Campaign> availableCampaigns = new ArrayList<>();
    private FirebaseFirestore db;
    private String influencerId;

    private String selectedPlatform = null;
    private Double maxBudget = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_influencer_dashboard);

        db = FirebaseFirestore.getInstance();

        appliedRecyclerView = findViewById(R.id.appliedCampaignsRecycler);
        availableRecyclerView = findViewById(R.id.availableCampaignsRecycler);
        Button filterBtn = findViewById(R.id.filterButton);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            influencerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        appliedAdapter = new CampaignAdapter(this, appliedCampaigns, campaign ->
                Toast.makeText(this, "Applied: " + campaign.getTitle(), Toast.LENGTH_SHORT).show());

        availableAdapter = new CampaignAdapter(this, new ArrayList<>(), campaign -> {
            Intent intent = new Intent(this, CampaignDetailsInfluencerActivity.class);
            intent.putExtra("campaign", campaign);
            startActivity(intent);
        });

        appliedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        appliedRecyclerView.setAdapter(appliedAdapter);

        availableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        availableRecyclerView.setAdapter(availableAdapter);

        filterBtn.setOnClickListener(v -> {
            FilterFragmentInfluencer fragment = new FilterFragmentInfluencer();
            fragment.setFilterListener((platform, budget) -> {
                selectedPlatform = platform;
                maxBudget = budget;
                applyFilter();
            });
            fragment.show(getSupportFragmentManager(), "filter_fragment");
        });

        loadCampaigns();
    }

    private void loadCampaigns() {
        db.collection("Campaigns")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    appliedCampaigns.clear();
                    availableCampaigns.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Campaign campaign = doc.toObject(Campaign.class);
                        if (campaign == null) continue;

                        campaign.setCampaignId(doc.getId());

                        db.collection("Brands")
                                .document(campaign.getBrandId())
                                .get()
                                .addOnSuccessListener(brandDoc -> {
                                    if (brandDoc.exists()) {
                                        String brandName = brandDoc.getString("name");
                                        if (brandName != null) {
                                            campaign.setBrandName(brandName);
                                        }
                                    }

                                    if (campaign.getApprovedInfluencers() != null &&
                                            campaign.getApprovedInfluencers().contains(influencerId)) {
                                        appliedCampaigns.add(campaign);
                                    } else {
                                        availableCampaigns.add(campaign);
                                    }

                                    appliedAdapter.notifyDataSetChanged();
                                    applyFilter();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Dashboard", "Error loading campaigns", e);
                    Toast.makeText(this, "Failed to load campaigns.", Toast.LENGTH_SHORT).show();
                });
    }

    private void applyFilter() {
        List<Campaign> filteredList = new ArrayList<>();
        for (Campaign campaign : availableCampaigns) {
            boolean matchesPlatform = selectedPlatform == null || campaign.getPlatforms().equalsIgnoreCase(selectedPlatform);
            boolean matchesBudget = maxBudget == null || campaign.getBudget() >= maxBudget;

            if (matchesPlatform && matchesBudget) {
                filteredList.add(campaign);
            }
        }
        availableAdapter.updateCampaigns(filteredList);
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
            return true;
        } else if (id == R.id.action_chat) {
            startActivity(new Intent(this, ChatActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, InfluencerProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

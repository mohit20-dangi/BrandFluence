package com.example.brandfluenceandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BrandDashboardActivity extends AppCompatActivity {

    private RecyclerView campaignRecyclerView, influencerRecyclerView;
    private Button btnCreateCampaign, btnFilter;
    private CampaignAdapter campaignAdapter;
    private InfluencerAdapter influencerAdapter;
    private List<Campaign> campaignList;
    private List<Influencer> influencerList, filteredList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentBrandId;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_dashboard);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchCampaigns);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentBrandId = currentUser.getUid();

        // Views
        campaignRecyclerView = findViewById(R.id.previousCampaignsRecycler);
        influencerRecyclerView = findViewById(R.id.influencersRecycler);
        btnCreateCampaign = findViewById(R.id.createCampaignButton);
        btnFilter = findViewById(R.id.filterButton);

        campaignList = new ArrayList<>();
        influencerList = new ArrayList<>();
        filteredList = new ArrayList<>();

        campaignAdapter = new CampaignAdapter(this, campaignList, campaign -> {
            Intent intent = new Intent(BrandDashboardActivity.this, CampaignDetailsBrandActivity.class);
            intent.putExtra("campaignId", campaign.getCampaignId());
            startActivity(intent);
        });

        influencerAdapter = new InfluencerAdapter(this, filteredList);

        campaignRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        influencerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        campaignRecyclerView.setAdapter(campaignAdapter);
        influencerRecyclerView.setAdapter(influencerAdapter);

        btnCreateCampaign.setOnClickListener(v -> startActivity(new Intent(this, CreateCampaignActivity.class)));

        btnFilter.setOnClickListener(v -> filterInfluencers());

        fetchCampaigns();
        fetchInfluencers();
    }

    private void fetchCampaigns() {
        db.collection("Campaigns").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    campaignList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String brandId = doc.getString("brandId");

                        if (brandId != null && brandId.equals(currentBrandId)) {
                            String title = doc.getString("title");
                            String description = doc.getString("description");
                            String platform = doc.getString("platform");
                            Double budget = doc.getDouble("budget");
                            String deadline = doc.getString("deadline");
                            String campaignId = doc.getId();

                            Campaign campaign = new Campaign(
                                    title != null ? title : "",
                                    description != null ? description : "",
                                    platform != null ? platform : "",
                                    budget != null ? budget : 0.0,
                                    deadline != null ? deadline : "",
                                    brandId,
                                    campaignId
                            );

                            campaignList.add(campaign);
                        }
                    }

                    campaignAdapter.notifyDataSetChanged();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load campaigns.", Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void fetchInfluencers() {
        db.collection("Influencers").get().addOnSuccessListener(queryDocumentSnapshots -> {
            influencerList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String followers;
                if (doc.contains("followers")) {
                    try {
                        followers = String.valueOf(doc.getLong("followers"));
                    } catch (Exception e) {
                        followers = doc.getString("followers");
                    }
                } else {
                    followers = "0";
                }

                String name = doc.getString("name");
                String gender = doc.getString("gender");
                String platform = doc.getString("platforms");
                String category = doc.getString("category");
                String socialMediaLink = doc.getString("socialMediaLink");
                String imageBase64 = doc.getString("profileImageBase64");

                Influencer influencer = new Influencer(
                        doc.getId(),
                        name != null ? name : "N/A",
                        gender != null ? gender : "N/A",
                        platform != null ? platform : "N/A",
                        followers != null ? followers : "0",
                        category != null ? category : "N/A",
                        socialMediaLink != null ? socialMediaLink : "N/A",
                        imageBase64 != null ? imageBase64 : ""
                );

                influencerList.add(influencer);
            }

            filteredList.clear();
            filteredList.addAll(influencerList);
            influencerAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch influencers.", Toast.LENGTH_SHORT).show();
        });
    }

    private void filterInfluencers() {
        InfluencerFilterBottomSheet filterSheet = new InfluencerFilterBottomSheet((category, minFollowers, platform) -> {
            filteredList.clear();

            for (Influencer influencer : influencerList) {
                boolean matchesCategory = category.isEmpty() || influencer.getCategory().equalsIgnoreCase(category);
                boolean matchesPlatform = platform.isEmpty() || influencer.getPlatforms().equalsIgnoreCase(platform);

                int influencerFollowers = 0;
                try {
                    influencerFollowers = Integer.parseInt(influencer.getFollowers());
                } catch (NumberFormatException ignored) {}

                boolean matchesFollowers = influencerFollowers >= minFollowers;

                if (matchesCategory && matchesPlatform && matchesFollowers) {
                    filteredList.add(influencer);
                }
            }

            influencerAdapter.notifyDataSetChanged();
        });

        filterSheet.show(getSupportFragmentManager(), "InfluencerFilter");
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
        Intent intent = new Intent(BrandDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

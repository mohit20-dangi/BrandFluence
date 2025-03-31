package com.example.brandfluenceandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Spinner spinnerGender, spinnerPlatform;
    private EditText editTextMinFollowers, editTextMaxFollowers;
    private Button buttonFilter;
    private RecyclerView recyclerViewInfluencers;
    private InfluencerAdapter influencerAdapter;
    private List<Influencer> influencerList;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI components
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerPlatform = findViewById(R.id.spinnerPlatform);
        editTextMinFollowers = findViewById(R.id.editTextMinFollowers);
        editTextMaxFollowers = findViewById(R.id.editTextMaxFollowers);
        buttonFilter = findViewById(R.id.buttonFilter);
        recyclerViewInfluencers = findViewById(R.id.recyclerViewInfluencers);

        // Setup RecyclerView
        recyclerViewInfluencers.setLayoutManager(new LinearLayoutManager(this));
        influencerList = new ArrayList<>();
        influencerAdapter = new InfluencerAdapter(this, influencerList);
        recyclerViewInfluencers.setAdapter(influencerAdapter);

        // Populate Spinners
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> platformAdapter = ArrayAdapter.createFromResource(this,
                R.array.platform_options, android.R.layout.simple_spinner_item);
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlatform.setAdapter(platformAdapter);

        // Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Influencers");

        // Filter Button Click
       // buttonFilter.setOnClickListener(v -> applyFilters());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.brand_menu, menu);
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
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

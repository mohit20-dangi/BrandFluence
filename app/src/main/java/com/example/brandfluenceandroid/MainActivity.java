package com.example.brandfluenceandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;


    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        handler.postDelayed(() -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                startActivity(new Intent(MainActivity.this, StartPage.class));
                finish();
            } else {
                db.collection("users").document(user.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String role = documentSnapshot.getString("role");
                                if ("influencer".equals(role)) {
                                    startActivity(new Intent(MainActivity.this, InfluencerDashboardActivity.class));
                                } else if ("brand".equals(role)) {
                                    startActivity(new Intent(MainActivity.this, BrandDashboardActivity.class));
                                } else {
                                    Log.w("MainActivity", "Unknown role: " + role);
                                    startActivity(new Intent(MainActivity.this, StartPage.class));
                                }
                            } else {
                                Log.w("MainActivity", "No role found in Firestore");
                                startActivity(new Intent(MainActivity.this, StartPage.class));
                            }
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("MainActivity", "Firestore error: " + e.getMessage());
                            startActivity(new Intent(MainActivity.this, StartPage.class));
                            finish();
                        });
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

}

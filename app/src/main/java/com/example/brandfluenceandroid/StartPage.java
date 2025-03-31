package com.example.brandfluenceandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class StartPage extends AppCompatActivity {

    Button loginBtn, regBrandBtn, regInfluencerBtn, aboutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page); // Make sure your XML file is named activity_start_page.xml

        loginBtn = findViewById(R.id.btnLogin);
        regBrandBtn = findViewById(R.id.btnRegisterBrand);
        regInfluencerBtn = findViewById(R.id.btnRegisterInfluencer);
        aboutBtn = findViewById(R.id.btnAbout);


        loginBtn.setOnClickListener(v -> startActivity(new Intent(StartPage.this, LoginActivity.class)));

        regBrandBtn.setOnClickListener(v -> startActivity(new Intent(StartPage.this, RegisterBrandActivity.class)));

        regInfluencerBtn.setOnClickListener(v -> startActivity(new Intent(StartPage.this, RegisterInfluencerActivity.class)));

        aboutBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.fragment_container, new AboutFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

    }
}

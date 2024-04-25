package com.example.pplastic_management_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen1 extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen1);

        // Set a delay for the splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the delay
                Intent mainIntent = new Intent(SplashScreen1.this, Landingpage.class);
                startActivity(mainIntent);
                finish(); // Close the splash activity to prevent going back
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
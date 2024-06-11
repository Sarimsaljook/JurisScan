package com.example.jurisscan;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Create a new thread to handle the delay
        new Thread(() -> {
            try {
                // Delay for 2 1/2 seconds
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                // Check if user is logged in or not
                if (currentUser != null) {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the splash screen activity
                } else {
                    startActivity(new Intent(SplashScreen.this, Login.class));
                    finish();
                }
            }
        }).start();
    }
}
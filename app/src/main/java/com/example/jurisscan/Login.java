package com.example.jurisscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        TextView signUpText = findViewById(R.id.signupText);
        signUpText.setOnClickListener(e -> startActivity(new Intent(Login.this, SignUp.class)));

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(e -> mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(Login.this, MainActivity.class));
                        Toast.makeText(this, "Login Successful, Welcome Back!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Sorry Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(error -> {
                    error.printStackTrace();
                    if (error instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, "Invalid Username Or Password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
                    }
                }));

    }
}
package com.example.jurisscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        TextView signUpText = findViewById(R.id.loginText);
        signUpText.setOnClickListener(e -> startActivity(new Intent(SignUp.this, Login.class)));

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        Button signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(e -> mAuth.createUserWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(SignUp.this, MainActivity.class));
                        Toast.makeText(this, "Sign Up Successful! \n Welcome To JurisScan.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Sorry Something Went Wrong While Making Your Account.", Toast.LENGTH_SHORT).show();
                }));

    }
}
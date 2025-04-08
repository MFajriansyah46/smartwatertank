package com.bangraja.smartwatertank;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn, exitBtn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        exitBtn = findViewById(R.id.exitBtn);
        progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(view -> {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
                Toast.makeText(LoginActivity.this, "Email dan Password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        exitBtn.setOnClickListener(view -> {
            finishAffinity();
        });
    }
}
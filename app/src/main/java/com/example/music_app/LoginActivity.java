package com.example.music_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.music_app.databinding.ActivityLoginBinding;
import com.example.music_app.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.txtEmail.getText().toString();
                String password = binding.txtPassword.getText().toString();

                if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                    binding.txtEmail.setError("Invalid Email");
                    return;
                }
                if(password.length() < 6) {
                    binding.txtPassword.setError("Length should more than 6 char");
                    return;
                }

                loginAccount(email, password);
            }
        });
        binding.btnGoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    void loginAccount(String email, String password) {
        setInProgress(true);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult ->  {
                setInProgress(false);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            })
            .addOnFailureListener(exception ->  {
                setInProgress(false);
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
            });
    }




    void setInProgress(Boolean inProgress) {
        if(inProgress) {
            binding.btnLogin.setVisibility(View.GONE);
            binding.Progress.setProgress(View.VISIBLE);
        } else {
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.Progress.setProgress(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
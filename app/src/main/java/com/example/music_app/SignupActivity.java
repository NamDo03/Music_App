package com.example.music_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.music_app.databinding.ActivitySignupBinding;

import java.util.regex.Pattern;
import android.util.Patterns;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.txtEmail.getText().toString();
                String password = binding.txtPassword.getText().toString();
                String confirmPassword = binding.txtConfirmPassword.getText().toString();

                if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                    binding.txtEmail.setError("Invalid Email");
                    return;
                }
                if(password.length() < 6) {
                    binding.txtPassword.setError("Length should more than 6 char");
                    return;
                }
                if(!password.equals(confirmPassword)) {
                    binding.txtConfirmPassword.setError("Confirm Password not matched");
                    return;
                }

                createAccount(email, password);
            }
        });

        binding.btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void createAccount(String email, String password) {
        setInProgress(true);
        //save db
        //Success
        setInProgress(false);
        Toast.makeText(getApplicationContext(), "Create account successfully", Toast.LENGTH_SHORT).show();
        finish();
        //Failed
        setInProgress(false);
        Toast.makeText(getApplicationContext(), "Create account failed", Toast.LENGTH_SHORT).show();

    }

    void setInProgress(Boolean inProgress) {
        if(inProgress) {
            binding.btnSignup.setVisibility(View.GONE);
            binding.Progress.setProgress(View.VISIBLE);
        } else {
            binding.btnSignup.setVisibility(View.VISIBLE);
            binding.Progress.setProgress(View.GONE);
        }
    }
}
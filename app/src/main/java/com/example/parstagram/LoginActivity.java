package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private EditText etUsername, etPassword;
    private Button btnLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null)
            goMainActivity();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.light_blue));
        btnLogin.setTextColor(getResources().getColor(R.color.black));
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked LOGIN button.");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                attemptLogin(username, password);
            }
        });


        btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setBackgroundColor(getResources().getColor(R.color.light_green));
        btnSignup.setTextColor(getResources().getColor(R.color.black));
        btnSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked SIGN UP button.");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                attemptSignUp(username, password);
            }
        });

    }

    private void attemptLogin(String username, String password) {
        Log.i(TAG, "Attempting to login user: " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error occurred while attempting login.", e);
                    Toast.makeText(LoginActivity.this, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                goMainActivity();
                Toast.makeText(LoginActivity.this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void attemptSignUp(String username, String password) {
        Log.i(TAG, "Attempting to sign up user: " + username);
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error occurred while attempting sign up.", e);
                    Toast.makeText(LoginActivity.this, "Error on sign up!", Toast.LENGTH_SHORT).show();
                    return;
                }

                goMainActivity();
                Toast.makeText(LoginActivity.this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
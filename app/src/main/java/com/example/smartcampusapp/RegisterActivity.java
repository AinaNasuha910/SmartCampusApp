package com.example.smartcampusapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText NewUsername, NewPassword;
    private Button btnRegister;
    private TextView BackToLogin, RegisterMessage;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        NewUsername = findViewById(R.id.NewUsername);
        NewPassword = findViewById(R.id.NewPassword);
        btnRegister = findViewById(R.id.btnRegister);
        BackToLogin = findViewById(R.id.BackToLogin);
        RegisterMessage = findViewById(R.id.RegisterMessage);

        BackToLogin.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String user = NewUsername.getText().toString().trim();
        String pass = NewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
            RegisterMessage.setText("Please fill in username and password.");
            return;
        }

        if (dbHelper.isUsernameTaken(user)) {
            RegisterMessage.setText("Username already exists. Try another one.");
            return;
        }

        boolean success = dbHelper.registerUser(user, pass);
        if (success) {
            RegisterMessage.setText("Account created! Please login.");
            finish(); // balik ke LoginActivity
        } else {
            RegisterMessage.setText("Register failed. Try again.");
        }
    }
}


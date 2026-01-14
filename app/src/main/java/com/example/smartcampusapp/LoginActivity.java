package com.example.smartcampusapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText Username, Password;
    private TextView RegisterLink, Message;
    private Button btnLogin;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        RegisterLink = findViewById(R.id.RegisterLink);
        Message = findViewById(R.id.Message);
        btnLogin = findViewById(R.id.btnLogin);

        RegisterLink.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String user = Username.getText().toString().trim();
        String pass = Password.getText().toString().trim();

        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
            Message.setText("Please enter username and password.");
            return;
        }

        boolean ok = dbHelper.checkLogin(user, pass);
        if (ok) {
            // EDIT: save username in session
            getSharedPreferences("session", MODE_PRIVATE)
                    .edit()
                    .putString("username", user)
                    .apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Message.setText("Invalid username or password.");
        }
    }
}


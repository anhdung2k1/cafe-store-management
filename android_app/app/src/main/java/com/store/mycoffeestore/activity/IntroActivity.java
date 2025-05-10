package com.store.mycoffeestore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.store.mycoffeestore.R;
import com.store.mycoffeestore.helper.TokenManager;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() != null) {
            // Auto-login if token exists
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
            return;
        }

        Button signInBtn = findViewById(R.id.signInBtn);
        Button signUpBtn = findViewById(R.id.signUpBtn);

        signInBtn.setOnClickListener(view ->
                startActivity(new Intent(IntroActivity.this, SignInActivity.class)));

        signUpBtn.setOnClickListener(view ->
                startActivity(new Intent(IntroActivity.this, SignUpActivity.class)));
    }
}
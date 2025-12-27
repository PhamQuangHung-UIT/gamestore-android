package com.uit.gamestore.ui.landing;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.gamestore.R;
import com.uit.gamestore.ui.login.LoginActivity;

public class LandingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);

        var getStartedButton = findViewById(R.id.get_started_button);

        getStartedButton.setOnClickListener(view -> {
            var intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

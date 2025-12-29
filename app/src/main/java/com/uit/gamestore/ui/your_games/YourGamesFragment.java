package com.uit.gamestore.ui.your_games;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.uit.gamestore.R;
import com.uit.gamestore.data.local.TokenManager;
import com.uit.gamestore.ui.login.LoginActivity;

public class YourGamesFragment extends Fragment {

    private YourGamesViewModel viewModel;
    private LinearLayout loginRequiredLayout;
    private View contentLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(YourGamesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        loginRequiredLayout = view.findViewById(R.id.loginRequiredLayout);
        contentLayout = view.findViewById(R.id.contentLayout);
        Button loginButton = view.findViewById(R.id.buttonLogin);
        
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginState();
    }

    private void updateLoginState() {
        boolean isLoggedIn = TokenManager.getInstance().isLoggedIn();
        
        if (loginRequiredLayout != null) {
            loginRequiredLayout.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        }
        if (contentLayout != null) {
            contentLayout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        }
    }
}
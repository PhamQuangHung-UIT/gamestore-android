package com.uit.gamestore.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.uit.gamestore.R;
import com.uit.gamestore.data.local.TokenManager;
import com.uit.gamestore.data.remote.dto.CustomerProfileDto;
import com.uit.gamestore.data.repository.CustomerRepository;
import com.uit.gamestore.ui.login.LoginActivity;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private LinearLayout loginRequiredLayout;
    private ScrollView profileLayout;
    private ProgressBar progressBar;
    private TextView textViewUsername;
    private TextView textViewEmail;
    private TextView textViewBalance;
    private Button buttonLogin;
    private Button buttonLogout;

    private final CustomerRepository customerRepository = new CustomerRepository();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void initViews(View view) {
        loginRequiredLayout = view.findViewById(R.id.loginRequiredLayout);
        profileLayout = view.findViewById(R.id.profileLayout);
        progressBar = view.findViewById(R.id.progressBar);
        textViewUsername = view.findViewById(R.id.textViewUsername);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewBalance = view.findViewById(R.id.textViewBalance);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogout = view.findViewById(R.id.buttonLogout);
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        buttonLogout.setOnClickListener(v -> {
            TokenManager.getInstance().clearSession();
            Toast.makeText(requireContext(), R.string.logged_out, Toast.LENGTH_SHORT).show();
            updateUI();
        });

        View menuWishlist = requireView().findViewById(R.id.menuWishlist);
        if (menuWishlist != null) {
            menuWishlist.setOnClickListener(v -> {
                Navigation.findNavController(requireView()).navigate(R.id.navigation_your_games);
            });
        }
    }

    private void updateUI() {
        boolean isLoggedIn = TokenManager.getInstance().isLoggedIn();

        if (isLoggedIn) {
            loginRequiredLayout.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);
            loadProfile();
        } else {
            loginRequiredLayout.setVisibility(View.VISIBLE);
            profileLayout.setVisibility(View.GONE);
        }
    }

    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);

        customerRepository.getProfile(new CustomerRepository.ProfileCallback() {
            @Override
            public void onSuccess(@NonNull CustomerProfileDto profile) {
                if (getContext() == null) return;
                
                progressBar.setVisibility(View.GONE);
                
                textViewUsername.setText(profile.getUsername() != null ? profile.getUsername() : "User");
                textViewEmail.setText(profile.getEmail() != null ? profile.getEmail() : "");
                
                double balance = profile.getAccountBalance();
                textViewBalance.setText(String.format(Locale.US, "$%.2f", balance));
            }

            @Override
            public void onError(@NonNull String message) {
                if (getContext() == null) return;
                
                progressBar.setVisibility(View.GONE);
                
                // If unauthorized, clear session and show login
                if (message.contains("401") || message.contains("login")) {
                    TokenManager.getInstance().clearSession();
                    updateUI();
                }
            }
        });
    }
}

package com.uit.gamestore;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.uit.gamestore.ui.store.GameStoreToolbar;

public class MainActivity extends AppCompatActivity {
    private GameStoreToolbar m_toolbar;
    private ImageButton m_searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        m_toolbar = findViewById(R.id.toolbar_gamestore);
        m_searchButton = m_toolbar.findViewById(R.id.button_search);

        setSupportActionBar(m_toolbar);
        
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_store, R.id.navigation_your_games, R.id.navigation_settings)
                .build();
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHost == null) {
            getSupportFragmentManager().executePendingTransactions();
            navHost = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_main);
        }
        var navController = navHost.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void showSearchIcon() {
        if (m_searchButton != null) {
            m_searchButton.setVisibility(View.VISIBLE);
        }
    }

    public void hideSearchIcon() {
        if (m_searchButton != null) {
            m_searchButton.setVisibility(View.GONE);
        }
    }

    public GameStoreToolbar getToolbar() {
        return m_toolbar;
    }
}
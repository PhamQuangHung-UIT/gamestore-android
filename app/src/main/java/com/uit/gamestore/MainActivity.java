package com.uit.gamestore;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.uit.gamestore.databinding.ActivityMainBinding;
import com.uit.gamestore.ui.store.GameStoreToolbar;

public class MainActivity extends AppCompatActivity {
    private Toolbar m_defaultToolbar;
    private GameStoreToolbar m_storeToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        //Get toolbar
        m_defaultToolbar = findViewById(R.id.toolbar_default);
        m_storeToolbar = findViewById(R.id.toolbar_gamestore);

        setSupportActionBar(m_defaultToolbar);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_store, R.id.navigation_your_games, R.id.navigation_settings)
                .build();
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHost == null) {
            // ensure any pending fragment transactions are executed
            getSupportFragmentManager().executePendingTransactions();
            navHost = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_main);
        }
        var navController = navHost.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void showDefaultToolbar() {
        m_defaultToolbar.setVisibility(View.VISIBLE);
        m_storeToolbar.setVisibility(View.GONE);

        setSupportActionBar(m_defaultToolbar);
    }

    public void showStoreToolbar() {
        m_defaultToolbar.setVisibility(View.GONE);
        m_storeToolbar.setVisibility(View.VISIBLE);

        setSupportActionBar(m_storeToolbar);
    }


    public Toolbar getToolbar() {
        return m_defaultToolbar;
    }
}
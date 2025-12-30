package com.uit.gamestore.ui.your_games;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.uit.gamestore.MainActivity;
import com.uit.gamestore.R;
import com.uit.gamestore.data.local.TokenManager;
import com.uit.gamestore.data.remote.dto.CustomerProfileDto;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.repository.CustomerRepository;
import com.uit.gamestore.ui.game_detail.GameDetailActivity;
import com.uit.gamestore.ui.login.LoginActivity;
import com.uit.gamestore.ui.store.CategorySection;
import com.uit.gamestore.ui.store.GameListAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YourGamesFragment extends Fragment {

    private LinearLayout loginRequiredLayout;
    private View contentLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private ProgressBar progressBar;
    
    private GameListAdapter adapter;
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final Set<String> ownedGameIds = new HashSet<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Hide search icon on this page
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideSearchIcon();
        }
        return inflater.inflate(R.layout.fragment_your_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginState();
    }


    private void initViews(View view) {
        loginRequiredLayout = view.findViewById(R.id.loginRequiredLayout);
        contentLayout = view.findViewById(R.id.contentLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerViewWishlist);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        progressBar = view.findViewById(R.id.progressBar);
        
        Button loginButton = view.findViewById(R.id.buttonLogin);
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            });
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this::loadWishlist);
            swipeRefreshLayout.setColorSchemeResources(R.color.teal_200, R.color.teal_700);
        }
    }

    private void setupRecyclerView() {
        if (recyclerView == null) return;
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GameListAdapter(CategorySection.DIRECTION_VERTICAL, requireContext());
        adapter.setOnGameClickListener(this::onGameClick);
        recyclerView.setAdapter(adapter);
    }

    private void updateLoginState() {
        boolean isLoggedIn = TokenManager.getInstance().isLoggedIn();
        
        if (loginRequiredLayout != null) {
            loginRequiredLayout.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        }
        if (contentLayout != null) {
            contentLayout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        }
        
        if (isLoggedIn) {
            loadProfileAndWishlist();
        }
    }

    private void loadProfileAndWishlist() {
        customerRepository.getProfile(new CustomerRepository.ProfileCallback() {
            @Override
            public void onSuccess(@NonNull CustomerProfileDto profile) {
                ownedGameIds.clear();
                List<String> ownedIds = profile.getOwnedGameIds();
                if (ownedIds != null) {
                    ownedGameIds.addAll(ownedIds);
                }
                if (adapter != null) {
                    adapter.setOwnedGameIds(ownedGameIds);
                }
                loadWishlist();
            }

            @Override
            public void onError(@NonNull String message) {
                loadWishlist();
            }
        });
    }


    private void loadWishlist() {
        if (progressBar != null && adapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        }

        customerRepository.getWishlist(new CustomerRepository.WishlistCallback() {
            @Override
            public void onSuccess(@NonNull List<GameDto> games) {
                if (getContext() == null) return;
                
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                adapter.submitList(games);
                
                if (emptyStateText != null) {
                    emptyStateText.setVisibility(games.isEmpty() ? View.VISIBLE : View.GONE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(games.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onError(@NonNull String message) {
                if (getContext() == null) return;
                
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onGameClick(GameDto game) {
        if (game != null && game.getId() != null && getContext() != null) {
            startActivity(GameDetailActivity.newIntent(requireContext(), game.getId()));
        }
    }
}

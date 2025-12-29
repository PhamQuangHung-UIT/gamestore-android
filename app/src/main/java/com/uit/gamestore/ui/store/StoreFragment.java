package com.uit.gamestore.ui.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.uit.gamestore.MainActivity;
import com.uit.gamestore.R;
import com.uit.gamestore.data.local.TokenManager;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.ui.game_detail.GameDetailActivity;
import com.uit.gamestore.ui.login.LoginActivity;

import java.util.List;

public class StoreFragment extends Fragment {

    private StoreViewModel storeViewModel;
    private GameListAdapter saleGamesAdapter;
    private GameListAdapter allGamesAdapter;

    // Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView saleRecyclerView;
    private RecyclerView allGamesRecyclerView;
    private TextView emptyStateSale;
    private TextView emptyStateAllGames;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private TextView errorMessage;
    private Button retryButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeViewModel = new ViewModelProvider(this).get(StoreViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_store, container, false);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showStoreToolbar();
        }

        initViews(root);
        setupToolbarButtons();
        setupRecyclerViews();
        setupSwipeRefresh();
        observeViewModel();
        loadData();

        return root;
    }

    private void initViews(View root) {
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        saleRecyclerView = root.findViewById(R.id.recyclerView_christmasSpecial);
        allGamesRecyclerView = root.findViewById(R.id.recycleView_allGames);
        emptyStateSale = root.findViewById(R.id.emptyStateSale);
        emptyStateAllGames = root.findViewById(R.id.emptyStateAllGames);
        progressBar = root.findViewById(R.id.progressBar);
        errorLayout = root.findViewById(R.id.errorLayout);
        errorMessage = root.findViewById(R.id.errorMessage);
        retryButton = root.findViewById(R.id.retryButton);

        retryButton.setOnClickListener(v -> {
            errorLayout.setVisibility(View.GONE);
            loadData();
        });
    }

    private void setupToolbarButtons() {
        if (getActivity() == null) return;

        // Search button
        ImageButton searchButton = getActivity().findViewById(R.id.button_search);
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> showSearchDialog());
        }

        // Saved games button
        ImageButton savedGamesButton = getActivity().findViewById(R.id.button_savedGames);
        if (savedGamesButton != null) {
            savedGamesButton.setOnClickListener(v -> {
                if (TokenManager.getInstance().isLoggedIn()) {
                    // Navigate to saved games tab
                    Navigation.findNavController(requireView()).navigate(R.id.navigation_your_games);
                } else {
                    Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                }
            });
        }
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.search_games);

        final EditText input = new EditText(requireContext());
        input.setHint(R.string.search_hint);
        input.setPadding(48, 32, 48, 32);
        builder.setView(input);

        builder.setPositiveButton(R.string.search, (dialog, which) -> {
            String query = input.getText().toString().trim();
            if (!query.isEmpty()) {
                storeViewModel.searchGames(query);
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.setNeutralButton(R.string.clear, (dialog, which) -> {
            storeViewModel.loadAllGames();
        });

        builder.show();
    }

    private void setupRecyclerViews() {
        // Sale games - horizontal scroll
        saleRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        saleGamesAdapter = new GameListAdapter(CategorySection.DIRECTION_HORIZONTAL, requireContext());
        saleGamesAdapter.setOnGameClickListener(this::onGameClick);
        saleGamesAdapter.setOnSaveClickListener(this::onSaveClick);
        saleRecyclerView.setAdapter(saleGamesAdapter);

        // All games - vertical list
        allGamesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        allGamesRecyclerView.setNestedScrollingEnabled(false);
        allGamesAdapter = new GameListAdapter(CategorySection.DIRECTION_VERTICAL, requireContext());
        allGamesAdapter.setOnGameClickListener(this::onGameClick);
        allGamesRecyclerView.setAdapter(allGamesAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        swipeRefreshLayout.setColorSchemeResources(
                com.google.android.material.R.color.design_default_color_primary,
                com.google.android.material.R.color.design_default_color_secondary
        );
    }

    private void observeViewModel() {
        storeViewModel.getAllGames().observe(getViewLifecycleOwner(), this::onAllGamesUpdate);
        storeViewModel.getSaleGames().observe(getViewLifecycleOwner(), this::onSaleGamesUpdate);
        storeViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::onLoadingChanged);
        storeViewModel.getError().observe(getViewLifecycleOwner(), this::onError);
    }

    private void loadData() {
        storeViewModel.loadAllGames();
        storeViewModel.loadSaleGames();
    }

    private void onAllGamesUpdate(List<GameDto> games) {
        if (games != null) {
            allGamesAdapter.submitList(games);
            emptyStateAllGames.setVisibility(games.isEmpty() ? View.VISIBLE : View.GONE);
            allGamesRecyclerView.setVisibility(games.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void onSaleGamesUpdate(List<GameDto> games) {
        if (games != null) {
            saleGamesAdapter.submitList(games);
            emptyStateSale.setVisibility(games.isEmpty() ? View.VISIBLE : View.GONE);
            saleRecyclerView.setVisibility(games.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void onLoadingChanged(Boolean isLoading) {
        if (isLoading == null) return;

        swipeRefreshLayout.setRefreshing(false);

        if (isLoading && allGamesAdapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void onError(String message) {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        if (message != null && !message.isEmpty()) {
            // Show error layout only if no data
            if (allGamesAdapter.getItemCount() == 0) {
                errorMessage.setText(message);
                errorLayout.setVisibility(View.VISIBLE);
            }
            storeViewModel.clearError();
        } else {
            errorLayout.setVisibility(View.GONE);
        }
    }

    private void onGameClick(GameDto game) {
        if (game != null && game.getId() != null && getContext() != null) {
            startActivity(GameDetailActivity.newIntent(requireContext(), game.getId()));
        }
    }

    private void onSaveClick(GameDto game) {
        if (!TokenManager.getInstance().isLoggedIn()) {
            Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return;
        }
        // TODO: Implement save game to wishlist API
        Toast.makeText(requireContext(), R.string.game_saved, Toast.LENGTH_SHORT).show();
    }
}
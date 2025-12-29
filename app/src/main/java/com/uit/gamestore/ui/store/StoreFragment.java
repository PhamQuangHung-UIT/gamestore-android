package com.uit.gamestore.ui.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.uit.gamestore.MainActivity;
import com.uit.gamestore.R;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.ui.game_detail.GameDetailActivity;

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

    private void setupRecyclerViews() {
        // Sale games - horizontal scroll
        saleRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        saleGamesAdapter = new GameListAdapter(CategorySection.DIRECTION_HORIZONTAL, requireContext());
        saleGamesAdapter.setOnGameClickListener(this::onGameClick);
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
                R.color.design_default_color_primary,
                R.color.design_default_color_secondary
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
}
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
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
    private NestedScrollView contentScrollView;
    private LinearLayout skeletonLayout;
    private RecyclerView saleRecyclerView;
    private RecyclerView allGamesRecyclerView;
    private android.widget.TextView emptyStateSale;
    private android.widget.TextView emptyStateAllGames;
    private LinearLayout errorLayout;
    private android.widget.TextView errorMessage;
    private Button retryButton;
    private Chip chipActiveFilter;

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
        contentScrollView = root.findViewById(R.id.contentScrollView);
        skeletonLayout = root.findViewById(R.id.skeletonLayout);
        saleRecyclerView = root.findViewById(R.id.recyclerView_christmasSpecial);
        allGamesRecyclerView = root.findViewById(R.id.recycleView_allGames);
        emptyStateSale = root.findViewById(R.id.emptyStateSale);
        emptyStateAllGames = root.findViewById(R.id.emptyStateAllGames);
        errorLayout = root.findViewById(R.id.errorLayout);
        errorMessage = root.findViewById(R.id.errorMessage);
        retryButton = root.findViewById(R.id.retryButton);
        chipActiveFilter = root.findViewById(R.id.chipActiveFilter);

        retryButton.setOnClickListener(v -> {
            errorLayout.setVisibility(View.GONE);
            loadData();
        });

        chipActiveFilter.setOnCloseIconClickListener(v -> {
            storeViewModel.resetFilters();
        });
    }

    private void setupToolbarButtons() {
        if (getActivity() == null) return;

        ImageButton searchButton = getActivity().findViewById(R.id.button_search);
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> showSearchFilterDialog());
        }

        ImageButton savedGamesButton = getActivity().findViewById(R.id.button_savedGames);
        if (savedGamesButton != null) {
            savedGamesButton.setOnClickListener(v -> {
                if (TokenManager.getInstance().isLoggedIn()) {
                    Navigation.findNavController(requireView()).navigate(R.id.navigation_your_games);
                } else {
                    Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                }
            });
        }
    }

    private void showSearchFilterDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search_filter, null);
        dialog.setContentView(dialogView);

        // Get views
        ImageButton buttonClose = dialogView.findViewById(R.id.buttonClose);
        TextInputEditText editTextSearch = dialogView.findViewById(R.id.editTextSearch);
        ChipGroup chipGroupGenre = dialogView.findViewById(R.id.chipGroupGenre);
        ChipGroup chipGroupSort = dialogView.findViewById(R.id.chipGroupSort);
        Button buttonReset = dialogView.findViewById(R.id.buttonReset);
        Button buttonApply = dialogView.findViewById(R.id.buttonApply);

        // Pre-fill current values
        if (storeViewModel.getCurrentSearch() != null) {
            editTextSearch.setText(storeViewModel.getCurrentSearch());
        }

        // Set current genre selection
        String currentGenre = storeViewModel.getCurrentGenre();
        if (currentGenre != null) {
            switch (currentGenre.toLowerCase()) {
                case "action":
                    chipGroupGenre.check(R.id.chipAction);
                    break;
                case "adventure":
                    chipGroupGenre.check(R.id.chipAdventure);
                    break;
                case "rpg":
                    chipGroupGenre.check(R.id.chipRPG);
                    break;
                case "strategy":
                    chipGroupGenre.check(R.id.chipStrategy);
                    break;
                case "sports":
                    chipGroupGenre.check(R.id.chipSports);
                    break;
                default:
                    chipGroupGenre.check(R.id.chipAll);
            }
        }

        // Set current sort selection
        StoreViewModel.SortOption currentSort = storeViewModel.getCurrentSort();
        switch (currentSort) {
            case PRICE_LOW_TO_HIGH:
                chipGroupSort.check(R.id.chipSortPriceLow);
                break;
            case PRICE_HIGH_TO_LOW:
                chipGroupSort.check(R.id.chipSortPriceHigh);
                break;
            case NAME_AZ:
                chipGroupSort.check(R.id.chipSortName);
                break;
            default:
                chipGroupSort.check(R.id.chipSortNewest);
        }

        buttonClose.setOnClickListener(v -> dialog.dismiss());

        buttonReset.setOnClickListener(v -> {
            editTextSearch.setText("");
            chipGroupGenre.check(R.id.chipAll);
            chipGroupSort.check(R.id.chipSortNewest);
        });

        buttonApply.setOnClickListener(v -> {
            String search = editTextSearch.getText() != null ? 
                editTextSearch.getText().toString().trim() : null;

            // Get selected genre
            String genre = null;
            int genreId = chipGroupGenre.getCheckedChipId();
            if (genreId == R.id.chipAction) genre = "Action";
            else if (genreId == R.id.chipAdventure) genre = "Adventure";
            else if (genreId == R.id.chipRPG) genre = "RPG";
            else if (genreId == R.id.chipStrategy) genre = "Strategy";
            else if (genreId == R.id.chipSports) genre = "Sports";

            // Get selected sort
            StoreViewModel.SortOption sort = StoreViewModel.SortOption.NEWEST;
            int sortId = chipGroupSort.getCheckedChipId();
            if (sortId == R.id.chipSortPriceLow) sort = StoreViewModel.SortOption.PRICE_LOW_TO_HIGH;
            else if (sortId == R.id.chipSortPriceHigh) sort = StoreViewModel.SortOption.PRICE_HIGH_TO_LOW;
            else if (sortId == R.id.chipSortName) sort = StoreViewModel.SortOption.NAME_AZ;

            storeViewModel.applySearchAndFilter(search, genre, sort);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupRecyclerViews() {
        saleRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        saleGamesAdapter = new GameListAdapter(CategorySection.DIRECTION_HORIZONTAL, requireContext());
        saleGamesAdapter.setOnGameClickListener(this::onGameClick);
        saleGamesAdapter.setOnSaveClickListener(this::onSaveClick);
        saleRecyclerView.setAdapter(saleGamesAdapter);

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
                R.color.teal_200,
                R.color.teal_700
        );
    }

    private void observeViewModel() {
        storeViewModel.getAllGames().observe(getViewLifecycleOwner(), this::onAllGamesUpdate);
        storeViewModel.getSaleGames().observe(getViewLifecycleOwner(), this::onSaleGamesUpdate);
        storeViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::onLoadingChanged);
        storeViewModel.getError().observe(getViewLifecycleOwner(), this::onError);
        storeViewModel.getActiveFilter().observe(getViewLifecycleOwner(), this::onActiveFilterChanged);
    }

    private void loadData() {
        storeViewModel.loadAllGames();
        storeViewModel.loadSaleGames();
    }

    private void onAllGamesUpdate(List<GameDto> games) {
        if (games == null) return;
        
        allGamesAdapter.submitList(games);
        
        // Only show empty state if not loading
        Boolean isLoading = storeViewModel.getIsLoading().getValue();
        if (isLoading != null && !isLoading) {
            emptyStateAllGames.setVisibility(games.isEmpty() ? View.VISIBLE : View.GONE);
            allGamesRecyclerView.setVisibility(games.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void onSaleGamesUpdate(List<GameDto> games) {
        if (games == null) return;
        
        saleGamesAdapter.submitList(games);
        emptyStateSale.setVisibility(games.isEmpty() ? View.VISIBLE : View.GONE);
        saleRecyclerView.setVisibility(games.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void onLoadingChanged(Boolean isLoading) {
        if (isLoading == null) return;

        swipeRefreshLayout.setRefreshing(false);

        if (isLoading) {
            skeletonLayout.setVisibility(View.VISIBLE);
            contentScrollView.setVisibility(View.GONE);
            emptyStateAllGames.setVisibility(View.GONE);
        } else {
            skeletonLayout.setVisibility(View.GONE);
            contentScrollView.setVisibility(View.VISIBLE);
        }
    }

    private void onActiveFilterChanged(String filterText) {
        if (filterText != null && !filterText.isEmpty()) {
            chipActiveFilter.setText(filterText);
            chipActiveFilter.setVisibility(View.VISIBLE);
        } else {
            chipActiveFilter.setVisibility(View.GONE);
        }
    }

    private void onError(String message) {
        swipeRefreshLayout.setRefreshing(false);
        skeletonLayout.setVisibility(View.GONE);

        if (message != null && !message.isEmpty()) {
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
        Toast.makeText(requireContext(), R.string.game_saved, Toast.LENGTH_SHORT).show();
    }
}

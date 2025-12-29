package com.uit.gamestore.ui.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uit.gamestore.MainActivity;
import com.uit.gamestore.R;
import com.uit.gamestore.data.remote.dto.GameDto;

import java.util.List;

public class StoreFragment extends Fragment {

    private StoreViewModel storeViewModel;
    private GameListAdapter saleGamesAdapter;
    private GameListAdapter allGamesAdapter;

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

        setupRecyclerViews(root);
        observeViewModel();
        loadData();

        return root;
    }

    private void setupRecyclerViews(View root) {
        // Sale games - horizontal scroll
        RecyclerView saleRecyclerView = root.findViewById(R.id.recyclerView_christmasSpecial);
        saleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        saleGamesAdapter = new GameListAdapter(CategorySection.DIRECTION_HORIZONTAL, requireContext());
        saleGamesAdapter.setOnGameClickListener(this::onGameClick);
        saleRecyclerView.setAdapter(saleGamesAdapter);

        // All games - vertical list
        RecyclerView allGamesRecyclerView = root.findViewById(R.id.recycleView_allGames);
        allGamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        allGamesAdapter = new GameListAdapter(CategorySection.DIRECTION_VERTICAL, requireContext());
        allGamesAdapter.setOnGameClickListener(this::onGameClick);
        allGamesRecyclerView.setAdapter(allGamesAdapter);
    }

    private void observeViewModel() {
        storeViewModel.getAllGames().observe(getViewLifecycleOwner(), this::onAllGamesUpdate);
        storeViewModel.getSaleGames().observe(getViewLifecycleOwner(), this::onSaleGamesUpdate);
        storeViewModel.getError().observe(getViewLifecycleOwner(), this::onError);
    }

    private void loadData() {
        storeViewModel.loadAllGames();
        storeViewModel.loadSaleGames();
    }

    private void onAllGamesUpdate(List<GameDto> games) {
        if (games != null) {
            allGamesAdapter.submitList(games);
        }
    }

    private void onSaleGamesUpdate(List<GameDto> games) {
        if (games != null) {
            saleGamesAdapter.submitList(games);
        }
    }

    private void onError(String message) {
        if (message != null && !message.isEmpty() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            storeViewModel.clearError();
        }
    }

    private void onGameClick(GameDto game) {
        // TODO: Navigate to game detail screen
        if (game != null && getContext() != null) {
            Toast.makeText(getContext(), "Selected: " + game.getName(), Toast.LENGTH_SHORT).show();
        }
    }
}
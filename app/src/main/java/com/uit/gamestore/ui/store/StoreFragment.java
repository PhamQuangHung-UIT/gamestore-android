package com.uit.gamestore.ui.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uit.gamestore.MainActivity;
import com.uit.gamestore.R;
import com.uit.gamestore.domain.model.Game;

import java.util.List;

public class StoreFragment extends Fragment {

    private MainActivity mActivity;

    public static final String CHRISTMAS = "Christmas";

    public static final String NEWEST = "Newest";

    private GameListAdapter m_christmasAdapter, m_newestGameAdapter, m_allGamesAdapter;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StoreViewModel storeViewModel =
                new ViewModelProvider(this).get(StoreViewModel.class);

        View root = inflater.inflate(R.layout.fragment_store, container, false);

        mActivity = (MainActivity) getActivity();

        if (mActivity != null) {
            mActivity.showStoreToolbar();
        }

        TextView label_christmasSpecial = root.findViewById(R.id.label_christmasSpecial);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView_christmasSpecial);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        m_christmasAdapter = new GameListAdapter(CategorySection.DIRECTION_HORIZONTAL, getContext());
        recyclerView.setAdapter(m_christmasAdapter);

        TextView label_newest = root.findViewById(R.id.label_newest);

        recyclerView = root.findViewById(R.id.recycleView_newest);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.HORIZONTAL, false));
        m_newestGameAdapter = new GameListAdapter(CategorySection.DIRECTION_BOTH, getContext());
        recyclerView.setAdapter(m_newestGameAdapter);

        TextView label_allGames = root.findViewById(R.id.label_allGames);

        recyclerView = root.findViewById(R.id.recycleView_allGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        m_allGamesAdapter = new GameListAdapter(CategorySection.DIRECTION_VERTICAL, getContext());
        recyclerView.setAdapter(m_allGamesAdapter);

        storeViewModel.getCategorySections().observe(getViewLifecycleOwner(), this::onCategorySectionsUpdate);
        storeViewModel.getGameLists().observe(getViewLifecycleOwner(), this::onGameListsUpdate);


        storeViewModel.getAllGames();
        storeViewModel.getGamesByCategory(CHRISTMAS);
        storeViewModel.getGamesByCategory(NEWEST);

        return root;
    }

    private void onGameListsUpdate(List<Game> games) {
        m_allGamesAdapter.submitList(games);
    }

    private void onCategorySectionsUpdate(List<CategorySection> categorySections) {
        for (CategorySection section : categorySections) {
            if (section.getCategory().equals(CHRISTMAS)) {
                m_christmasAdapter.submitList(section.getListGame());
            } else if (section.getCategory().equals(NEWEST)) {
                m_newestGameAdapter.submitList(section.getListGame());
            }
        }
    }
}
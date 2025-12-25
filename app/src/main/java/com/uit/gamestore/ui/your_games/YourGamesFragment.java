package com.uit.gamestore.ui.your_games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.uit.gamestore.R;

public class YourGamesFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        YourGamesViewModel notificationsViewModel =
                new ViewModelProvider(this).get(YourGamesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        return root;
    }
}
package com.uit.gamestore.ui.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.uit.gamestore.R;

public class StoreFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StoreViewModel storeViewModel =
                new ViewModelProvider(this).get(StoreViewModel.class);

        View root = inflater.inflate(R.layout.fragment_store, container, false);

        final TextView textView = root.findViewById(R.id.text_home);
        storeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}
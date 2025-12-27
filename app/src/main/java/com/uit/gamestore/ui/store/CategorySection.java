package com.uit.gamestore.ui.store;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uit.gamestore.domain.model.Game;

import java.util.List;

public class CategorySection {

    public static final int DIRECTION_HORIZONTAL = 0;
    public static final int DIRECTION_VERTICAL = 1;

    public static final int DIRECTION_BOTH = 2;

    private final String m_category;

    private final List<Game> m_listGame;

    public CategorySection(String category, List<Game> listGame) {
        m_category = category;
        m_listGame = listGame;
    }

    public String getCategory() {
        return m_category;
    }

    public List<Game> getListGame() {
        return m_listGame;
    }
}

package com.uit.gamestore.ui.store;

import com.uit.gamestore.domain.model.Game;

import java.util.List;

public class CategorySection {

    public static final int DIRECTION_HORIZONTAL = 0;
    public static final int DIRECTION_VERTICAL = 1;
    public static final int DIRECTION_BOTH = 2;

    private final String category;
    private final List<Game> listGame;

    public CategorySection(String category, List<Game> listGame) {
        this.category = category;
        this.listGame = listGame;
    }

    public String getCategory() {
        return category;
    }

    public List<Game> getListGame() {
        return listGame;
    }
}

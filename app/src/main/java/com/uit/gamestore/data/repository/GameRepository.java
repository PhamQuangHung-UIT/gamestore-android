package com.uit.gamestore.data.repository;

import com.uit.gamestore.data.local.MockGameAPI;
import com.uit.gamestore.domain.model.Game;

import java.util.List;

public class GameRepository {
    public List<Game> getAllGames() {
        return MockGameAPI.getInstance().getAllGames();
    }


    public List<Game> getGamesByCategory(String category) {
        return MockGameAPI.getInstance().getGamesByCategory(category);
    }
}

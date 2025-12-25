package com.uit.gamestore.domain.model;

import java.util.List;

public class User {
    private final String id;
    private final String email;

    private final String name;

    private final List<Game> savedGames;

    private final List<Game> purchasedGames;

    public User(String id, String email, String name, List<Game> savedGames, List<Game> purchasedGames) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.savedGames = savedGames;
        this.purchasedGames = purchasedGames;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public List<Game> getSavedGames() {
        return savedGames;
    }

    public List<Game> getPurchasedGames() {
        return purchasedGames;
    }
}

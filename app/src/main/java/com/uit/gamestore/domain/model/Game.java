package com.uit.gamestore.domain.model;

import java.util.List;

public class Game {
    private String id;
    private String name;

    private String shortDescription;

    private String longDescription;

    private Price price;

    private String iconUrl;

    private String bannerUrl;

    private List<String> galleryUrls;

    private String minimumAgeRating;

    public static class Price {
        private String currency;
        private double value;
    }
}

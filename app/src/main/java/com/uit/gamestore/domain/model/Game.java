package com.uit.gamestore.domain.model;

import java.util.List;

public class Game {
    private final String id;
    private final String name;
    private final String shortDescription;

    private final String longDescription;

    private final Price price;

    private final String iconUrl;

    private final String bannerUrl;

    private final List<String> galleryUrls;

    private final String minimumAgeRating;

    public static class Price {
        private final String currency;
        private final double value;

        public Price(String currency, double value) {
            this.currency = currency;
            this.value = value;
        }

        public String getCurrency() {
            return currency;
        }

        public double getValue() {
            return value;
        }
    }

    public Game(
            String id,
            String name,
            String shortDescription,
            String longDescription,
            Price price,
            String iconUrl,
            String bannerUrl,
            List<String> galleryUrls,
            String minimumAgeRating
    ) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.price = price;
        this.iconUrl = iconUrl;
        this.bannerUrl = bannerUrl;
        this.galleryUrls = galleryUrls;
        this.minimumAgeRating = minimumAgeRating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Price getPrice() {
        return price;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public List<String> getGalleryUrls() {
        return galleryUrls;
    }

    public String getMinimumAgeRating() {
        return minimumAgeRating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id.equals(game.id);
    }
}

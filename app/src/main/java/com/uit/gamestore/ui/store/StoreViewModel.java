package com.uit.gamestore.ui.store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.repository.CustomerRepository;
import com.uit.gamestore.data.repository.GameRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StoreViewModel extends ViewModel {

    public enum SortOption {
        NEWEST, PRICE_LOW_TO_HIGH, PRICE_HIGH_TO_LOW, NAME_AZ
    }

    public interface WishlistCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    private final MutableLiveData<List<GameDto>> allGames = new MutableLiveData<>();
    private final MutableLiveData<List<GameDto>> saleGames = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> activeFilter = new MutableLiveData<>();

    private final GameRepository gameRepository = new GameRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();

    // Store original data for filtering/sorting
    private List<GameDto> originalGames = new ArrayList<>();
    private String currentSearch = null;
    private String currentGenre = null;
    private SortOption currentSort = SortOption.NEWEST;

    public void loadAllGames() {
        isLoading.setValue(true);
        error.setValue(null);
        currentSearch = null;
        currentGenre = null;
        currentSort = SortOption.NEWEST;
        activeFilter.setValue(null);

        gameRepository.getAllGames(new GameRepository.GamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games) {
                originalGames = games != null ? new ArrayList<>(games) : new ArrayList<>();
                allGames.postValue(originalGames);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void loadSaleGames() {
        gameRepository.getDiscountedGames(new GameRepository.GamesCallback() {
            @Override
            public void onSuccess(List<GameDto> games) {
                saleGames.postValue(games != null ? games : Collections.emptyList());
            }

            @Override
            public void onError(String message) {
                // Don't show error for sale games, just leave empty
            }
        });
    }

    public void searchGames(String query) {
        currentSearch = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        applyFiltersAndSort();
    }

    public void filterByGenre(String genre) {
        currentGenre = (genre != null && !genre.isEmpty() && !genre.equals("All")) ? genre : null;
        applyFiltersAndSort();
    }

    public void setSortOption(SortOption sortOption) {
        currentSort = sortOption;
        applyFiltersAndSort();
    }

    public void applySearchAndFilter(String search, String genre, SortOption sort) {
        currentSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        currentGenre = (genre != null && !genre.isEmpty() && !genre.equals("All")) ? genre : null;
        currentSort = sort != null ? sort : SortOption.NEWEST;
        applyFiltersAndSort();
    }

    public void resetFilters() {
        currentSearch = null;
        currentGenre = null;
        currentSort = SortOption.NEWEST;
        activeFilter.setValue(null);
        allGames.setValue(originalGames);
    }

    private void applyFiltersAndSort() {
        List<GameDto> filtered = new ArrayList<>(originalGames);

        // Apply search filter
        if (currentSearch != null) {
            String searchLower = currentSearch.toLowerCase();
            filtered.removeIf(game -> 
                game.getName() == null || !game.getName().toLowerCase().contains(searchLower));
        }

        // Apply genre filter
        if (currentGenre != null) {
            filtered.removeIf(game -> 
                game.getGenre() == null || !game.getGenre().equalsIgnoreCase(currentGenre));
        }

        // Apply sorting
        switch (currentSort) {
            case PRICE_LOW_TO_HIGH:
                filtered.sort(Comparator.comparingDouble(GameDto::getEffectivePrice));
                break;
            case PRICE_HIGH_TO_LOW:
                filtered.sort((a, b) -> Double.compare(b.getEffectivePrice(), a.getEffectivePrice()));
                break;
            case NAME_AZ:
                filtered.sort((a, b) -> {
                    String nameA = a.getName() != null ? a.getName() : "";
                    String nameB = b.getName() != null ? b.getName() : "";
                    return nameA.compareToIgnoreCase(nameB);
                });
                break;
            case NEWEST:
            default:
                // Keep original order (newest first from API)
                break;
        }

        // Update active filter indicator
        updateActiveFilterText();

        allGames.setValue(filtered);
    }

    private void updateActiveFilterText() {
        StringBuilder filterText = new StringBuilder();
        
        if (currentSearch != null) {
            filterText.append("\"").append(currentSearch).append("\"");
        }
        
        if (currentGenre != null) {
            if (filterText.length() > 0) filterText.append(" • ");
            filterText.append(currentGenre);
        }
        
        if (currentSort != SortOption.NEWEST) {
            if (filterText.length() > 0) filterText.append(" • ");
            switch (currentSort) {
                case PRICE_LOW_TO_HIGH:
                    filterText.append("Price ↑");
                    break;
                case PRICE_HIGH_TO_LOW:
                    filterText.append("Price ↓");
                    break;
                case NAME_AZ:
                    filterText.append("A-Z");
                    break;
            }
        }

        activeFilter.setValue(filterText.length() > 0 ? filterText.toString() : null);
    }

    public void clearError() {
        error.setValue(null);
    }

    public LiveData<List<GameDto>> getAllGames() {
        return allGames;
    }

    public LiveData<List<GameDto>> getSaleGames() {
        return saleGames;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getActiveFilter() {
        return activeFilter;
    }

    public String getCurrentSearch() {
        return currentSearch;
    }

    public String getCurrentGenre() {
        return currentGenre;
    }

    public SortOption getCurrentSort() {
        return currentSort;
    }

    public void addToWishlist(String gameId, WishlistCallback callback) {
        customerRepository.addToWishlist(gameId, new CustomerRepository.WishlistActionCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}
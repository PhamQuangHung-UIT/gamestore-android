package com.uit.gamestore.ui.game_detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.ReviewDto;
import com.uit.gamestore.data.repository.CustomerRepository;
import com.uit.gamestore.data.repository.GameRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameDetailViewModel extends ViewModel {

    private final MutableLiveData<GameDto> game = new MutableLiveData<>();
    private final MutableLiveData<List<ReviewDto>> reviews = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaved = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSaveLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> saveMessage = new MutableLiveData<>();

    private final GameRepository gameRepository = new GameRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final Set<String> wishlistGameIds = new HashSet<>();
    private String currentGameId;

    public void loadGame(@NonNull String gameId) {
        if (gameId.isEmpty()) {
            error.setValue("Invalid game ID");
            return;
        }

        currentGameId = gameId;
        isLoading.setValue(true);
        error.setValue(null);

        gameRepository.getGameById(gameId, new GameRepository.GameDetailCallback() {
            @Override
            public void onSuccess(GameDto gameDto) {
                game.postValue(gameDto);
                isLoading.postValue(false);
                // Use reviews from game response if available
                if (gameDto.getReviews() != null && !gameDto.getReviews().isEmpty()) {
                    // Convert GameDto.ReviewInfo to ReviewDto format for adapter
                    java.util.List<ReviewDto> reviewList = new java.util.ArrayList<>();
                    for (GameDto.ReviewInfo reviewInfo : gameDto.getReviews()) {
                        reviewList.add(ReviewDto.fromGameReviewInfo(reviewInfo));
                    }
                    reviews.postValue(reviewList);
                } else {
                    reviews.postValue(Collections.emptyList());
                }
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void loadWishlist() {
        customerRepository.getWishlist(new CustomerRepository.WishlistCallback() {
            @Override
            public void onSuccess(@NonNull List<GameDto> games) {
                wishlistGameIds.clear();
                for (GameDto g : games) {
                    wishlistGameIds.add(g.getId());
                }
                // Update saved state for current game
                if (currentGameId != null) {
                    isSaved.postValue(wishlistGameIds.contains(currentGameId));
                }
            }

            @Override
            public void onError(@NonNull String message) {
                // Silently fail - user might not be logged in
            }
        });
    }

    public void toggleSave() {
        if (currentGameId == null || currentGameId.isEmpty()) return;
        
        // Use the local wishlistGameIds set as source of truth
        boolean currentlySaved = wishlistGameIds.contains(currentGameId);
        
        isSaveLoading.setValue(true);
        
        if (currentlySaved) {
            // Remove from wishlist
            customerRepository.removeFromWishlist(currentGameId, new CustomerRepository.WishlistActionCallback() {
                @Override
                public void onSuccess(@NonNull String message) {
                    wishlistGameIds.remove(currentGameId);
                    isSaved.postValue(false);
                    isSaveLoading.postValue(false);
                    saveMessage.postValue("Removed from wishlist");
                }

                @Override
                public void onError(@NonNull String message) {
                    isSaveLoading.postValue(false);
                    saveMessage.postValue(message);
                }
            });
        } else {
            // Add to wishlist
            customerRepository.addToWishlist(currentGameId, new CustomerRepository.WishlistActionCallback() {
                @Override
                public void onSuccess(@NonNull String message) {
                    wishlistGameIds.add(currentGameId);
                    isSaved.postValue(true);
                    isSaveLoading.postValue(false);
                    saveMessage.postValue("Added to wishlist");
                }

                @Override
                public void onError(@NonNull String message) {
                    // If 409 conflict (already in wishlist), update local state
                    if (message.contains("already") || message.contains("409")) {
                        wishlistGameIds.add(currentGameId);
                        isSaved.postValue(true);
                    }
                    isSaveLoading.postValue(false);
                    saveMessage.postValue(message);
                }
            });
        }
    }

    public void clearSaveMessage() {
        saveMessage.setValue(null);
    }

    public void refresh() {
        if (currentGameId != null && !currentGameId.isEmpty()) {
            loadGame(currentGameId);
        }
    }

    public void clearError() {
        error.setValue(null);
    }

    public LiveData<GameDto> getGame() {
        return game;
    }

    public LiveData<List<ReviewDto>> getReviews() {
        return reviews;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsSaved() {
        return isSaved;
    }

    public LiveData<Boolean> getIsSaveLoading() {
        return isSaveLoading;
    }

    public LiveData<String> getSaveMessage() {
        return saveMessage;
    }
}

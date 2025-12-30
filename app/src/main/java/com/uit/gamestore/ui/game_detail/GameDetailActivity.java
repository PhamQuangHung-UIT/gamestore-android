package com.uit.gamestore.ui.game_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.uit.gamestore.R;
import com.uit.gamestore.data.local.TokenManager;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.OrderDto;
import com.uit.gamestore.data.remote.dto.ReviewDto;
import com.uit.gamestore.data.repository.CustomerRepository;
import com.uit.gamestore.ui.login.LoginActivity;

import java.util.List;
import java.util.Locale;

public class GameDetailActivity extends AppCompatActivity {

    private static final String EXTRA_GAME_ID = "game_id";

    private GameDetailViewModel viewModel;
    private ReviewAdapter reviewAdapter;
    private final CustomerRepository customerRepository = new CustomerRepository();
    private GameDto currentGame;

    // Views
    private ImageView imageViewBanner;
    private ShapeableImageView imageViewIcon;
    private TextView textViewGameName;
    private TextView textViewPublisher;
    private TextView textViewRating;
    private TextView textViewReviewCount;
    private TextView textViewPrice;
    private TextView textViewOriginalPrice;
    private TextView textViewDiscount;
    private TextView textViewDescription;
    private TextView textViewGenre;
    private TextView textViewVersion;
    private TextView textViewReleaseDate;
    private TextView textViewNoReviews;
    private Button buttonBuy;
    private ImageButton buttonSave;
    private RecyclerView recyclerViewReviews;
    private ProgressBar progressBar;

    public static Intent newIntent(@NonNull Context context, @NonNull String gameId) {
        Intent intent = new Intent(context, GameDetailActivity.class);
        intent.putExtra(EXTRA_GAME_ID, gameId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        String gameId = getIntent().getStringExtra(EXTRA_GAME_ID);
        if (gameId == null || gameId.isEmpty()) {
            Toast.makeText(this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(GameDetailViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        observeViewModel();

        viewModel.loadGame(gameId);
        
        // Load wishlist if user is logged in
        if (TokenManager.getInstance().isLoggedIn()) {
            viewModel.loadWishlist();
        }
    }

    private void initViews() {
        imageViewBanner = findViewById(R.id.imageViewBanner);
        imageViewIcon = findViewById(R.id.imageViewIcon);
        textViewGameName = findViewById(R.id.textViewGameName);
        textViewPublisher = findViewById(R.id.textViewPublisher);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReviewCount = findViewById(R.id.textViewReviewCount);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewOriginalPrice = findViewById(R.id.textViewOriginalPrice);
        textViewDiscount = findViewById(R.id.textViewDiscount);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewGenre = findViewById(R.id.textViewGenre);
        textViewVersion = findViewById(R.id.textViewVersion);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewNoReviews = findViewById(R.id.textViewNoReviews);
        buttonBuy = findViewById(R.id.buttonBuy);
        buttonSave = findViewById(R.id.buttonSave);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        progressBar = findViewById(R.id.progressBar);

        buttonBuy.setOnClickListener(v -> onBuyClicked());
        buttonSave.setOnClickListener(v -> onSaveClicked());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter();
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewReviews.setNestedScrollingEnabled(false);
    }

    private void observeViewModel() {
        viewModel.getGame().observe(this, this::displayGame);
        viewModel.getReviews().observe(this, this::displayReviews);
        viewModel.getIsLoading().observe(this, this::showLoading);
        viewModel.getError().observe(this, this::showError);
        viewModel.getIsSaved().observe(this, this::updateSaveButton);
        viewModel.getIsSaveLoading().observe(this, this::updateSaveLoading);
        viewModel.getSaveMessage().observe(this, this::showSaveMessage);
    }

    private void displayGame(@Nullable GameDto game) {
        if (game == null) return;
        
        currentGame = game;

        // Images
        loadImage(game.getImageUrl(), imageViewBanner);
        loadImage(game.getImageUrl(), imageViewIcon);

        // Basic info
        textViewGameName.setText(getStringOrDefault(game.getName(), getString(R.string.unknown)));
        
        String publisherName = game.getPublisher() != null ? game.getPublisher().getPublisherName() : null;
        textViewPublisher.setText(getStringOrDefault(publisherName, getString(R.string.unknown)));

        // Rating
        Double rating = game.getAverageRating();
        if (rating != null && rating > 0) {
            // Show as integer if it's a whole number, otherwise show one decimal
            if (rating == Math.floor(rating)) {
                textViewRating.setText(String.format(Locale.getDefault(), "%.0f ★", rating));
            } else {
                textViewRating.setText(String.format(Locale.getDefault(), "%.1f ★", rating));
            }
            textViewRating.setVisibility(View.VISIBLE);
        } else {
            textViewRating.setVisibility(View.GONE);
        }

        Integer reviewCount = game.getReviewCount();
        if (reviewCount != null && reviewCount > 0) {
            textViewReviewCount.setText(String.format(Locale.getDefault(), "(%d)", reviewCount));
            textViewReviewCount.setVisibility(View.VISIBLE);
        } else {
            textViewReviewCount.setVisibility(View.GONE);
        }

        // Price
        displayPrice(game);

        // Details
        textViewDescription.setText(getStringOrDefault(game.getDescription(), ""));
        textViewGenre.setText(getStringOrDefault(game.getGenre(), getString(R.string.unknown)));
        textViewVersion.setText(getStringOrDefault(game.getVersion(), "1.0"));
        
        String releaseDate = game.getReleaseDate();
        if (releaseDate != null && releaseDate.length() >= 4) {
            textViewReleaseDate.setText(releaseDate.substring(0, Math.min(10, releaseDate.length())));
        } else {
            textViewReleaseDate.setText(getString(R.string.unknown));
        }
    }

    private void displayPrice(@NonNull GameDto game) {
        double effectivePrice = game.getEffectivePrice();
        double originalPrice = game.getOriginalPrice();

        if (effectivePrice <= 0) {
            textViewPrice.setText(R.string.free);
            textViewOriginalPrice.setVisibility(View.GONE);
            textViewDiscount.setVisibility(View.GONE);
            buttonBuy.setText(R.string.button_install);
        } else {
            textViewPrice.setText(String.format(Locale.US, "$%.2f", effectivePrice));
            buttonBuy.setText(R.string.buy_now);

            if (game.hasDiscount() && originalPrice > effectivePrice) {
                textViewOriginalPrice.setText(String.format(Locale.US, "$%.2f", originalPrice));
                textViewOriginalPrice.setPaintFlags(textViewOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textViewOriginalPrice.setVisibility(View.VISIBLE);

                int discountPercent = (int) ((1 - effectivePrice / originalPrice) * 100);
                textViewDiscount.setText(String.format(Locale.getDefault(), "-%d%%", discountPercent));
                textViewDiscount.setVisibility(View.VISIBLE);
            } else {
                textViewOriginalPrice.setVisibility(View.GONE);
                textViewDiscount.setVisibility(View.GONE);
            }
        }
    }

    private void displayReviews(@Nullable List<ReviewDto> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            recyclerViewReviews.setVisibility(View.GONE);
            textViewNoReviews.setVisibility(View.VISIBLE);
        } else {
            recyclerViewReviews.setVisibility(View.VISIBLE);
            textViewNoReviews.setVisibility(View.GONE);
            reviewAdapter.submitList(reviews);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showError(@Nullable String error) {
        if (error != null && !error.isEmpty()) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            viewModel.clearError();
        }
    }

    private void onBuyClicked() {
        if (!TokenManager.getInstance().isLoggedIn()) {
            Toast.makeText(this, R.string.login_required, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (currentGame == null) return;

        double price = currentGame.getEffectivePrice();
        String gameName = currentGame.getName();

        // Show payment method dialog
        String[] paymentMethods = {"Wallet", "Credit Card", "PayPal"};
        String[] paymentMethodValues = {"Wallet", "CreditCard", "PayPal"};

        new AlertDialog.Builder(this, R.style.Theme_GameStore_Dialog)
                .setTitle(R.string.select_payment_method)
                .setItems(paymentMethods, (dialog, which) -> {
                    String selectedMethod = paymentMethodValues[which];
                    showConfirmPurchaseDialog(gameName, price, selectedMethod);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showConfirmPurchaseDialog(String gameName, double price, String paymentMethod) {
        String message = getString(R.string.confirm_purchase_message, gameName, price, paymentMethod);

        new AlertDialog.Builder(this, R.style.Theme_GameStore_Dialog)
                .setTitle(R.string.confirm_purchase)
                .setMessage(message)
                .setPositiveButton(R.string.buy_now, (dialog, which) -> processPurchase(paymentMethod))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void processPurchase(String paymentMethod) {
        if (currentGame == null) return;

        buttonBuy.setEnabled(false);
        buttonBuy.setText(R.string.processing);

        customerRepository.createOrder(currentGame.getId(), paymentMethod, new CustomerRepository.OrderCallback() {
            @Override
            public void onSuccess(@NonNull OrderDto order) {
                runOnUiThread(() -> {
                    buttonBuy.setEnabled(true);
                    buttonBuy.setText(R.string.purchased);
                    buttonBuy.setEnabled(false);
                    
                    new AlertDialog.Builder(GameDetailActivity.this, R.style.Theme_GameStore_Dialog)
                            .setTitle(R.string.purchase_success)
                            .setMessage(getString(R.string.purchase_success_message, currentGame.getName()))
                            .setPositiveButton(R.string.close, null)
                            .show();
                });
            }

            @Override
            public void onError(@NonNull String message) {
                runOnUiThread(() -> {
                    buttonBuy.setEnabled(true);
                    buttonBuy.setText(R.string.buy_now);
                    Toast.makeText(GameDetailActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void onSaveClicked() {
        if (!TokenManager.getInstance().isLoggedIn()) {
            Toast.makeText(this, R.string.login_required, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        viewModel.toggleSave();
    }

    private void updateSaveButton(boolean isSaved) {
        if (isSaved) {
            buttonSave.setImageResource(R.drawable.ic_bookmark_filled_24dp);
        } else {
            buttonSave.setImageResource(R.drawable.ic_bookmark_outlined_24dp);
        }
    }

    private void updateSaveLoading(boolean isLoading) {
        buttonSave.setEnabled(!isLoading);
        buttonSave.setAlpha(isLoading ? 0.5f : 1.0f);
    }

    private void showSaveMessage(@Nullable String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            viewModel.clearSaveMessage();
        }
    }

    private void loadImage(@Nullable String url, @NonNull ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_videogame_asset_black_24dp)
                    .error(R.drawable.ic_videogame_asset_black_24dp)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_videogame_asset_black_24dp);
        }
    }

    @NonNull
    private String getStringOrDefault(@Nullable String value, @NonNull String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}

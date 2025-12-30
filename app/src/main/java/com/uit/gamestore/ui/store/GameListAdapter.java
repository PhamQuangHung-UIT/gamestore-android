package com.uit.gamestore.ui.store;

import static com.uit.gamestore.ui.store.CategorySection.DIRECTION_HORIZONTAL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uit.gamestore.R;
import com.uit.gamestore.data.remote.dto.GameDto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

public class GameListAdapter extends ListAdapter<GameDto, GameListAdapter.GameViewHolder> {

    private final int direction;
    private final Context context;
    private OnGameClickListener clickListener;
    private OnSaveClickListener saveClickListener;
    private OnDownloadClickListener downloadClickListener;
    private java.util.Set<String> ownedGameIds = new java.util.HashSet<>();
    private boolean showDownloadButton = false;

    public interface OnGameClickListener {
        void onGameClick(GameDto game);
    }

    public interface OnSaveClickListener {
        void onSaveClick(GameDto game);
    }

    public interface OnDownloadClickListener {
        void onDownloadClick(GameDto game, Button downloadButton, ProgressBar progressBar);
    }

    private static final DiffUtil.ItemCallback<GameDto> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull GameDto oldItem, @NonNull GameDto newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GameDto oldItem, @NonNull GameDto newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId()) &&
                    Objects.equals(oldItem.getName(), newItem.getName()) &&
                    oldItem.getOriginalPrice() == newItem.getOriginalPrice();
        }
    };

    public GameListAdapter(int direction, @NonNull Context context) {
        super(DIFF_CALLBACK);
        this.direction = direction;
        this.context = context;
    }

    public void setOnGameClickListener(@Nullable OnGameClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnSaveClickListener(@Nullable OnSaveClickListener listener) {
        this.saveClickListener = listener;
    }

    public void setOnDownloadClickListener(@Nullable OnDownloadClickListener listener) {
        this.downloadClickListener = listener;
    }

    public void setShowDownloadButton(boolean show) {
        this.showDownloadButton = show;
        notifyDataSetChanged();
    }

    public void setOwnedGameIds(java.util.Set<String> ownedIds) {
        this.ownedGameIds = ownedIds != null ? ownedIds : new java.util.HashSet<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (direction == DIRECTION_HORIZONTAL) {
            return new HorizontalGameViewHolder(inflater.inflate(R.layout.item_game_big, parent, false));
        } else {
            return new VerticalGameViewHolder(inflater.inflate(R.layout.item_game, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        GameDto game = getItem(position);
        if (game == null) return;

        boolean isOwned = ownedGameIds.contains(game.getId());
        holder.bind(game, context, isOwned, showDownloadButton);
        
        // Set click listener on the whole item
        holder.itemView.setOnClickListener(v -> {
            android.util.Log.d("GameListAdapter", "Item clicked: " + game.getName());
            if (clickListener != null) {
                clickListener.onGameClick(game);
            }
        });

        // Set save button listener (for horizontal items)
        if (holder instanceof HorizontalGameViewHolder) {
            HorizontalGameViewHolder hHolder = (HorizontalGameViewHolder) holder;
            if (hHolder.buttonSave != null) {
                hHolder.buttonSave.setOnClickListener(v -> {
                    android.util.Log.d("GameListAdapter", "Save button clicked: " + game.getName());
                    if (saveClickListener != null) {
                        saveClickListener.onSaveClick(game);
                    }
                });
            }
        }

        // Set download button listener (for vertical items with owned games)
        if (holder instanceof VerticalGameViewHolder && isOwned && showDownloadButton) {
            VerticalGameViewHolder vHolder = (VerticalGameViewHolder) holder;
            if (vHolder.buttonDownload != null && downloadClickListener != null) {
                vHolder.buttonDownload.setOnClickListener(v -> {
                    android.util.Log.d("GameListAdapter", "Download button clicked: " + game.getName());
                    downloadClickListener.onDownloadClick(game, vHolder.buttonDownload, vHolder.progressBarDownload);
                });
            }
        }

        // Make purchase button also trigger game click (unless owned)
        if (holder.buttonPurchase != null && !isOwned) {
            holder.buttonPurchase.setOnClickListener(v -> {
                android.util.Log.d("GameListAdapter", "Purchase button clicked: " + game.getName());
                if (clickListener != null) {
                    clickListener.onGameClick(game);
                }
            });
        }
    }

    public static String formatPrice(double price, @NonNull Locale locale) {
        if (price <= 0) {
            return "Free";
        }
        try {
            BigDecimal amount = BigDecimal.valueOf(price);
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
            formatter.setCurrency(Currency.getInstance("USD"));
            return formatter.format(amount);
        } catch (Exception e) {
            return String.format(locale, "$%.2f", price);
        }
    }

    public static abstract class GameViewHolder extends RecyclerView.ViewHolder {

        protected final TextView labelGameName;
        protected final ImageView imageViewGameIcon;
        protected final ImageView imageViewMinimumAge;
        protected final Button buttonPurchase;
        protected final TextView textViewGenre;
        protected final TextView textViewRating;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            labelGameName = itemView.findViewById(R.id.textView_gameName);
            imageViewGameIcon = itemView.findViewById(R.id.imageView_gameIcon);
            imageViewMinimumAge = itemView.findViewById(R.id.imageView_ageRating);
            buttonPurchase = itemView.findViewById(R.id.button_installGame);
            textViewGenre = itemView.findViewById(R.id.textView_genre);
            textViewRating = itemView.findViewById(R.id.textView_rating);
        }

        public void bind(@NonNull GameDto game, @NonNull Context context) {
            bind(game, context, false, false);
        }

        public void bind(@NonNull GameDto game, @NonNull Context context, boolean isOwned) {
            bind(game, context, isOwned, false);
        }

        public void bind(@NonNull GameDto game, @NonNull Context context, boolean isOwned, boolean showDownload) {
            // Set game name
            String name = game.getName();
            labelGameName.setText(name != null ? name : "Unknown Game");

            // Load image
            String imageUrl = game.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_videogame_asset_black_24dp)
                        .error(R.drawable.ic_videogame_asset_black_24dp)
                        .into(imageViewGameIcon);
            } else {
                imageViewGameIcon.setImageResource(R.drawable.ic_videogame_asset_black_24dp);
            }

            // Genre
            if (textViewGenre != null) {
                String genre = game.getGenre();
                if (genre != null && !genre.isEmpty()) {
                    textViewGenre.setText(genre);
                    textViewGenre.setVisibility(View.VISIBLE);
                } else {
                    textViewGenre.setVisibility(View.GONE);
                }
            }

            // Rating
            if (textViewRating != null) {
                Double rating = game.getAverageRating();
                if (rating != null && rating > 0) {
                    textViewRating.setText(String.format(Locale.getDefault(), "%.1f ★", rating));
                    textViewRating.setVisibility(View.VISIBLE);
                } else {
                    textViewRating.setVisibility(View.GONE);
                }
            }

            // Hide age rating
            if (imageViewMinimumAge != null) {
                imageViewMinimumAge.setVisibility(View.GONE);
            }

            bindPrice(game, isOwned, showDownload);
        }

        protected void bindPrice(@NonNull GameDto game, boolean isOwned, boolean showDownload) {
            if (buttonPurchase == null) return;

            if (isOwned) {
                if (showDownload) {
                    // Hide purchase button, show download button in subclass
                    buttonPurchase.setVisibility(View.GONE);
                } else {
                    buttonPurchase.setVisibility(View.VISIBLE);
                    buttonPurchase.setText(R.string.owned);
                    buttonPurchase.setEnabled(false);
                    buttonPurchase.setAlpha(0.7f);
                }
            } else {
                buttonPurchase.setVisibility(View.VISIBLE);
                buttonPurchase.setEnabled(true);
                buttonPurchase.setAlpha(1.0f);
                double effectivePrice = game.getEffectivePrice();
                if (effectivePrice <= 0) {
                    buttonPurchase.setText(R.string.button_install);
                } else {
                    buttonPurchase.setText(formatPrice(effectivePrice, Locale.getDefault()));
                }
            }
        }

        protected void bindPrice(@NonNull GameDto game, boolean isOwned) {
            bindPrice(game, isOwned, false);
        }

        protected void bindPrice(@NonNull GameDto game) {
            bindPrice(game, false, false);
        }
    }

    public static class HorizontalGameViewHolder extends GameViewHolder {

        private final ImageView banner;
        final ImageButton buttonSave;

        public HorizontalGameViewHolder(@NonNull View itemView) {
            super(itemView);
            banner = itemView.findViewById(R.id.imageView_banner);
            // buttonSave was removed from layout
            buttonSave = null;
        }

        @Override
        public void bind(@NonNull GameDto game, @NonNull Context context) {
            bind(game, context, false, false);
        }

        @Override
        public void bind(@NonNull GameDto game, @NonNull Context context, boolean isOwned) {
            bind(game, context, isOwned, false);
        }

        @Override
        public void bind(@NonNull GameDto game, @NonNull Context context, boolean isOwned, boolean showDownload) {
            super.bind(game, context, isOwned, showDownload);
            loadBannerImage(game, context);
        }

        private void loadBannerImage(@NonNull GameDto game, @NonNull Context context) {
            if (banner == null) return;

            String imageUrl = game.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_videogame_asset_black_24dp)
                        .error(R.drawable.ic_videogame_asset_black_24dp)
                        .into(banner);
            } else {
                banner.setImageResource(R.drawable.ic_videogame_asset_black_24dp);
            }
        }
    }

    public static class VerticalGameViewHolder extends GameViewHolder {
        final Button buttonDownload;
        final ProgressBar progressBarDownload;

        public VerticalGameViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonDownload = itemView.findViewById(R.id.button_download);
            progressBarDownload = itemView.findViewById(R.id.progressBar_download);
        }

        @Override
        public void bind(@NonNull GameDto game, @NonNull Context context, boolean isOwned, boolean showDownload) {
            super.bind(game, context, isOwned, showDownload);
            
            // Handle download button visibility
            if (buttonDownload != null) {
                if (isOwned && showDownload) {
                    buttonDownload.setVisibility(View.VISIBLE);
                    buttonDownload.setText(R.string.download);
                    buttonDownload.setEnabled(true);
                } else {
                    buttonDownload.setVisibility(View.GONE);
                }
            }
            
            // Hide progress bar initially
            if (progressBarDownload != null) {
                progressBarDownload.setVisibility(View.GONE);
            }
        }
    }
}
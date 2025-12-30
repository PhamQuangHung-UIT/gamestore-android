package com.uit.gamestore.ui.store;

import static com.uit.gamestore.ui.store.CategorySection.DIRECTION_HORIZONTAL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    public interface OnGameClickListener {
        void onGameClick(GameDto game);
    }

    public interface OnSaveClickListener {
        void onSaveClick(GameDto game);
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

        holder.bind(game, context);
        
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

        // Make purchase button also trigger game click
        if (holder.buttonPurchase != null) {
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

            bindPrice(game);
        }

        protected void bindPrice(@NonNull GameDto game) {
            if (buttonPurchase == null) return;

            double effectivePrice = game.getEffectivePrice();
            if (effectivePrice <= 0) {
                buttonPurchase.setText(R.string.button_install);
            } else {
                buttonPurchase.setText(formatPrice(effectivePrice, Locale.getDefault()));
            }
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
            super.bind(game, context);

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
        public VerticalGameViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

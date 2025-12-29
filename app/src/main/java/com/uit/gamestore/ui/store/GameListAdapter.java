package com.uit.gamestore.ui.store;

import static com.uit.gamestore.ui.store.CategorySection.DIRECTION_HORIZONTAL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class GameListAdapter extends ListAdapter<GameDto, GameListAdapter.GameViewHolder> {

    private final int direction;
    private final Context context;
    private OnGameClickListener clickListener;

    public interface OnGameClickListener {
        void onGameClick(GameDto game);
    }

    private static final DiffUtil.ItemCallback<GameDto> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull GameDto oldItem, @NonNull GameDto newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GameDto oldItem, @NonNull GameDto newItem) {
            return oldItem.getId().equals(newItem.getId()) &&
                    oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getPrice() == newItem.getPrice();
        }
    };

    public GameListAdapter(int direction, @NonNull Context context) {
        super(DIFF_CALLBACK);
        this.direction = direction;
        this.context = context;
    }

    public void setOnGameClickListener(OnGameClickListener listener) {
        this.clickListener = listener;
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
        holder.bind(game, context);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onGameClick(game);
            }
        });
    }

    public static String formatPrice(double price, Locale locale) {
        if (price == 0) {
            return "Free";
        }
        BigDecimal amount = BigDecimal.valueOf(price);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setCurrency(Currency.getInstance("USD"));
        return formatter.format(amount);
    }

    public static abstract class GameViewHolder extends RecyclerView.ViewHolder {

        protected final TextView labelGameName;
        protected final ImageView imageViewGameIcon;
        protected final ImageView imageViewMinimumAge;
        protected final Button buttonPurchase;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            labelGameName = itemView.findViewById(R.id.textView_gameName);
            imageViewGameIcon = itemView.findViewById(R.id.imageView_gameIcon);
            imageViewMinimumAge = itemView.findViewById(R.id.imageView_ageRating);
            buttonPurchase = itemView.findViewById(R.id.button_installGame);
        }

        public void bind(GameDto game, Context context) {
            labelGameName.setText(game.getTitle());
            Glide.with(context)
                    .load(game.getCoverImage())
                    .placeholder(R.drawable.ic_videogame_asset_black_24dp)
                    .into(imageViewGameIcon);
            bindAgeRating(game.getAgeRating());
            bindPrice(game);
        }

        private void bindAgeRating(String ageRating) {
            int resourceId = getAgeRatingResource(ageRating);
            if (resourceId != 0) {
                imageViewMinimumAge.setImageResource(resourceId);
                imageViewMinimumAge.setVisibility(View.VISIBLE);
            } else {
                imageViewMinimumAge.setVisibility(View.GONE);
            }
        }

        private int getAgeRatingResource(String ageRating) {
            if (ageRating == null) return 0;
            return switch (ageRating) {
                case "3+", "iarc-3" -> R.drawable.iarc_3;
                case "7+", "iarc-7" -> R.drawable.iarc_7;
                case "12+", "iarc-12" -> R.drawable.iarc_12;
                case "16+", "iarc-16" -> R.drawable.iarc_16;
                case "18+", "iarc-18" -> R.drawable.iarc_18;
                default -> 0;
            };
        }

        private void bindPrice(GameDto game) {
            double effectivePrice = game.getEffectivePrice();
            if (effectivePrice == 0) {
                buttonPurchase.setText(R.string.button_install);
            } else {
                buttonPurchase.setText(formatPrice(effectivePrice, Locale.getDefault()));
            }
        }
    }

    public static class HorizontalGameViewHolder extends GameViewHolder {

        private final ImageView banner;

        public HorizontalGameViewHolder(@NonNull View itemView) {
            super(itemView);
            banner = itemView.findViewById(R.id.imageView_banner);
        }

        @Override
        public void bind(GameDto game, Context context) {
            super.bind(game, context);
            Glide.with(context)
                    .load(game.getCoverImage())
                    .placeholder(R.drawable.ic_videogame_asset_black_24dp)
                    .into(banner);
        }
    }

    public static class VerticalGameViewHolder extends GameViewHolder {
        public VerticalGameViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

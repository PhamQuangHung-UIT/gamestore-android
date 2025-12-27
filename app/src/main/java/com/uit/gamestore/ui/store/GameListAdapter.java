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
import com.uit.gamestore.domain.model.Game;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class GameListAdapter extends ListAdapter<Game, GameListAdapter.GameViewHolder> {
    int m_direction;

    private Context m_context;

    private static final DiffUtil.ItemCallback<Game> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Game oldItem, @NonNull Game newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Game oldItem, @NonNull Game newItem) {
            return oldItem.equals(newItem);
        }
    };
    public GameListAdapter(int direction, Context context) {
        super(DIFF_CALLBACK);
        m_direction = direction;
        m_context = context;
    }
    @NonNull
    @Override
    public GameListAdapter.GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (m_direction == DIRECTION_HORIZONTAL)
            return new HorizontalGameViewHolder(LayoutInflater.from(m_context).inflate(R.layout.item_game_big, parent, false));
        else return new VerticalGameViewHolder(LayoutInflater.from(m_context).inflate(R.layout.item_game, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GameListAdapter.GameViewHolder holder, int position) {
        var game = getItem(position);
        holder.getLabel_gameName().setText(game.getName());
        Glide.with(m_context).load(game.getIconUrl()).into(holder.getImageView_gameIcon());
        switch (game.getMinimumAgeRating()) {
            case "iarc-3":
                holder.getImageView_minimumAge().setImageResource(R.drawable.iarc_3);
                break;
            case "iarc-7":
                holder.getImageView_minimumAge().setImageResource(R.drawable.iarc_7);
                break;
            case "iarc-12":
                holder.getImageView_minimumAge().setImageResource(R.drawable.iarc_12);
                break;
            case "iarc-16":
                holder.getImageView_minimumAge().setImageResource(R.drawable.iarc_16);
                break;
            case "iarc-18":
                holder.getImageView_minimumAge().setImageResource(R.drawable.iarc_18);
                break;
            default:
                holder.getImageView_minimumAge().setVisibility(View.GONE);
                break;
        }
        if (holder instanceof HorizontalGameViewHolder) {
            var horizontalHolder = (HorizontalGameViewHolder) holder;
            Glide.with(m_context).load(game.getBannerUrl())
                    .into(horizontalHolder.getBanner());

        }

        if (game.getPrice().getValue() == 0) {
            holder.getButton_purchase().setText(R.string.button_install);
        } else {
            holder.getButton_purchase().setText(formatPrice(game.getPrice(), Locale.getDefault()));
        }
    }

    public static String formatPrice(Game.Price price, Locale locale) {
        BigDecimal amount = new BigDecimal(price.getValue());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setCurrency(Currency.getInstance(price.getCurrency()));

        return formatter.format(amount);
    }

    public static abstract class GameViewHolder extends RecyclerView.ViewHolder {
        private TextView label_gameName;
        private ImageView imageView_gameIcon;
        private ImageView imageView_minimumAge;
        private Button button_purchase;

        public GameViewHolder(@NonNull View itemView) {

            super(itemView);
            label_gameName = itemView.findViewById(R.id.textView_gameName);
            imageView_gameIcon = itemView.findViewById(R.id.imageView_gameIcon);
            imageView_minimumAge = itemView.findViewById(R.id.imageView_ageRating);
            button_purchase = itemView.findViewById(R.id.button_installGame);

        }

        public TextView getLabel_gameName() {
            return label_gameName;
        }

        public ImageView getImageView_gameIcon() {
            return imageView_gameIcon;
        }

        public ImageView getImageView_minimumAge() {
            return imageView_minimumAge;
        }

        public Button getButton_purchase() {
            return button_purchase;
        }
    }

    public static class HorizontalGameViewHolder extends GameViewHolder {
        private ImageView m_banner;

        private View container_gameinfo;

        public HorizontalGameViewHolder(@NonNull View itemView) {
            super(itemView);
            m_banner = itemView.findViewById(R.id.imageView_banner);
            container_gameinfo = itemView.findViewById(R.id.container_gameinfo);
        }

        public ImageView getBanner() {
            return m_banner;
        }

        public View getContainerGameinfo() {
            return container_gameinfo;
        }
    }

    public static class VerticalGameViewHolder extends GameViewHolder {
        public VerticalGameViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

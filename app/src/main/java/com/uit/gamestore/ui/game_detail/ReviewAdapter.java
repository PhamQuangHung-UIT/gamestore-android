package com.uit.gamestore.ui.game_detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.uit.gamestore.R;
import com.uit.gamestore.data.remote.dto.ReviewDto;

import java.util.Locale;
import java.util.Objects;

public class ReviewAdapter extends ListAdapter<ReviewDto, ReviewAdapter.ReviewViewHolder> {

    private static final DiffUtil.ItemCallback<ReviewDto> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReviewDto oldItem, @NonNull ReviewDto newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReviewDto oldItem, @NonNull ReviewDto newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId()) &&
                    oldItem.getRating() == newItem.getRating() &&
                    Objects.equals(oldItem.getComment(), newItem.getComment());
        }
    };

    public ReviewAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewDto review = getItem(position);
        if (review != null) {
            holder.bind(review);
        }
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUsername;
        private final TextView textViewRating;
        private final TextView textViewComment;
        private final TextView textViewDate;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }

        public void bind(@NonNull ReviewDto review) {
            // Username
            String username = "Anonymous";
            if (review.getCustomer() != null && review.getCustomer().getUsername() != null) {
                username = review.getCustomer().getUsername();
            }
            textViewUsername.setText(username);

            // Rating
            textViewRating.setText(String.format(Locale.getDefault(), "%d ★", review.getRating()));

            // Comment
            String comment = review.getComment();
            if (comment != null && !comment.isEmpty()) {
                textViewComment.setText(comment);
                textViewComment.setVisibility(View.VISIBLE);
            } else {
                textViewComment.setVisibility(View.GONE);
            }

            // Date
            String date = review.getCreatedAt();
            if (date != null && date.length() >= 10) {
                textViewDate.setText(date.substring(0, 10));
            } else {
                textViewDate.setVisibility(View.GONE);
            }
        }
    }
}

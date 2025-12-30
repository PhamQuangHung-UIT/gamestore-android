package com.uit.gamestore.ui.orders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.uit.gamestore.R;
import com.uit.gamestore.data.remote.dto.GameDto;
import com.uit.gamestore.data.remote.dto.OrderDto;
import com.uit.gamestore.data.repository.CustomerRepository;
import com.uit.gamestore.util.DownloadHelper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyStateLayout;
    private ProgressBar progressBar;

    private OrderAdapter adapter;
    private final CustomerRepository customerRepository = new CustomerRepository();
    private DownloadHelper downloadHelper;
    
    // Pending download info
    private GameDto pendingDownloadGame;
    private Button pendingDownloadButton;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (pendingDownloadGame != null && pendingDownloadButton != null) {
                        startDownload(pendingDownloadGame, pendingDownloadButton);
                    }
                } else {
                    Toast.makeText(this, "Storage permission required for download", Toast.LENGTH_SHORT).show();
                }
                pendingDownloadGame = null;
                pendingDownloadButton = null;
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        downloadHelper = new DownloadHelper(this);
        initViews();
        setupToolbar();
        setupRecyclerView();
        loadOrders();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadHelper != null) {
            downloadHelper.cleanup();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewOrders);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        progressBar = findViewById(R.id.progressBar);

        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
        swipeRefreshLayout.setColorSchemeResources(R.color.teal_200, R.color.teal_700);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        if (isFinishing() || isDestroyed()) return;
        
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing() && progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        customerRepository.getOrders(new CustomerRepository.OrdersCallback() {
            @Override
            public void onSuccess(@NonNull List<OrderDto> orders) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (orders.isEmpty()) {
                        if (emptyStateLayout != null) emptyStateLayout.setVisibility(View.VISIBLE);
                        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
                    } else {
                        if (emptyStateLayout != null) emptyStateLayout.setVisibility(View.GONE);
                        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
                        if (adapter != null) adapter.submitList(orders);
                    }
                });
            }

            @Override
            public void onError(@NonNull String message) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(OrderHistoryActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void onDownloadClick(GameDto game, Button downloadButton) {
        if (game == null || downloadButton == null) return;
        
        String gameId = game.getId();
        if (gameId == null || gameId.isEmpty()) {
            Toast.makeText(this, "Invalid game", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check storage permission for older Android versions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                pendingDownloadGame = game;
                pendingDownloadButton = downloadButton;
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
        
        startDownload(game, downloadButton);
    }

    private void startDownload(GameDto game, Button downloadButton) {
        if (downloadHelper == null || game == null || downloadButton == null) return;
        if (isFinishing() || isDestroyed()) return;
        
        String gameId = game.getId();
        if (gameId == null || gameId.isEmpty()) {
            Toast.makeText(this, "Invalid game ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String gameName = game.getName() != null ? game.getName() : "Game";
        
        downloadButton.setText(R.string.downloading);
        downloadButton.setEnabled(false);
        
        Toast.makeText(this, "Starting download: " + gameName, Toast.LENGTH_SHORT).show();
        
        downloadHelper.downloadApk(gameId, gameName, new DownloadHelper.DownloadCallback() {
            @Override
            public void onProgress(int progress) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> {
                    if (downloadButton != null) {
                        downloadButton.setText(getString(R.string.download_progress, progress));
                    }
                });
            }

            @Override
            public void onComplete(File file) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> {
                    if (downloadButton != null) {
                        downloadButton.setText(R.string.install);
                        downloadButton.setEnabled(true);
                        downloadButton.setOnClickListener(v -> {
                            if (downloadHelper != null && file != null && file.exists()) {
                                downloadHelper.installApk(file);
                            }
                        });
                    }
                    Toast.makeText(OrderHistoryActivity.this, R.string.download_complete, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> {
                    if (downloadButton != null) {
                        downloadButton.setText(R.string.download);
                        downloadButton.setEnabled(true);
                    }
                    Toast.makeText(OrderHistoryActivity.this, 
                            getString(R.string.download_failed) + ": " + (message != null ? message : "Unknown error"), 
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    // Inner Adapter class
    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

        private List<OrderDto> orders = new ArrayList<>();

        public void submitList(List<OrderDto> newOrders) {
            this.orders = newOrders != null ? newOrders : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            holder.bind(orders.get(position));
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewOrderId;
            private final TextView textViewOrderDate;
            private final TextView textViewStatus;
            private final TextView textViewPaymentMethod;
            private final TextView textViewTotal;
            private final LinearLayout layoutGames;

            OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
                textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);
                textViewPaymentMethod = itemView.findViewById(R.id.textViewPaymentMethod);
                textViewTotal = itemView.findViewById(R.id.textViewTotal);
                layoutGames = itemView.findViewById(R.id.layoutGames);
            }

            void bind(OrderDto order) {
                // Order ID
                String transactionId = order.getTransactionId();
                if (transactionId != null && transactionId.length() > 15) {
                    transactionId = transactionId.substring(0, 15) + "...";
                }
                textViewOrderId.setText(getString(R.string.order_id_format, transactionId != null ? transactionId : order.getId()));

                // Order Date
                String dateStr = formatDate(order.getOrderDate());
                textViewOrderDate.setText(dateStr);

                // Status
                String status = order.getPaymentStatus();
                textViewStatus.setText(status != null ? status : "Unknown");

                // Payment Method
                textViewPaymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");

                // Total
                textViewTotal.setText(String.format(Locale.US, "$%.2f", order.getTotalValue()));

                // Games
                layoutGames.removeAllViews();
                List<OrderDto.OrderDetailDto> details = order.getOrderDetails();
                if (details != null) {
                    for (OrderDto.OrderDetailDto detail : details) {
                        addGameItem(detail);
                    }
                }
            }


            private void addGameItem(OrderDto.OrderDetailDto detail) {
                View gameView = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.item_order_game, layoutGames, false);

                ImageView imageView = gameView.findViewById(R.id.imageViewGame);
                TextView textViewName = gameView.findViewById(R.id.textViewGameName);
                TextView textViewKey = gameView.findViewById(R.id.textViewGameKey);
                TextView textViewPrice = gameView.findViewById(R.id.textViewPrice);
                Button buttonDownload = gameView.findViewById(R.id.buttonDownload);

                GameDto game = detail.getGame();
                if (game != null) {
                    textViewName.setText(game.getName() != null ? game.getName() : "Unknown Game");

                    if (game.getImageUrl() != null) {
                        Glide.with(itemView.getContext())
                                .load(game.getImageUrl())
                                .placeholder(R.drawable.ic_videogame_asset_black_24dp)
                                .into(imageView);
                    }
                    
                    // Setup download button
                    buttonDownload.setOnClickListener(v -> onDownloadClick(game, buttonDownload));
                } else {
                    textViewName.setText("Unknown Game");
                    buttonDownload.setVisibility(View.GONE);
                }

                textViewPrice.setText(String.format(Locale.US, "$%.2f", detail.getValue()));

                // Show game key if available
                OrderDto.GameKeyDto gameKey = detail.getGameKey();
                if (gameKey != null && gameKey.getKeyCode() != null) {
                    textViewKey.setText(getString(R.string.game_key_format, gameKey.getKeyCode()));
                    textViewKey.setVisibility(View.VISIBLE);
                }

                layoutGames.addView(gameView);
            }

            private String formatDate(String dateString) {
                if (dateString == null) return "Unknown";
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    Date date = inputFormat.parse(dateString);
                    return date != null ? outputFormat.format(date) : dateString;
                } catch (ParseException e) {
                    return dateString.length() > 10 ? dateString.substring(0, 10) : dateString;
                }
            }
        }
    }
}

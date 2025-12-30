package com.uit.gamestore.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownloadHelper {

    // Mock APK URL for testing
    // Using F-Droid's sample APK which is reliable and small
    private static final String MOCK_APK_URL = "https://f-droid.org/repo/org.fdroid.fdroid_1019050.apk";
    
    private final Context context;
    private final DownloadManager downloadManager;
    private final Map<Long, DownloadCallback> callbacks = new HashMap<>();
    private final Map<Long, String> downloadGameNames = new HashMap<>();
    private BroadcastReceiver downloadReceiver;

    public interface DownloadCallback {
        void onProgress(int progress);
        void onComplete(File file);
        void onError(String message);
    }

    public DownloadHelper(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            registerReceiver();
        }
    }

    private void registerReceiver() {
        if (context == null) return;
        
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent == null) return;
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId != -1 && callbacks.containsKey(downloadId)) {
                    checkDownloadStatus(downloadId);
                }
            }
        };
        
        try {
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(downloadReceiver, filter, Context.RECEIVER_EXPORTED);
            } else {
                context.registerReceiver(downloadReceiver, filter);
            }
        } catch (Exception e) {
            android.util.Log.e("DownloadHelper", "Failed to register receiver: " + e.getMessage());
        }
    }


    public long downloadApk(@NonNull String gameId, @NonNull String gameName, @NonNull DownloadCallback callback) {
        if (downloadManager == null) {
            callback.onError("Download manager not available");
            return -1;
        }
        
        if (gameId.isEmpty()) {
            callback.onError("Invalid game ID");
            return -1;
        }

        String downloadUrl = getDownloadUrl(gameId);
        String fileName = sanitizeFileName(gameName) + "_" + System.currentTimeMillis() + ".apk";

        android.util.Log.d("DownloadHelper", "Starting download from: " + downloadUrl);
        android.util.Log.d("DownloadHelper", "Saving to: " + fileName);

        try {
            Uri uri = Uri.parse(downloadUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            
            request.setTitle(gameName);
            request.setDescription("Downloading " + gameName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setMimeType("application/vnd.android.package-archive");
            
            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

            long downloadId = downloadManager.enqueue(request);
            android.util.Log.d("DownloadHelper", "Download enqueued with ID: " + downloadId);
            
            callbacks.put(downloadId, callback);
            downloadGameNames.put(downloadId, gameName);
            
            trackProgress(downloadId, callback);
            
            return downloadId;
        } catch (IllegalArgumentException e) {
            android.util.Log.e("DownloadHelper", "Invalid URL: " + e.getMessage());
            callback.onError("Invalid download URL");
            return -1;
        } catch (SecurityException e) {
            android.util.Log.e("DownloadHelper", "Security error: " + e.getMessage());
            callback.onError("Storage permission denied");
            return -1;
        } catch (Exception e) {
            android.util.Log.e("DownloadHelper", "Download error: " + e.getMessage());
            callback.onError("Failed to start download: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            return -1;
        }
    }

    private String getDownloadUrl(String gameId) {
        // Mock implementation - in production, call backend API to get signed download URL
        // The backend should verify the user owns the game before providing the URL
        return MOCK_APK_URL;
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private void trackProgress(long downloadId, DownloadCallback callback) {
        if (downloadManager == null || callback == null) return;
        
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (!callbacks.containsKey(downloadId) || downloadManager == null) return;
                
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                
                Cursor cursor = null;
                try {
                    cursor = downloadManager.query(query);
                    if (cursor != null && cursor.moveToFirst()) {
                        int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int bytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                        int totalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                        
                        if (statusIndex >= 0) {
                            int status = cursor.getInt(statusIndex);
                            
                            if (status == DownloadManager.STATUS_RUNNING && bytesIndex >= 0 && totalIndex >= 0) {
                                long bytesDownloaded = cursor.getLong(bytesIndex);
                                long totalBytes = cursor.getLong(totalIndex);
                                
                                if (totalBytes > 0) {
                                    int progress = (int) ((bytesDownloaded * 100) / totalBytes);
                                    callback.onProgress(Math.min(progress, 100));
                                }
                                
                                handler.postDelayed(this, 500);
                            } else if (status == DownloadManager.STATUS_PENDING) {
                                callback.onProgress(0);
                                handler.postDelayed(this, 500);
                            }
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("DownloadHelper", "Error tracking progress: " + e.getMessage());
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        };
        
        handler.post(progressRunnable);
    }


    private void checkDownloadStatus(long downloadId) {
        if (downloadManager == null) return;
        
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                
                if (statusIndex >= 0) {
                    int status = cursor.getInt(statusIndex);
                    DownloadCallback callback = callbacks.get(downloadId);
                    
                    if (callback != null) {
                        switch (status) {
                            case DownloadManager.STATUS_SUCCESSFUL:
                                if (localUriIndex >= 0) {
                                    String localUri = cursor.getString(localUriIndex);
                                    if (localUri != null && !localUri.isEmpty()) {
                                        try {
                                            String path = Uri.parse(localUri).getPath();
                                            if (path != null) {
                                                File file = new File(path);
                                                if (file.exists()) {
                                                    callback.onProgress(100);
                                                    callback.onComplete(file);
                                                } else {
                                                    callback.onError("Downloaded file not found");
                                                }
                                            } else {
                                                callback.onError("Invalid file path");
                                            }
                                        } catch (Exception e) {
                                            callback.onError("Error accessing downloaded file");
                                        }
                                    } else {
                                        callback.onError("Download location unknown");
                                    }
                                }
                                break;
                                
                            case DownloadManager.STATUS_FAILED:
                                String reason = "Download failed";
                                if (reasonIndex >= 0) {
                                    int errorCode = cursor.getInt(reasonIndex);
                                    reason = getErrorMessage(errorCode);
                                }
                                callback.onError(reason);
                                break;
                        }
                    }
                    
                    callbacks.remove(downloadId);
                    downloadGameNames.remove(downloadId);
                }
            }
        } catch (Exception e) {
            DownloadCallback callback = callbacks.get(downloadId);
            if (callback != null) {
                callback.onError("Error checking download status");
            }
            callbacks.remove(downloadId);
            downloadGameNames.remove(downloadId);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getErrorMessage(int errorCode) {
        android.util.Log.e("DownloadHelper", "Download error code: " + errorCode);
        switch (errorCode) {
            case DownloadManager.ERROR_CANNOT_RESUME:
                return "Cannot resume download";
            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                return "Storage not found";
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                return "File already exists";
            case DownloadManager.ERROR_FILE_ERROR:
                return "File error";
            case DownloadManager.ERROR_HTTP_DATA_ERROR:
                return "Network error - please check your connection";
            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                return "Insufficient storage space";
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                return "Too many redirects";
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                return "Server error - please try again later";
            case DownloadManager.ERROR_UNKNOWN:
            default:
                return "Download failed (code: " + errorCode + ")";
        }
    }

    public void installApk(@NonNull File file) {
        if (context == null) return;
        
        if (!file.exists()) {
            Toast.makeText(context, "APK file not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                apkUri = FileProvider.getUriForFile(context, 
                        context.getPackageName() + ".fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                apkUri = Uri.fromFile(file);
            }
            
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, "Cannot access APK file", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(context, "No app found to install APK", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Cannot install APK: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelDownload(long downloadId) {
        if (downloadManager != null && downloadId >= 0) {
            try {
                downloadManager.remove(downloadId);
            } catch (Exception e) {
                android.util.Log.e("DownloadHelper", "Error canceling download: " + e.getMessage());
            }
        }
        callbacks.remove(downloadId);
        downloadGameNames.remove(downloadId);
    }

    public void cleanup() {
        if (downloadReceiver != null && context != null) {
            try {
                context.unregisterReceiver(downloadReceiver);
            } catch (IllegalArgumentException e) {
                // Receiver was not registered
            } catch (Exception e) {
                android.util.Log.e("DownloadHelper", "Error unregistering receiver: " + e.getMessage());
            }
            downloadReceiver = null;
        }
        callbacks.clear();
        downloadGameNames.clear();
    }
}

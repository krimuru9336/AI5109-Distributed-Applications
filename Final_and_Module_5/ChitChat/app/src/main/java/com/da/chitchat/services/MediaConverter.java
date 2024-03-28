// Sven Schickentanz - fdai7287
package com.da.chitchat.services;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.da.chitchat.interfaces.MessageListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * The `MediaConverter` class is responsible for converting media files, such as images and videos,
 * to Base64 format and saving them to storage. It also provides methods for displaying
 * images and videos using Glide and ExoPlayer.
 */
public class MediaConverter {
    MessageListener messageListener;
    // Map to store chunks of media data, that will later be merged to form the complete media file
    private final Map<UUID, TreeMap<Long, String>> chunkMap;
    // Map to store media data that got an end emit from the server, but still waiting for the rest of the chunks
    private final HashMap<UUID, MediaMessageData> finishedMap;

    /**
     * Represents the data of a media message.
     */
    public static class MediaMessageData {
        private final String target;
        // Number of chunks that the media file is divided into
        // This is used to determine if all chunks have been received
        private final int chunkCounts;
        private final String mimeType;
        private final boolean isGroup;

        /**
         * Constructs a new MediaMessageData object.
         *
         * @param target      the target user or group
         * @param chunkCounts the number of chunks
         * @param mimeType    the MIME type of the media file
         * @param isGroup     indicates whether the target is a group
         */
        public MediaMessageData(String target, int chunkCounts, String mimeType, boolean isGroup) {
            this.target = target;
            this.chunkCounts = chunkCounts;
            this.mimeType = mimeType;
            this.isGroup = isGroup;
        }

        /**
         * Returns the target user or group.
         */
        public String getTarget() {
            return target;
        }

        /**
         * Returns the number of chunks.
         */
        public int getChunkCounts() {
            return chunkCounts;
        }

        /**
         * Returns the MIME type of the media file.
         */
        public String getMimeType() {
            return mimeType;
        }

        /**
         * Returns whether the target is a group.
         */
        public boolean isGroup() {
            return isGroup;
        }
    }

    /**
     * Constructs a new MediaConverter object.
     */
    public MediaConverter() {
        chunkMap = new HashMap<>();
        finishedMap = new HashMap<>();
    }

    /**
     * Sets the message listener.
     *
     * @param listener the message listener
     */
    public void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    /**
     * Converts an image to Base64 format.
     *
     * @param ctx       the context
     * @param imageUri  the URI of the image
     * @return the Base64-encoded image
     */
    public String convertBitmapToBase64(Context ctx, Uri imageUri) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            Bitmap bitmap;
            // Create a byte array output stream to store the image data
            byteArrayOutputStream = new ByteArrayOutputStream();

            // Check if the image is a GIF
            if (isGif(ctx, imageUri)) {
                // Get input stream for the GIF file
                try (InputStream inputStream = ctx.getContentResolver().openInputStream(imageUri)) {
                    if (inputStream != null) {
                        // Read the GIF file as a byte array
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        // Read the GIF file in chunks
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                    } else {
                        return "";
                    }
                }
                // Convert the byte array to Base64
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } else {
                // For other image formats (e.g., JPEG), compress the bitmap
                bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null)
                    byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Converts a video to Base64 format.
     *
     * @param ctx      the context
     * @param videoUri the URI of the video
     * @return the Base64-encoded video
     */
    public String convertVideoToBase64(Context ctx, Uri videoUri) {
        // Read the video file as a byte array
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();

            // Get input stream for the video file
            try (InputStream inputStream = ctx.getContentResolver().openInputStream(videoUri)) {
                if (inputStream != null) {
                    // Read the video file as a byte array
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    // Read the video file in chunks
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                } else {
                    return "";
                }
            }
            // Convert the byte array to Base64
            byte[] videoBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(videoBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null)
                    byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Displays an image using Glide.
     * 
     * @param ctx              the context
     * @param selectedImageUri the URI of the selected image
     */
    public void showImage(Context ctx, Uri selectedImageUri, ImageView imageView) {
        // Check if the selected image is a GIF
        boolean isGif = isGif(ctx, selectedImageUri);

        if (isGif) {
            // Display the selected GIF using Glide
            Glide.with(ctx)
                    .asGif()
                    .load(selectedImageUri)
                    .into(imageView);
        } else {
            // Display the selected image using Glide
            Glide.with(ctx)
                    .asBitmap()
                    .load(selectedImageUri)
                    .into(imageView);
        }
    }

    /**
     * Displays a video using ExoPlayer.
     * 
     * @param selectedVideoUri the URI of the selected video
     * @param exoPlayer        the ExoPlayer instance
     */
    public void showVideo(Uri selectedVideoUri, ExoPlayer exoPlayer) {
        MediaItem mediaItem = MediaItem.fromUri(selectedVideoUri);
        // Set the media item and prepare the ExoPlayer
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    /**
     * Checks if the selected image is a GIF using its mimeType.
     * 
     * @param ctx              the context
     * @param selectedImageUri the URI of the selected image
     * @return true if the selected image is a GIF, false otherwise
     */
    private boolean isGif(Context ctx, Uri selectedImageUri) {
        String mimeType = ctx.getContentResolver().getType(selectedImageUri);
        return mimeType != null && mimeType.endsWith("gif");
    }

    /**
     * Adds a chunk of media data to the chunk map.
     * 
     * @param ctx              the context
     * @param imageBase64Chunk the Base64-encoded media chunk
     * @param id               the ID of the media message
     */
    public void addChunk(Context ctx, String imageBase64Chunk, UUID id, long offset) {
        // Add the chunk to the chunk map, creating a new map if the ID is not present
        TreeMap<Long, String> idMap = chunkMap.computeIfAbsent(id, k -> new TreeMap<>());
        idMap.put(offset, imageBase64Chunk);
        // Check if received the complete media file event early, if so save the media
        if (finishedMap.containsKey(id)) {
            MediaMessageData mmd = finishedMap.get(id);
            if (mmd != null) {
                saveMedia(ctx, mmd.getTarget(), id, mmd.getChunkCounts(), mmd.getMimeType(), mmd.isGroup());
            }
        }
    }

    /**
     * Saves the media file to storage.
     * 
     * @param ctx       the context
     * @param target    the target user or group
     * @param id        the ID of the media message
     * @param chunkCount the number of chunks
     * @param mimeType  the MIME type of the media file
     * @param isGroup   indicates whether the target is a group
     */
    public void saveMedia(Context ctx, String target, UUID id, int chunkCount, String mimeType,
                          boolean isGroup) {
        if (checkIfFinished(target, id, chunkCount, mimeType, isGroup)) {
            finishedMap.remove(id);
            String base64Media = mergeMediaChunks(id);
            saveImageFromBase64(ctx, target, id, base64Media, mimeType, isGroup);
        }
    }

    /**
     * Merges the chunks of media data to form the complete media file.
     * 
     * @param id the ID of the media message
     */
    private String mergeMediaChunks(UUID id) {
        TreeMap<Long, String> idMap = chunkMap.get(id);
        if (idMap != null) {
            StringBuilder mergedChunks = new StringBuilder();

            for (String chunk : idMap.values()) {
                mergedChunks.append(chunk);
            }

            return mergedChunks.toString();
        } else {
            return "";
        }
    }

    /**
     * Checks if the media file transmission is finished.
     * 
     * @param target      the target user or group
     * @param id          the ID of the media message
     * @param chunkCounts the number of chunks
     * @param mimeType    the MIME type of the media file
     * @param isGroup     indicates whether the target is a group
     * @return true if the transmission is finished, false otherwise
     */
    private boolean checkIfFinished(String target, UUID id, int chunkCounts, String mimeType,
                                    boolean isGroup) {
        if (chunkMap.containsKey(id)) {
            TreeMap<Long, String> idMap = chunkMap.get(id);
            if (idMap != null) {
                // Transmission not finished, add to queue
                if (idMap.size() != chunkCounts) {
                    finishedMap.put(id, new MediaMessageData(target, chunkCounts, mimeType, isGroup));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Saves an image to storage from Base64 format.
     * 
     * @param ctx              the context
     * @param target           the target user or group
     * @param id               the ID of the media message
     * @param base64ImageData  the Base64-encoded image data
     * @param mimeType         the MIME type of the image
     * @param isGroup          indicates whether the target is a group
     */
    public void saveImageFromBase64(Context ctx, String target, UUID id, String base64ImageData,
                                    String mimeType, boolean isGroup) {
        // Decode base64 image data to binary
        byte[] imageData = Base64.decode(base64ImageData, Base64.DEFAULT);

        // Determine file extension based on MIME type
        String fileExtension = getFileExtensionFromMimeType(mimeType);
        if (fileExtension == null) {
            // Unsupported MIME type
            return;
        }

        // Save image to external storage
        File directory = ctx.getExternalFilesDir("Media");
        File file = new File(directory, "image_" + System.currentTimeMillis() + fileExtension);

        // Create the file if it does not exist
        Uri fileUri = FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".provider", file);
        // Grant read and write permissions to the file
        ctx.grantUriPermission(ctx.getApplicationContext().getPackageName(), fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // Write the image data to the file
        try (OutputStream outputStream = ctx.getContentResolver().openOutputStream(fileUri)) {
            if (outputStream == null) return;
            outputStream.write(imageData);

            // Add image to MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.SIZE, imageData.length);
            // Set IS_PENDING to 1 to prevent other apps from accessing the file
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri contentUri;
            // Determine content URI based on MIME type
            if (mimeType.startsWith("image")) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if (mimeType.startsWith("video")) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else {
                // Unsupported MIME type
                return;
            }

            // Insert the image into MediaStore and get the image URI
            Uri imageUri = ctx.getContentResolver().insert(contentUri, values);

            if (imageUri != null) {
                try {
                    // Write the image data to the image URI
                    OutputStream os = ctx.getContentResolver().openOutputStream(imageUri);
                    if (os != null) {
                        os.write(imageData);
                        os.close();
                    }
                    values.clear();
                    // Set IS_PENDING to 0 to allow other apps to access the file
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    ctx.getContentResolver().update(imageUri, values, null, null);
                } catch (IOException e) {
                    ctx.getContentResolver().delete(imageUri, null, null);
                }
            }

            // Notify the message listener that the media has been received
            if (messageListener != null) {
                messageListener.onMediaReceived(target, id, imageUri, isGroup, mimeType.startsWith("video"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the file extension based on the MIME type.
     * 
     * @param mimeType the MIME type
     * @return the file extension
     */
    private String getFileExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "video/mp4":
                return ".mp4";
            case "video/mpeg":
                return ".mpeg";
            case "video/webm":
                return ".webm";
            default:
                return null; // Unsupported MIME type
        }
    }
}

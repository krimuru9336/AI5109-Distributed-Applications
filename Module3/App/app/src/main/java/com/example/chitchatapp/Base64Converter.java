package com.example.chitchatapp;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class Base64Converter {

    public static String fileToBase64(Context context, Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            assert inputStream != null;
            byte[] bytes = readBytes(inputStream);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    // Convert Base64 string to file
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static File base64ToFile(Context context, String base64String, String filename) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        File file = new File(context.getCacheDir(), filename);
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            mimeType = context.getContentResolver().getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private static byte[] readBytes(InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteBuffer.toByteArray();
    }
}
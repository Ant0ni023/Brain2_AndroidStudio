package com.dev.brain2;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static File createFolder(Context context, String folderName) {
        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static File saveImageToFolder(Context context, Uri imageUri, String folderName) throws IOException {
        File folder = createFolder(context, folderName);
        File imageFile = new File(folder, "image_" + System.currentTimeMillis() + ".jpg");

        try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return imageFile;
    }
}

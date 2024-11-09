package com.dev.brain2.managers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dev.brain2.models.Folder;
import com.dev.brain2.models.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageManager {

    private Context context;
    private FolderManager folderManager;


    public ImageManager(Context context, FolderManager folderManager) {
        this.context = context;
        this.folderManager = folderManager;
    }


    public void saveImage(Uri imageUri, String imageName, Folder folder) throws IOException {
        File folderDir = folderManager.getFolder(folder.getName());
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }

        String fileName = imageName + ".jpg";
        File imageFile = new File(folderDir, fileName);

        Log.d("ImageManager", "Guardando imagen en: " + imageFile.getAbsolutePath());

        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        FileOutputStream outputStream = new FileOutputStream(imageFile);

        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();

        if (imageFile.exists()) {
            Log.d("ImageManager", "Imagen guardada exitosamente.");
        } else {
            Log.e("ImageManager", "Error al guardar la imagen.");
        }

        // Actualizar la información de la imagen en el objeto Folder
        Image image = new Image(Uri.fromFile(imageFile), imageName);
        folder.addImage(image);
        folderManager.updateFolder(folder);
    }


    public boolean moveImage(Image image, Folder sourceFolder, Folder targetFolder) {
        File sourceFile = new File(image.getUri().getPath());
        File targetDir = folderManager.getFolder(targetFolder.getName());
        File targetFile = new File(targetDir, sourceFile.getName());

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        if (sourceFile.renameTo(targetFile)) {
            sourceFolder.removeImage(image);
            image.setUri(Uri.fromFile(targetFile));
            targetFolder.addImage(image);
            folderManager.updateFolder(sourceFolder);
            folderManager.updateFolder(targetFolder);
            return true;
        } else {
            return false;
        }
    }


    public boolean deleteImage(Image image, Folder folder) {
        File imageFile = new File(image.getUri().getPath());
        if (imageFile.exists() && imageFile.delete()) {
            folder.removeImage(image);
            folderManager.updateFolder(folder);
            return true;
        } else {
            return false;
        }
    }


    public boolean renameImage(Image image, String newName, Folder folder) {
        File imageFile = new File(image.getUri().getPath());
        File newImageFile = new File(imageFile.getParent(), newName + ".jpg");

        if (imageFile.renameTo(newImageFile)) {
            image.setName(newName);
            image.setUri(Uri.fromFile(newImageFile));
            folderManager.updateFolder(folder);
            return true;
        } else {
            return false;
        }
    }
}

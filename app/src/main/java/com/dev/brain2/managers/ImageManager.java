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
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase se encarga de gestionar las imágenes de la aplicación.
 */
public class ImageManager {

    private final Context appContext;
    private final FolderManager folderManager;

    /**
     * Constructor.
     *
     * @param context       Contexto de la aplicación.
     * @param folderManager Manager de carpetas.
     */
    public ImageManager(Context context, FolderManager folderManager) {
        this.appContext = context;
        this.folderManager = folderManager;
    }

    /**
     * Guarda una nueva imagen en una carpeta.
     *
     * @param imageUri  URI de la imagen.
     * @param imageName Nombre de la imagen.
     * @param folder    Carpeta donde se guardará.
     * @throws IOException Si ocurre un error al guardar la imagen.
     */
    public void saveImage(Uri imageUri, String imageName, Folder folder) throws IOException {
        File folderDir = folderManager.createFolderOnDisk(folder.getName());
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }

        File imageFile = createImageFile(folderDir, imageName);
        copyImageToFile(imageUri, imageFile);

        if (imageFile.exists()) {
            Image image = new Image(Uri.fromFile(imageFile), imageName);
            folder.addImage(image);
            folderManager.updateFolder(folder);
        } else {
            Log.e("ImageManager", "Error al guardar la imagen.");
        }
    }

    /**
     * Crea un archivo para la imagen.
     *
     * @param folderDir Directorio de la carpeta.
     * @param imageName Nombre de la imagen.
     * @return Archivo creado.
     */
    private File createImageFile(File folderDir, String imageName) {
        String fileName = imageName + ".jpg";
        return new File(folderDir, fileName);
    }

    /**
     * Copia la imagen desde el URI al archivo destino.
     *
     * @param imageUri  URI de la imagen.
     * @param imageFile Archivo destino.
     * @throws IOException Si ocurre un error al copiar.
     */
    private void copyImageToFile(Uri imageUri, File imageFile) throws IOException {
        try (InputStream inputStream = appContext.getContentResolver().openInputStream(imageUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    /**
     * Mueve una imagen de una carpeta a otra.
     *
     * @param image        Imagen a mover.
     * @param sourceFolder Carpeta origen.
     * @param targetFolder Carpeta destino.
     * @return Verdadero si se movió exitosamente, falso de lo contrario.
     */
    public boolean moveImage(Image image, Folder sourceFolder, Folder targetFolder) {
        File sourceFile = new File(image.getUri().getPath());
        File targetDir = folderManager.createFolderOnDisk(targetFolder.getName());
        File targetFile = new File(targetDir, sourceFile.getName());

        if (moveFile(sourceFile, targetFile)) {
            updateFoldersAfterMove(image, sourceFolder, targetFolder, targetFile);
            return true;
        }
        return false;
    }

    /**
     * Mueve el archivo de imagen al directorio destino.
     *
     * @param sourceFile Archivo origen.
     * @param targetFile Archivo destino.
     * @return Verdadero si se movió, falso de lo contrario.
     */
    private boolean moveFile(File sourceFile, File targetFile) {
        return sourceFile.renameTo(targetFile);
    }

    /**
     * Actualiza las carpetas después de mover una imagen.
     *
     * @param image       Imagen movida.
     * @param sourceFolder Carpeta origen.
     * @param targetFolder Carpeta destino.
     * @param targetFile  Archivo en la nueva ubicación.
     */
    private void updateFoldersAfterMove(Image image, Folder sourceFolder, Folder targetFolder, File targetFile) {
        sourceFolder.removeImage(image);
        image.setUri(Uri.fromFile(targetFile));
        targetFolder.addImage(image);
        folderManager.updateFolder(sourceFolder);
        folderManager.updateFolder(targetFolder);
    }

    /**
     * Elimina una imagen de una carpeta.
     *
     * @param image  Imagen a eliminar.
     * @param folder Carpeta donde se encuentra la imagen.
     * @return Verdadero si se eliminó exitosamente, falso de lo contrario.
     */
    public boolean deleteImage(Image image, Folder folder) {
        File imageFile = new File(image.getUri().getPath());

        if (imageFile.exists() && imageFile.delete()) {
            folder.removeImage(image);
            folderManager.updateFolder(folder);
            return true;
        }
        return false;
    }

    /**
     * Renombra una imagen.
     *
     * @param image   Imagen a renombrar.
     * @param newName Nuevo nombre.
     * @param folder  Carpeta donde se encuentra la imagen.
     * @return Verdadero si se renombró exitosamente, falso de lo contrario.
     */
    public boolean renameImage(Image image, String newName, Folder folder) {
        File imageFile = new File(image.getUri().getPath());
        File newImageFile = new File(imageFile.getParent(), newName + ".jpg");

        if (imageFile.renameTo(newImageFile)) {
            updateImageAfterRename(image, newName, newImageFile, folder);
            return true;
        }
        return false;
    }

    /**
     * Actualiza la información de la imagen después de renombrarla.
     *
     * @param image       Imagen a actualizar.
     * @param newName     Nuevo nombre.
     * @param newImageFile Nuevo archivo.
     * @param folder      Carpeta donde se encuentra la imagen.
     */
    private void updateImageAfterRename(Image image, String newName, File newImageFile, Folder folder) {
        image.setName(newName);
        image.setUri(Uri.fromFile(newImageFile));
        folderManager.updateFolder(folder);
    }

    /**
     * Obtiene todas las imágenes de todas las carpetas.
     *
     * @return Lista de todas las imágenes.
     */
    public List<Image> getAllImages() {
        List<Image> allImages = new ArrayList<>();
        List<Folder> folders = folderManager.getAllFolders();
        for (Folder folder : folders) {
            allImages.addAll(folder.getImages());
        }
        return allImages;
    }
}

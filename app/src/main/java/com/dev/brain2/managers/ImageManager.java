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
    // Variables necesarias para gestionar las imágenes
    private Context context;
    private FolderManager folderManager;

    /**
     * Constructor.
     *
     * @param context       Contexto de la aplicación.
     * @param folderManager Manager de carpetas.
     */
    public ImageManager(Context context, FolderManager folderManager) {
        this.context = context;
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
        // Creamos la carpeta si no existe
        File folderDir = folderManager.createFolderOnDisk(folder.getName());
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }

        // Creamos el archivo de la imagen
        String fileName = imageName + ".jpg";
        File imageFile = new File(folderDir, fileName);

        // Copiamos la imagen al archivo
        try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        // Verificamos si la imagen se guardó correctamente
        if (imageFile.exists()) {
            Log.d("ImageManager", "Imagen guardada exitosamente en: " + imageFile.getAbsolutePath());

            // Creamos un objeto Image y lo añadimos a la carpeta
            Image image = new Image(Uri.fromFile(imageFile), imageName);
            folder.addImage(image);
            folderManager.updateFolder(folder);
        } else {
            Log.e("ImageManager", "Error al guardar la imagen.");
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
        // Obtenemos los archivos de origen y destino
        File sourceFile = new File(image.getUri().getPath());
        File targetDir = folderManager.createFolderOnDisk(targetFolder.getName());
        File targetFile = new File(targetDir, sourceFile.getName());

        // Intentamos mover el archivo
        if (sourceFile.renameTo(targetFile)) {
            // Si se movió correctamente, actualizamos las carpetas
            sourceFolder.removeImage(image);
            image.setUri(Uri.fromFile(targetFile));
            targetFolder.addImage(image);

            // Actualizamos ambas carpetas
            folderManager.updateFolder(sourceFolder);
            folderManager.updateFolder(targetFolder);

            // La carpeta origen se eliminará automáticamente si quedó vacía
            return true;
        }
        return false;
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

        // Intentamos eliminar el archivo
        if (imageFile.exists() && imageFile.delete()) {
            // Si se eliminó correctamente, actualizamos la carpeta
            folder.removeImage(image);
            folderManager.updateFolder(folder);

            // La carpeta se eliminará automáticamente si quedó vacía
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
        // Obtenemos los archivos viejo y nuevo
        File imageFile = new File(image.getUri().getPath());
        File newImageFile = new File(imageFile.getParent(), newName + ".jpg");

        // Intentamos renombrar el archivo
        if (imageFile.renameTo(newImageFile)) {
            // Si se renombró correctamente, actualizamos la imagen
            image.setName(newName);
            image.setUri(Uri.fromFile(newImageFile));
            folderManager.updateFolder(folder);
            return true;
        }
        return false;
    }

    /**
     * Obtiene todas las imágenes de todas las carpetas.
     *
     * @return Lista de todas las imágenes.
     */
    public List<Image> getAllImages() {
        List<Image> allImages = new ArrayList<>();

        // Supón que folderManager tiene un método para obtener todas las carpetas
        List<Folder> folders = folderManager.getAllFolders();
        for (Folder folder : folders) {
            allImages.addAll(folder.getImages());
        }

        return allImages;
    }
}

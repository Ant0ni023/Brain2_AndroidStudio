package com.dev.brain2;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Clase responsable de la gestión de imágenes en la aplicación.
 * Centraliza todas las operaciones relacionadas con imágenes, incluyendo
 * guardado, carga, movimiento y eliminación de imágenes.
 *
 * Responsabilidad única: Gestionar el ciclo de vida y operaciones de las imágenes.
 */
public class ImageManager {

    private final Context context;
    private final FolderManager folderManager;
    private final FileUtils fileUtils;

    /**
     * Constructor del ImageManager.
     *
     * @param context Contexto de la aplicación
     * @param folderManager Gestor de carpetas
     */
    public ImageManager(Context context, FolderManager folderManager) {
        if (context == null || folderManager == null) {
            throw new IllegalArgumentException("Contexto y FolderManager no pueden ser null");
        }
        this.context = context;
        this.folderManager = folderManager;
        this.fileUtils = new FileUtils();
    }

    /**
     * Guarda una imagen desde un Uri en una carpeta específica.
     *
     * @param imageUri Uri de la imagen a guardar
     * @param imageName Nombre para la imagen
     * @param folder Carpeta donde guardar la imagen
     * @return Image objeto creado si la operación fue exitosa
     * @throws IOException si hay un error al guardar la imagen
     */
    public Image saveImage(Uri imageUri, String imageName, Folder folder) throws IOException {
        validateImageParameters(imageUri, imageName, folder);

        File savedFile = FileUtils.saveImageToFolder(context, imageUri, folder.getName());
        Image newImage = new Image(savedFile.getAbsolutePath(), imageName);

        folder.addImage(newImage);
        folderManager.updateFolder(folder);

        return newImage;
    }

    /**
     * Guarda una imagen desde un Bitmap en una carpeta específica.
     *
     * @param bitmap Bitmap de la imagen
     * @param imageName Nombre para la imagen
     * @param folder Carpeta donde guardar la imagen
     * @return Image objeto creado si la operación fue exitosa
     */
    public Image saveBitmapImage(Bitmap bitmap, String imageName, Folder folder) {
        validateBitmapParameters(bitmap, imageName, folder);

        Uri savedUri = FileUtils.saveBitmapToFolder(context, bitmap, folder.getName());
        if (savedUri == null) {
            throw new RuntimeException("Error al guardar el bitmap");
        }

        Image newImage = new Image(savedUri.toString(), imageName);
        folder.addImage(newImage);
        folderManager.updateFolder(folder);

        return newImage;
    }

    /**
     * Guarda temporalmente un bitmap en el directorio cache.
     *
     * @param bitmap Bitmap a guardar temporalmente
     * @return Uri del archivo temporal creado, o null si hubo un error
     */
    public Uri saveBitmapAsTemp(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap no puede ser null");
        }

        try {
            File cacheDir = context.getCacheDir();
            File imageFile = new File(cacheDir, "temp_image_" + System.currentTimeMillis() + ".jpg");

            Uri savedUri = FileUtils.saveBitmapToFolder(context, bitmap, cacheDir.getName());
            if (savedUri == null) {
                throw new RuntimeException("Error al guardar el bitmap temporal");
            }

            return savedUri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mueve una imagen de una carpeta a otra.
     *
     * @param image Imagen a mover
     * @param sourceFolder Carpeta origen
     * @param targetFolder Carpeta destino
     * @return boolean indicando si la operación fue exitosa
     */
    public boolean moveImage(Image image, Folder sourceFolder, Folder targetFolder) {
        validateMoveParameters(image, sourceFolder, targetFolder);

        try {
            // Crear una copia de la imagen en la carpeta destino
            File sourceFile = new File(image.getUri());
            Uri sourceUri = Uri.fromFile(sourceFile);
            File newFile = FileUtils.saveImageToFolder(context, sourceUri, targetFolder.getName());

            // Crear nueva imagen en carpeta destino
            Image movedImage = new Image(newFile.getAbsolutePath(), image.getName());
            targetFolder.addImage(movedImage);

            // Eliminar imagen de carpeta origen
            sourceFolder.removeImage(image);
            sourceFile.delete();

            // Actualizar ambas carpetas
            folderManager.updateFolder(sourceFolder);
            folderManager.updateFolder(targetFolder);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una imagen de una carpeta.
     *
     * @param image Imagen a eliminar
     * @param folder Carpeta que contiene la imagen
     * @return boolean indicando si la operación fue exitosa
     */
    public boolean deleteImage(Image image, Folder folder) {
        validateDeleteParameters(image, folder);

        // Eliminar archivo físico
        File imageFile = new File(image.getUri());
        boolean fileDeleted = imageFile.delete();

        if (fileDeleted) {
            // Eliminar referencia de la imagen en la carpeta
            folder.removeImage(image);
            folderManager.updateFolder(folder);
            return true;
        }
        return false;
    }

    /**
     * Renombra una imagen.
     *
     * @param image Imagen a renombrar
     * @param newName Nuevo nombre
     * @param folder Carpeta que contiene la imagen
     */
    public void renameImage(Image image, String newName, Folder folder) {
        validateRenameParameters(image, newName, folder);

        image.setName(newName);
        folderManager.updateFolder(folder);
    }

    /**
     * Obtiene todas las imágenes de una carpeta.
     *
     * @param folderName Nombre de la carpeta
     * @return Lista de imágenes en la carpeta
     */
    public List<Image> getImagesFromFolder(String folderName) {
        Optional<Folder> folder = folderManager.getFolderByName(folderName);
        return folder.map(Folder::getImages).orElseThrow(() ->
                new IllegalArgumentException("Carpeta no encontrada: " + folderName));
    }

    /**
     * Obtiene la lista de carpetas disponibles, excluyendo una específica.
     *
     * @param currentFolder Carpeta a excluir de la lista
     * @return Lista de carpetas disponibles
     */
    public List<Folder> getAvailableFolders(Folder currentFolder) {
        return folderManager.getAvailableFolders(currentFolder);
    }

    // Métodos de validación privados

    private void validateImageParameters(Uri imageUri, String imageName, Folder folder) {
        if (imageUri == null) {
            throw new IllegalArgumentException("URI de imagen no puede ser null");
        }
        validateBasicParameters(imageName, folder);
    }

    private void validateBitmapParameters(Bitmap bitmap, String imageName, Folder folder) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap no puede ser null");
        }
        validateBasicParameters(imageName, folder);
    }

    private void validateBasicParameters(String imageName, Folder folder) {
        if (imageName == null || imageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de imagen no puede ser null o vacío");
        }
        if (folder == null) {
            throw new IllegalArgumentException("Carpeta no puede ser null");
        }
    }

    private void validateMoveParameters(Image image, Folder sourceFolder, Folder targetFolder) {
        if (image == null || sourceFolder == null || targetFolder == null) {
            throw new IllegalArgumentException("Imagen y carpetas no pueden ser null");
        }
        if (sourceFolder.equals(targetFolder)) {
            throw new IllegalArgumentException("Carpeta origen y destino no pueden ser la misma");
        }
    }

    private void validateDeleteParameters(Image image, Folder folder) {
        if (image == null || folder == null) {
            throw new IllegalArgumentException("Imagen y carpeta no pueden ser null");
        }
    }

    private void validateRenameParameters(Image image, String newName, Folder folder) {
        if (image == null || folder == null) {
            throw new IllegalArgumentException("Imagen y carpeta no pueden ser null");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nuevo nombre no puede ser null o vacío");
        }
    }
}
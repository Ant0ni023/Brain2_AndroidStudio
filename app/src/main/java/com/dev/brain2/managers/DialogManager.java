package com.dev.brain2.managers;

import android.content.Context;
import com.dev.brain2.dialogs.FolderDialog;
import com.dev.brain2.dialogs.ImageDialog;
import com.dev.brain2.models.Folder;
import com.dev.brain2.models.Image;

/**
 * Esta clase centraliza la gestión de todos los diálogos de la aplicación.
 */
public class DialogManager {
    private final FolderDialog folderDialog;    // Maneja diálogos de carpetas
    private final ImageDialog imageDialog;      // Maneja diálogos de imágenes

    /**
     * Constructor: inicializa los manejadores específicos de diálogos.
     *
     * @param context       Contexto de la aplicación.
     * @param folderManager Manager de carpetas.
     * @param imageManager  Manager de imágenes.
     */
    public DialogManager(Context context,
                         FolderManager folderManager,
                         ImageManager imageManager) {
        this.folderDialog = new FolderDialog(context, folderManager);
        this.imageDialog = new ImageDialog(context, imageManager, folderManager);
    }

    // ====== DIÁLOGOS DE CARPETAS ======

    /**
     * Muestra el diálogo para crear una nueva carpeta.
     *
     * @param listener Listener que recibe la carpeta creada cuando se completa la acción.
     */
    public void showFolderCreationDialog(FolderDialog.FolderDialogListener listener) {
        folderDialog.showCreationDialog(listener);
    }

    /**
     * Muestra el diálogo para editar una carpeta existente.
     *
     * @param folder   Carpeta a editar.
     * @param listener Listener que recibe la carpeta actualizada cuando se completa la edición.
     */
    public void showFolderEditDialog(Folder folder,
                                     FolderDialog.FolderDialogListener listener) {
        folderDialog.showEditDialog(folder, listener);
    }

    /**
     * Muestra el diálogo para seleccionar una carpeta existente.
     *
     * @param listener Listener que recibe la carpeta seleccionada cuando el usuario elige una.
     */
    public void showFolderSelectionDialog(FolderDialog.FolderDialogListener listener) {
        folderDialog.showSelectionDialog(listener);
    }

    // ====== DIÁLOGOS DE IMÁGENES ======

    /**
     * Muestra el diálogo para elegir de dónde obtener la imagen.
     *
     * @param galleryCallback Se ejecuta si el usuario elige la galería.
     * @param cameraCallback  Se ejecuta si el usuario elige la cámara.
     */
    public void showImageSourceDialog(Runnable galleryCallback,
                                      Runnable cameraCallback) {
        imageDialog.showSourceDialog(galleryCallback, cameraCallback);
    }

    /**
     * Muestra el diálogo para mover una imagen a otra carpeta.
     *
     * @param currentFolder  Carpeta actual donde está la imagen.
     * @param image          Imagen a mover.
     * @param onMoveComplete Se ejecuta cuando se completa el movimiento.
     */
    public void showImageMoveDialog(Folder currentFolder,
                                    Image image,
                                    Runnable onMoveComplete) {
        imageDialog.showMoveDialog(currentFolder, image, onMoveComplete);
    }

    /**
     * Muestra el diálogo para cambiar el nombre de una imagen.
     *
     * @param folder           Carpeta donde está la imagen.
     * @param image            Imagen a renombrar.
     * @param onRenameComplete Se ejecuta cuando se completa el cambio de nombre.
     */
    public void showImageRenameDialog(Folder folder,
                                      Image image,
                                      Runnable onRenameComplete) {
        imageDialog.showRenameDialog(folder, image, onRenameComplete);
    }

    /**
     * Muestra el diálogo para asignar nombre a una nueva imagen.
     *
     * @param listener Recibe el nombre seleccionado por el usuario.
     */
    public void showImageNameDialog(ImageDialog.ImageNameListener listener) {
        imageDialog.showImageNameDialog(listener);
    }
}

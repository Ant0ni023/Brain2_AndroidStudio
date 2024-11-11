package com.dev.brain2.managers;

import android.content.Context;
import com.dev.brain2.dialogs.FolderDialog;
import com.dev.brain2.dialogs.ImageDialog;
import com.dev.brain2.models.Folder;
import com.dev.brain2.models.Image;

// Esta clase centraliza la gestión de todos los diálogos de la aplicación
public class DialogManager {
    // Manejadores de diálogos específicos para carpetas e imágenes
    private final FolderDialog folderDialog;    // Maneja diálogos de carpetas
    private final ImageDialog imageDialog;      // Maneja diálogos de imágenes

    // Constructor: inicializa los manejadores específicos de diálogos
    public DialogManager(Context context,
                         FolderManager folderManager,
                         ImageManager imageManager) {
        // Creamos las instancias de los manejadores específicos
        this.folderDialog = new FolderDialog(context, folderManager);
        this.imageDialog = new ImageDialog(context, imageManager, folderManager);
    }

    // ====== DIÁLOGOS DE CARPETAS ======

    // Muestra el diálogo para crear una nueva carpeta
    // listener: recibe la carpeta creada cuando se completa la acción
    public void showFolderCreationDialog(FolderDialog.FolderDialogListener listener) {
        folderDialog.showCreationDialog(listener);
    }

    // Muestra el diálogo para editar una carpeta existente
    // folder: la carpeta a editar
    // listener: recibe la carpeta actualizada cuando se completa la edición
    public void showFolderEditDialog(Folder folder,
                                     FolderDialog.FolderDialogListener listener) {
        folderDialog.showEditDialog(folder, listener);
    }

    // Muestra el diálogo para seleccionar una carpeta existente
    // listener: recibe la carpeta seleccionada cuando el usuario elige una
    public void showFolderSelectionDialog(FolderDialog.FolderDialogListener listener) {
        folderDialog.showSelectionDialog(listener);
    }

    // ====== DIÁLOGOS DE IMÁGENES ======

    // Muestra el diálogo para elegir de dónde obtener la imagen
    // galleryCallback: se ejecuta si el usuario elige la galería
    // cameraCallback: se ejecuta si el usuario elige la cámara
    public void showImageSourceDialog(Runnable galleryCallback,
                                      Runnable cameraCallback) {
        imageDialog.showSourceDialog(galleryCallback, cameraCallback);
    }

    // Muestra el diálogo para mover una imagen a otra carpeta
    // currentFolder: carpeta actual donde está la imagen
    // image: la imagen a mover
    // onMoveComplete: se ejecuta cuando se completa el movimiento
    public void showImageMoveDialog(Folder currentFolder,
                                    Image image,
                                    Runnable onMoveComplete) {
        imageDialog.showMoveDialog(currentFolder, image, onMoveComplete);
    }

    // Muestra el diálogo para cambiar el nombre de una imagen
    // folder: carpeta donde está la imagen
    // image: la imagen a renombrar
    // onRenameComplete: se ejecuta cuando se completa el cambio de nombre
    public void showImageRenameDialog(Folder folder,
                                      Image image,
                                      Runnable onRenameComplete) {
        imageDialog.showRenameDialog(folder, image, onRenameComplete);
    }

    // Muestra el diálogo para asignar nombre a una nueva imagen
    // listener: recibe el nombre seleccionado por el usuario
    public void showImageNameDialog(ImageDialog.ImageNameListener listener) {
        imageDialog.showImageNameDialog(listener);
    }
}
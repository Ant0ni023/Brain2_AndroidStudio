package com.dev.brain2.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.dev.brain2.R;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.models.Image;
import com.dev.brain2.utils.Notifier;
import java.util.List;

/**
 * Esta clase maneja todos los diálogos relacionados con las imágenes.
 */
public class ImageDialog {
    // Variables necesarias para gestionar los diálogos
    private final Context context;
    private final ImageManager imageManager;
    private final FolderManager folderManager;

    /**
     * Constructor.
     *
     * @param context       Contexto de la aplicación.
     * @param imageManager  Manager para manejar las imágenes.
     * @param folderManager Manager para manejar las carpetas.
     */
    public ImageDialog(Context context, ImageManager imageManager, FolderManager folderManager) {
        this.context = context;
        this.imageManager = imageManager;
        this.folderManager = folderManager;
    }

    /**
     * Interfaz para notificar cuando se selecciona un nombre para la imagen.
     */
    public interface ImageNameListener {
        void onNameSelected(String name);
    }

    /**
     * Muestra el diálogo para seleccionar el origen de una imagen.
     *
     * @param galleryCallback Callback si se elige la galería.
     * @param cameraCallback  Callback si se elige la cámara.
     */
    public void showSourceDialog(Runnable galleryCallback, Runnable cameraCallback) {
        // Definimos las opciones disponibles
        String[] options = {"Galería", "Cámara"};

        // Creamos y mostramos el diálogo
        new AlertDialog.Builder(context)
                .setTitle("Seleccionar origen")
                .setItems(options, (dialog, which) -> {
                    // Ejecutamos el callback correspondiente según la selección
                    if (which == 0) galleryCallback.run();
                    else if (which == 1) cameraCallback.run();
                })
                .show();
    }

    /**
     * Muestra el diálogo para mover una imagen a otra carpeta.
     *
     * @param currentFolder Carpeta actual de la imagen.
     * @param image         Imagen a mover.
     * @param onMoveComplete Callback al completar el movimiento.
     */
    public void showMoveDialog(Folder currentFolder, Image image, Runnable onMoveComplete) {
        // Obtenemos las carpetas disponibles (excluyendo la actual)
        List<Folder> availableFolders = folderManager.getAvailableFolders(currentFolder);

        // Verificamos si hay carpetas disponibles
        if (availableFolders.isEmpty()) {
            Notifier.showInfo(context, "No hay otras carpetas disponibles");
            return;
        }

        // Creamos el array de nombres de carpetas
        String[] folderNames = availableFolders.stream()
                .map(Folder::getName)
                .toArray(String[]::new);

        // Mostramos el diálogo con las opciones
        new AlertDialog.Builder(context)
                .setTitle("Mover imagen a...")
                .setItems(folderNames, (dialog, which) -> {
                    // Intentamos mover la imagen a la carpeta seleccionada
                    Folder targetFolder = availableFolders.get(which);
                    if (imageManager.moveImage(image, currentFolder, targetFolder)) {
                        Notifier.showInfo(context,
                                "Imagen movida a " + targetFolder.getName());
                        onMoveComplete.run();
                    } else {
                        Notifier.showError(context, "Error al mover la imagen");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Muestra el diálogo para renombrar una imagen.
     *
     * @param folder           Carpeta donde está la imagen.
     * @param image            Imagen a renombrar.
     * @param onRenameComplete Callback al completar el renombrado.
     */
    public void showRenameDialog(Folder folder, Image image, Runnable onRenameComplete) {
        // Creamos la vista del diálogo
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_image_name, null);
        EditText nameInput = dialogView.findViewById(R.id.imageNameInput);
        nameInput.setText(image.getName());

        // Mostramos el diálogo
        new AlertDialog.Builder(context)
                .setTitle("Renombrar imagen")
                .setView(dialogView)
                .setPositiveButton("Renombrar", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        if (imageManager.renameImage(image, newName, folder)) {
                            Notifier.showInfo(context, "Imagen renombrada");
                            onRenameComplete.run();
                        } else {
                            Notifier.showError(context, "Error al renombrar la imagen");
                        }
                    } else {
                        Notifier.showError(context, "El nombre no puede estar vacío");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Muestra el diálogo para poner nombre a una nueva imagen.
     *
     * @param listener Listener que recibe el nombre seleccionado.
     */
    public void showImageNameDialog(ImageNameListener listener) {
        // Creamos la vista del diálogo
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_image_name, null);
        EditText nameInput = dialogView.findViewById(R.id.imageNameInput);

        // Mostramos el diálogo
        new AlertDialog.Builder(context)
                .setTitle("Nombre de la imagen")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String imageName = nameInput.getText().toString().trim();
                    if (!imageName.isEmpty()) {
                        listener.onNameSelected(imageName);
                    } else {
                        Notifier.showError(context, "El nombre no puede estar vacío");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

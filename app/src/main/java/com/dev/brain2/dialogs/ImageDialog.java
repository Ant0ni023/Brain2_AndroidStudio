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
import java.util.stream.Collectors;

/**
 * Esta clase maneja todos los diálogos relacionados con las imágenes.
 */
public class ImageDialog {

    private final Context appContext;
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
        this.appContext = context;
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
        String[] options = {"Galería", "Cámara"};

        new AlertDialog.Builder(appContext)
                .setTitle("Seleccionar origen")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        galleryCallback.run();
                    } else if (which == 1) {
                        cameraCallback.run();
                    }
                })
                .show();
    }

    /**
     * Muestra el diálogo para mover una imagen a otra carpeta.
     *
     * @param currentFolder  Carpeta actual de la imagen.
     * @param image          Imagen a mover.
     * @param onMoveComplete Callback al completar el movimiento.
     */
    public void showMoveDialog(Folder currentFolder, Image image, Runnable onMoveComplete) {
        List<Folder> availableFolders = folderManager.getAvailableFolders(currentFolder);

        if (availableFolders.isEmpty()) {
            Notifier.showInfo(appContext, "No hay otras carpetas disponibles");
            return;
        }

        showFolderMoveSelectionDialog(availableFolders, currentFolder, image, onMoveComplete);
    }

    /**
     * Muestra el diálogo de selección de carpeta para mover la imagen.
     *
     * @param availableFolders Lista de carpetas disponibles.
     * @param currentFolder    Carpeta actual de la imagen.
     * @param image            Imagen a mover.
     * @param onMoveComplete   Callback al completar el movimiento.
     */
    private void showFolderMoveSelectionDialog(List<Folder> availableFolders, Folder currentFolder, Image image, Runnable onMoveComplete) {
        String[] folderNames = availableFolders.stream()
                .map(Folder::getName)
                .toArray(String[]::new);

        new AlertDialog.Builder(appContext)
                .setTitle("Mover imagen a...")
                .setItems(folderNames, (dialog, which) -> {
                    handleImageMoveSelection(availableFolders.get(which), currentFolder, image, onMoveComplete);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Maneja la selección de carpeta para mover la imagen.
     *
     * @param targetFolder   Carpeta destino.
     * @param currentFolder  Carpeta actual.
     * @param image          Imagen a mover.
     * @param onMoveComplete Callback al completar el movimiento.
     */
    private void handleImageMoveSelection(Folder targetFolder, Folder currentFolder, Image image, Runnable onMoveComplete) {
        if (imageManager.moveImage(image, currentFolder, targetFolder)) {
            Notifier.showInfo(appContext, "Imagen movida a " + targetFolder.getName());
            onMoveComplete.run();
        } else {
            Notifier.showError(appContext, "Error al mover la imagen");
        }
    }

    /**
     * Muestra el diálogo para renombrar una imagen.
     *
     * @param folder           Carpeta donde está la imagen.
     * @param image            Imagen a renombrar.
     * @param onRenameComplete Callback al completar el renombrado.
     */
    public void showRenameDialog(Folder folder, Image image, Runnable onRenameComplete) {
        View dialogView = createRenameDialogView(image);
        EditText editTextImageName = dialogView.findViewById(R.id.imageNameInput);

        new AlertDialog.Builder(appContext)
                .setTitle("Renombrar imagen")
                .setView(dialogView)
                .setPositiveButton("Renombrar", (dialog, which) -> {
                    handleRenamePositiveClick(folder, image, editTextImageName, onRenameComplete);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Crea la vista para el diálogo de renombrar imagen.
     *
     * @param image Imagen a renombrar.
     * @return Vista del diálogo.
     */
    private View createRenameDialogView(Image image) {
        View dialogView = LayoutInflater.from(appContext)
                .inflate(R.layout.dialog_image_name, null);
        EditText editTextImageName = dialogView.findViewById(R.id.imageNameInput);
        editTextImageName.setText(image.getName());
        return dialogView;
    }

    /**
     * Maneja el evento de clic positivo en el diálogo de renombrar imagen.
     *
     * @param folder             Carpeta donde está la imagen.
     * @param image              Imagen a renombrar.
     * @param editTextImageName  Campo de texto para el nombre.
     * @param onRenameComplete   Callback al completar el renombrado.
     */
    private void handleRenamePositiveClick(Folder folder, Image image, EditText editTextImageName, Runnable onRenameComplete) {
        String newName = editTextImageName.getText().toString().trim();

        if (!newName.isEmpty()) {
            if (imageManager.renameImage(image, newName, folder)) {
                Notifier.showInfo(appContext, "Imagen renombrada");
                onRenameComplete.run();
            } else {
                Notifier.showError(appContext, "Error al renombrar la imagen");
            }
        } else {
            Notifier.showError(appContext, "El nombre no puede estar vacío");
        }
    }

    /**
     * Muestra el diálogo para poner nombre a una nueva imagen.
     *
     * @param listener Listener que recibe el nombre seleccionado.
     */
    public void showImageNameDialog(ImageNameListener listener) {
        View dialogView = createImageNameDialogView();
        EditText editTextImageName = dialogView.findViewById(R.id.imageNameInput);

        new AlertDialog.Builder(appContext)
                .setTitle("Nombre de la imagen")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    handleImageNamePositiveClick(editTextImageName, listener);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Crea la vista para el diálogo de nombrar imagen.
     *
     * @return Vista del diálogo.
     */
    private View createImageNameDialogView() {
        return LayoutInflater.from(appContext)
                .inflate(R.layout.dialog_image_name, null);
    }

    /**
     * Maneja el evento de clic positivo en el diálogo de nombrar imagen.
     *
     * @param editTextImageName Campo de texto para el nombre.
     * @param listener          Listener que recibe el nombre seleccionado.
     */
    private void handleImageNamePositiveClick(EditText editTextImageName, ImageNameListener listener) {
        String imageName = editTextImageName.getText().toString().trim();

        if (!imageName.isEmpty()) {
            listener.onNameSelected(imageName);
        } else {
            Notifier.showError(appContext, "El nombre no puede estar vacío");
        }
    }
}

package com.dev.brain2.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.dev.brain2.R;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.ColorManager;
import com.dev.brain2.utils.Notifier;

import java.util.List;

/**
 * Esta clase maneja todos los diálogos relacionados con las carpetas.
 */
public class FolderDialog {

    private final Context appContext;
    private final FolderManager folderManager;

    /**
     * Constructor.
     *
     * @param context       Contexto de la aplicación.
     * @param folderManager Manager para manejar las carpetas.
     */
    public FolderDialog(Context context, FolderManager folderManager) {
        this.appContext = context;
        this.folderManager = folderManager;
    }

    /**
     * Interfaz para notificar cuando se completa una acción con una carpeta.
     */
    public interface FolderDialogListener {
        void onFolderActionComplete(Folder folder);
    }

    /**
     * Muestra el diálogo para crear una nueva carpeta.
     *
     * @param listener Listener que recibe la carpeta creada.
     */
    public void showCreationDialog(FolderDialogListener listener) {
        showFolderDialog(null, listener, true);
    }

    /**
     * Muestra el diálogo para editar una carpeta existente.
     *
     * @param folder   Carpeta a editar.
     * @param listener Listener que recibe la carpeta actualizada.
     */
    public void showEditDialog(Folder folder, FolderDialogListener listener) {
        showFolderDialog(folder, listener, false);
    }

    /**
     * Muestra el diálogo para seleccionar una carpeta existente.
     *
     * @param listener Listener que recibe la carpeta seleccionada.
     */
    public void showSelectionDialog(FolderDialogListener listener) {
        List<Folder> folders = folderManager.getFolders();

        if (folders.isEmpty()) {
            showNoFoldersDialog(listener);
        } else {
            showFolderSelectionDialog(folders, listener);
        }
    }

    /**
     * Muestra un diálogo cuando no hay carpetas disponibles.
     *
     * @param listener Listener para crear una nueva carpeta.
     */
    private void showNoFoldersDialog(FolderDialogListener listener) {
        new AlertDialog.Builder(appContext)
                .setTitle("No hay carpetas disponibles")
                .setMessage("¿Desea crear una carpeta?")
                .setPositiveButton("Crear", (dialog, which) -> showCreationDialog(listener))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Muestra un diálogo para seleccionar una carpeta existente.
     *
     * @param folders  Lista de carpetas disponibles.
     * @param listener Listener que recibe la carpeta seleccionada.
     */
    private void showFolderSelectionDialog(List<Folder> folders, FolderDialogListener listener) {
        String[] folderNames = folders.stream()
                .map(Folder::getName)
                .toArray(String[]::new);

        new AlertDialog.Builder(appContext)
                .setTitle("Seleccionar Carpeta")
                .setItems(folderNames, (dialog, which) ->
                        listener.onFolderActionComplete(folders.get(which)))
                .setPositiveButton("Nueva Carpeta", (dialog, which) ->
                        showCreationDialog(listener))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Método principal que maneja el diálogo de crear/editar carpeta.
     *
     * @param folder     Carpeta a editar (nulo si es creación).
     * @param listener   Listener que recibe la carpeta creada/actualizada.
     * @param isCreation Indica si es creación o edición.
     */
    private void showFolderDialog(Folder folder, FolderDialogListener listener, boolean isCreation) {
        View dialogView = createFolderDialogView(folder, isCreation);
        EditText editTextFolderName = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

        new AlertDialog.Builder(appContext)
                .setTitle(isCreation ? "Crear Carpeta" : "Editar Carpeta")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    handleFolderDialogPositiveClick(folder, listener, isCreation, editTextFolderName, colorSpinner);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Crea la vista para el diálogo de carpeta.
     *
     * @param folder     Carpeta a editar (nulo si es creación).
     * @param isCreation Indica si es creación o edición.
     * @return Vista del diálogo.
     */
    private View createFolderDialogView(Folder folder, boolean isCreation) {
        View dialogView = LayoutInflater.from(appContext)
                .inflate(R.layout.dialog_create_folder, null);
        EditText editTextFolderName = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

        setupColorSpinner(colorSpinner);

        if (!isCreation && folder != null) {
            prefillDialogFields(editTextFolderName, colorSpinner, folder);
        }

        return dialogView;
    }

    /**
     * Configura el spinner de colores.
     *
     * @param colorSpinner Spinner a configurar.
     */
    private void setupColorSpinner(Spinner colorSpinner) {
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(
                appContext,
                android.R.layout.simple_spinner_item,
                ColorManager.FOLDER_COLOR_NAMES
        );
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);
    }

    /**
     * Rellena los campos del diálogo con los datos actuales.
     *
     * @param editTextFolderName Campo de texto para el nombre.
     * @param colorSpinner       Spinner de colores.
     * @param folder             Carpeta actual.
     */
    private void prefillDialogFields(EditText editTextFolderName, Spinner colorSpinner, Folder folder) {
        editTextFolderName.setText(folder.getName());
        int colorIndex = ColorManager.getFolderColorIndex(folder.getColor());
        colorSpinner.setSelection(colorIndex);
    }

    /**
     * Maneja el evento de clic positivo en el diálogo de carpeta.
     *
     * @param folder             Carpeta a editar (nulo si es creación).
     * @param listener           Listener que recibe la carpeta creada/actualizada.
     * @param isCreation         Indica si es creación o edición.
     * @param editTextFolderName Campo de texto para el nombre.
     * @param colorSpinner       Spinner de colores.
     */
    private void handleFolderDialogPositiveClick(Folder folder, FolderDialogListener listener, boolean isCreation, EditText editTextFolderName, Spinner colorSpinner) {
        String folderName = editTextFolderName.getText().toString().trim();
        String selectedColor = ColorManager.getFolderColorByIndex(colorSpinner.getSelectedItemPosition());

        if (folderName.isEmpty()) {
            Notifier.showError(appContext, "El nombre no puede estar vacío");
            return;
        }

        if (isCreation) {
            Folder newFolder = createFolder(folderName, selectedColor);
            listener.onFolderActionComplete(newFolder);
        } else if (folder != null) {
            updateFolder(folder, folderName, selectedColor);
            listener.onFolderActionComplete(folder);
        }
    }

    /**
     * Método auxiliar para crear una nueva carpeta.
     *
     * @param name  Nombre de la carpeta.
     * @param color Color asignado a la carpeta.
     * @return La carpeta creada.
     */
    private Folder createFolder(String name, String color) {
        Folder newFolder = new Folder(name, color);
        folderManager.addFolder(newFolder);
        return newFolder;
    }

    /**
     * Método auxiliar para actualizar una carpeta existente.
     *
     * @param folder   Carpeta a actualizar.
     * @param newName  Nuevo nombre de la carpeta.
     * @param newColor Nuevo color de la carpeta.
     */
    private void updateFolder(Folder folder, String newName, String newColor) {
        folder.setName(newName);
        folder.setColor(newColor);
        folderManager.updateFolder(folder);
    }
}

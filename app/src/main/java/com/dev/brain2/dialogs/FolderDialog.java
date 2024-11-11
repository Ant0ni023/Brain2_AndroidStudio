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
import com.dev.brain2.utils.FolderColor;
import com.dev.brain2.utils.Notifier;
import java.util.List;

// Esta clase maneja todos los diálogos relacionados con las carpetas
public class FolderDialog {
    // Variables necesarias para crear y gestionar los diálogos
    private final Context context;
    private final FolderManager folderManager;

    // Constructor
    public FolderDialog(Context context, FolderManager folderManager) {
        this.context = context;
        this.folderManager = folderManager;
    }

    // Interfaz para notificar cuando se completa una acción con una carpeta
    public interface FolderDialogListener {
        void onFolderActionComplete(Folder folder);
    }

    // Muestra el diálogo para crear una nueva carpeta
    public void showCreationDialog(FolderDialogListener listener) {
        showFolderDialog(null, listener, true);
    }

    // Muestra el diálogo para editar una carpeta existente
    public void showEditDialog(Folder folder, FolderDialogListener listener) {
        showFolderDialog(folder, listener, false);
    }

    // Muestra el diálogo para seleccionar una carpeta existente
    public void showSelectionDialog(FolderDialogListener listener) {
        // Obtenemos la lista de carpetas disponibles
        List<Folder> folders = folderManager.getFolders();

        // Si no hay carpetas, mostramos diálogo para crear una
        if (folders.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle("No hay carpetas disponibles")
                    .setMessage("¿Desea crear una carpeta?")
                    .setPositiveButton("Crear", (dialog, which) -> showCreationDialog(listener))
                    .setNegativeButton("Cancelar", null)
                    .show();
            return;
        }

        // Si hay carpetas, mostramos la lista para seleccionar
        String[] folderNames = folders.stream()
                .map(Folder::getName)
                .toArray(String[]::new);

        new AlertDialog.Builder(context)
                .setTitle("Seleccionar Carpeta")
                .setItems(folderNames, (dialog, which) ->
                        listener.onFolderActionComplete(folders.get(which)))
                .setPositiveButton("Nueva Carpeta", (dialog, which) ->
                        showCreationDialog(listener))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Método principal que maneja el diálogo de crear/editar carpeta
    private void showFolderDialog(Folder folder, FolderDialogListener listener, boolean isCreation) {
        // Creamos la vista del diálogo
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_create_folder, null);
        EditText folderNameInput = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

        // Configuramos el spinner de colores
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                FolderColor.COLOR_NAMES
        );
        colorAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        colorSpinner.setAdapter(colorAdapter);

        // Si estamos editando, rellenamos los campos con los datos actuales
        if (!isCreation && folder != null) {
            folderNameInput.setText(folder.getName());
            colorSpinner.setSelection(FolderColor.getColorIndex(folder.getColor()));
        }

        // Creamos y mostramos el diálogo
        new AlertDialog.Builder(context)
                .setTitle(isCreation ? "Crear Carpeta" : "Editar Carpeta")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Obtenemos los datos ingresados
                    String folderName = folderNameInput.getText().toString().trim();
                    String selectedColor = FolderColor.getColorByIndex(
                            colorSpinner.getSelectedItemPosition()
                    );

                    // Validamos el nombre
                    if (folderName.isEmpty()) {
                        Notifier.showError(context, "El nombre no puede estar vacío");
                        return;
                    }

                    // Creamos o actualizamos la carpeta según corresponda
                    if (isCreation) {
                        Folder newFolder = createFolder(folderName, selectedColor);
                        listener.onFolderActionComplete(newFolder);
                    } else if (folder != null) {
                        updateFolder(folder, folderName, selectedColor);
                        listener.onFolderActionComplete(folder);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Método auxiliar para crear una nueva carpeta
    private Folder createFolder(String name, String color) {
        Folder newFolder = new Folder(name, color);
        folderManager.addFolder(newFolder);
        return newFolder;
    }

    // Método auxiliar para actualizar una carpeta existente
    private void updateFolder(Folder folder, String newName, String newColor) {
        folder.setName(newName);
        folder.setColor(newColor);
        folderManager.updateFolder(folder);
    }
}
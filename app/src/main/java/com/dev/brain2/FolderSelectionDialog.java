package com.dev.brain2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.List;

public class FolderSelectionDialog {

    private final Context context;
    private final FolderManager folderManager;
    private final OnFolderSelectedListener listener;

    public interface OnFolderSelectedListener {
        void onFolderSelected(Folder folder);
    }

    public FolderSelectionDialog(Context context, FolderManager folderManager, OnFolderSelectedListener listener) {
        this.context = context;
        this.folderManager = folderManager;
        this.listener = listener;
    }

    public void show() {
        List<String> folderList = new ArrayList<>(folderManager.getFolderNames());
        if (folderList.isEmpty()) {
            showCreateFolderDialog();
        } else {
            folderList.add("Crear nueva carpeta");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, folderList);
            new AlertDialog.Builder(context)
                    .setTitle("Seleccionar Carpeta")
                    .setAdapter(adapter, (dialog, which) -> {
                        String selectedFolderName = folderList.get(which);
                        if (selectedFolderName.equals("Crear nueva carpeta")) {
                            showCreateFolderDialog();
                        } else {
                            Folder selectedFolder = folderManager.getFolderByName(selectedFolderName);
                            showEditFolderDialog(selectedFolder); // Aquí mostramos el diálogo de edición
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
    }

    private void showCreateFolderDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_folder, null);
        EditText input = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

        String[] colorNames = {"Azul", "Rojo", "Verde", "Amarillo", "Naranja", "Morado"};
        String[] colorValues = {"#1E90FF", "#FF0000", "#00FF00", "#FFFF00", "#FFA500", "#800080"};
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, colorNames);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);

        new AlertDialog.Builder(context)
                .setTitle("Crear Carpeta")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String folderName = input.getText().toString().trim();
                    int selectedColorPosition = colorSpinner.getSelectedItemPosition();
                    String selectedColor = colorValues[selectedColorPosition];
                    if (!folderName.isEmpty()) {
                        folderManager.addFolder(folderName, selectedColor);
                        Folder newFolder = folderManager.getFolderByName(folderName);
                        listener.onFolderSelected(newFolder);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Método para mostrar el diálogo de edición de carpetas
    private void showEditFolderDialog(Folder folder) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_folder, null);
        EditText input = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

        String[] colorNames = {"Azul", "Rojo", "Verde", "Amarillo", "Naranja", "Morado"};
        String[] colorValues = {"#1E90FF", "#FF0000", "#00FF00", "#FFFF00", "#FFA500", "#800080"};
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, colorNames);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);

        // Inicializar el nombre y color actual de la carpeta
        input.setText(folder.getName());
        int colorPosition = java.util.Arrays.asList(colorValues).indexOf(folder.getColor());
        if (colorPosition >= 0) {
            colorSpinner.setSelection(colorPosition);
        }

        new AlertDialog.Builder(context)
                .setTitle("Modificar Carpeta")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String folderName = input.getText().toString().trim();
                    int selectedColorPosition = colorSpinner.getSelectedItemPosition();
                    String selectedColor = colorValues[selectedColorPosition];

                    if (!folderName.isEmpty()) {
                        folder.setName(folderName);
                        folder.setColor(selectedColor);
                        folderManager.updateFolder(folder); // Actualiza la carpeta en FolderManager
                        listener.onFolderSelected(folder); // Llama al listener para notificar la actualización
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

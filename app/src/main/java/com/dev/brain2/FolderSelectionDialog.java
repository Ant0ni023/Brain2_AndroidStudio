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
            // No hay carpetas, mostrar el diálogo para crear una nueva
            showCreateFolderDialog();
        } else {
            // Agregar opción para crear nueva carpeta
            folderList.add("Crear nueva carpeta");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_list_item_1,
                    folderList
            );

            new AlertDialog.Builder(context)
                    .setTitle("Seleccionar Carpeta")
                    .setAdapter(adapter, (dialog, which) -> {
                        String selectedFolderName = folderList.get(which);
                        if (selectedFolderName.equals("Crear nueva carpeta")) {
                            showCreateFolderDialog();
                        } else {
                            Folder selectedFolder = folderManager.getFolderByName(selectedFolderName);
                            listener.onFolderSelected(selectedFolder);
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

        // Nombres y valores de colores
        String[] colorNames = {"Azul", "Rojo", "Verde", "Amarillo", "Naranja", "Morado"};
        String[] colorValues = {"#1E90FF", "#FF0000", "#00FF00", "#FFFF00", "#FFA500", "#800080"};

        // Configurar el adaptador para el Spinner
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
}

package com.dev.brain2.utils;

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
import com.dev.brain2.interfaces.OnFolderSelectedListener;

import java.util.ArrayList;
import java.util.List;


public class FolderSelectionDialog {

    // Constantes para la configuración de colores
    private static final String[] COLOR_NAMES = {"Azul", "Rojo", "Verde", "Amarillo", "Naranja", "Morado"};
    private static final String[] COLOR_VALUES = {"#1E90FF", "#FF0000", "#00FF00", "#FFFF00", "#FFA500", "#800080"};

    // Dependencias principales
    private final Context context;
    private final FolderManager folderManager;
    private final OnFolderSelectedListener listener;


    public FolderSelectionDialog(Context context, FolderManager folderManager, OnFolderSelectedListener listener) {
        if (context == null || folderManager == null || listener == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser null");
        }

        this.context = context;
        this.folderManager = folderManager;
        this.listener = listener;
    }


    public void show() {
        List<Folder> folders = folderManager.getFolders();
        List<String> folderNames = new ArrayList<>();

        for (Folder folder : folders) {
            folderNames.add(folder.getName());
        }

        if (folderNames.isEmpty()) {
            showCreateFolderDialog();
        } else {
            showFolderSelectionDialog(folderNames);
        }
    }


    private void showFolderSelectionDialog(List<String> folderNames) {
        folderNames.add("Crear nueva carpeta");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                folderNames
        );

        new AlertDialog.Builder(context)
                .setTitle("Seleccionar Carpeta")
                .setAdapter(adapter, (dialog, which) -> {
                    String selectedFolderName = folderNames.get(which);
                    if (selectedFolderName.equals("Crear nueva carpeta")) {
                        showCreateFolderDialog();
                    } else {
                        Folder selectedFolder = folderManager.getFolderByName(selectedFolderName);
                        if (selectedFolder != null) {
                            listener.onFolderSelected(selectedFolder);
                        } else {
                            showToast("Carpeta no encontrada");
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void showCreateFolderDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_folder, null);
        EditText folderNameInput = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                COLOR_NAMES
        );
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);

        new AlertDialog.Builder(context)
                .setTitle("Crear Carpeta")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String folderName = folderNameInput.getText().toString().trim();
                    if (!folderName.isEmpty()) {
                        int colorPosition = colorSpinner.getSelectedItemPosition();
                        String selectedColor = COLOR_VALUES[colorPosition];

                        Folder newFolder = new Folder(folderName, selectedColor);
                        folderManager.addFolder(newFolder);
                        listener.onFolderSelected(newFolder);
                    } else {
                        showToast("El nombre de la carpeta no puede estar vacío");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void showToast(String message) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}

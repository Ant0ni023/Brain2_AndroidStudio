package com.dev.brain2.interfaces;

import com.dev.brain2.models.Folder;

// Esta interfaz define el método para notificar cuando el usuario selecciona una carpeta
// Se usa principalmente en diálogos de selección
public interface OnFolderSelectedListener {
    // Se llama cuando una carpeta es seleccionada
    // Parámetros:
    //   folder: la carpeta que fue seleccionada por el usuario
    void onFolderSelected(Folder folder);
}
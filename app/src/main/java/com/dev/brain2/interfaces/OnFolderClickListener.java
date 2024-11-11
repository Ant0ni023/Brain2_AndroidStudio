package com.dev.brain2.interfaces;

import com.dev.brain2.models.Folder;

// Esta interfaz define los métodos para manejar las interacciones del usuario con las carpetas
public interface OnFolderClickListener {
    // Se llama cuando el usuario hace clic normal en una carpeta
    // Parámetros:
    //   folder: la carpeta que fue clickeada
    void onFolderClick(Folder folder);

    // Se llama cuando el usuario mantiene presionada una carpeta
    // Parámetros:
    //   folder: la carpeta que fue presionada
    //   position: la posición de la carpeta en la lista
    void onFolderLongClick(Folder folder, int position);
}
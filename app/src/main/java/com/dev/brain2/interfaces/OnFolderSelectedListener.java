package com.dev.brain2.interfaces;

import com.dev.brain2.models.Folder;


//Interfaz para notificar cuando se selecciona una carpeta.

public interface OnFolderSelectedListener {
    void onFolderSelected(Folder folder);
}

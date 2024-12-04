package com.dev.brain2.interfaces;

import com.dev.brain2.models.Folder;

/**
 * Esta interfaz define los métodos para manejar las interacciones del usuario con las carpetas.
 */
public interface OnFolderClickListener {

    /**
     * Se llama cuando el usuario hace clic normal en una carpeta.
     *
     * @param clickedFolder La carpeta que fue clickeada.
     */
    void onFolderClick(Folder clickedFolder);

    /**
     * Se llama cuando el usuario mantiene presionada una carpeta.
     *
     * @param longClickedFolder La carpeta que fue presionada.
     */
    void onFolderLongClick(Folder longClickedFolder);
}

package com.dev.brain2.interfaces;

import com.dev.brain2.models.Folder;

/**
 * Esta interfaz define los métodos para manejar las interacciones del usuario con las carpetas.
 */
public interface OnFolderClickListener {
    /**
     * Se llama cuando el usuario hace clic normal en una carpeta.
     *
     * @param folder La carpeta que fue clickeada.
     */
    void onFolderClick(Folder folder);

    /**
     * Se llama cuando el usuario mantiene presionada una carpeta.
     *
     * @param folder   La carpeta que fue presionada.
     * @param position La posición de la carpeta en la lista.
     */
    void onFolderLongClick(Folder folder, int position);
}

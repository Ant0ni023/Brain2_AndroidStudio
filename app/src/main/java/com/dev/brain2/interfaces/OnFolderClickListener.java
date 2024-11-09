package com.dev.brain2.interfaces;

import com.dev.brain2.models.Folder;

//Interfaz para manejar eventos de clic en carpetas.

public interface OnFolderClickListener {
    void onFolderClick(Folder folder);
    void onFolderLongClick(Folder folder, int position);
}

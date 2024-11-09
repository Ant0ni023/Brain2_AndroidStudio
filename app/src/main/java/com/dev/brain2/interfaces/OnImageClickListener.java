package com.dev.brain2.interfaces;

import com.dev.brain2.models.Image;


//Interfaz para manejar eventos de clic en imágenes.

public interface OnImageClickListener {
    void onImageClick(Image image);
    void onImageLongClick(Image image, int position);
}

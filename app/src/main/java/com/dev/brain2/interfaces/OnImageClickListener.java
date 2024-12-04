package com.dev.brain2.interfaces;

import com.dev.brain2.models.Image;

/**
 * Esta interfaz define los métodos para manejar las interacciones del usuario con las imágenes.
 */
public interface OnImageClickListener {

    /**
     * Se llama cuando el usuario hace clic normal en una imagen.
     *
     * @param clickedImage La imagen que fue clickeada.
     */
    void onImageClick(Image clickedImage);

    /**
     * Se llama cuando el usuario mantiene presionada una imagen.
     *
     * @param longClickedImage La imagen que fue presionada.
     */
    void onImageLongClick(Image longClickedImage);
}

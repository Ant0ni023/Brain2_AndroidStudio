package com.dev.brain2.utils;

import com.dev.brain2.models.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que maneja la lógica de búsqueda entre las imágenes.
 */
public class SearchHandler {

    private List<Image> allImages;

    /**
     * Constructor.
     *
     * @param allImages Lista de todas las imágenes disponibles.
     */
    public SearchHandler(List<Image> allImages) {
        this.allImages = allImages;
    }

    /**
     * Realiza la búsqueda en base a la consulta proporcionada.
     *
     * @param query Consulta de búsqueda.
     * @return Lista de imágenes que coinciden con la consulta.
     */
    public List<Image> performSearch(String query) {
        List<Image> filteredImages = new ArrayList<>();
        for (Image image : allImages) {
            if (image.getName().toLowerCase().contains(query) || image.getTags().contains(query)) {
                filteredImages.add(image);
            }
        }
        return filteredImages;
    }
}

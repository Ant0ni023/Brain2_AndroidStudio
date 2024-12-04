package com.dev.brain2.utils;

import com.dev.brain2.models.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que maneja la lógica de búsqueda entre las imágenes.
 */
public class SearchHandler {

    private final List<Image> allImages;

    /**
     * Constructor.
     *
     * @param allImages Lista de todas las imágenes disponibles.
     */
    public SearchHandler(List<Image> allImages) {
        this.allImages = new ArrayList<>(allImages);
    }

    /**
     * Realiza la búsqueda en base a la consulta proporcionada.
     *
     * @param query Consulta de búsqueda.
     * @return Lista de imágenes que coinciden con la consulta.
     */
    public List<Image> performSearch(String query) {
        return filterImages(query.toLowerCase());
    }

    /**
     * Filtra las imágenes que coinciden con la consulta.
     *
     * @param query Consulta en minúsculas.
     * @return Lista de imágenes filtradas.
     */
    private List<Image> filterImages(String query) {
        List<Image> filteredImages = new ArrayList<>();
        for (Image image : allImages) {
            if (matchesQuery(image, query)) {
                filteredImages.add(image);
            }
        }
        return filteredImages;
    }

    /**
     * Verifica si una imagen coincide con la consulta.
     *
     * @param image Imagen a verificar.
     * @param query Consulta en minúsculas.
     * @return Verdadero si coincide, falso de lo contrario.
     */
    private boolean matchesQuery(Image image, String query) {
        return image.getName().toLowerCase().contains(query) || image.getTags().contains(query);
    }
}

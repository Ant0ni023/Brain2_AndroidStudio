package com.dev.brain2.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Esta clase representa una carpeta que puede contener imágenes.
 */
public class Folder implements Serializable {
    // Propiedades básicas de la carpeta
    private String id;          // Identificador único de la carpeta
    private String name;        // Nombre de la carpeta
    private String color;       // Color para mostrar en la interfaz
    private List<Image> images; // Lista de imágenes en la carpeta

    /**
     * Constructor: crea una nueva carpeta con un nombre y color.
     *
     * @param name  Nombre de la carpeta.
     * @param color Color asignado a la carpeta.
     */
    public Folder(String name, String color) {
        this.name = name;
        this.color = color;
        this.images = new ArrayList<>(); // Inicializamos la lista de imágenes vacía
    }

    // GETTERS Y SETTERS BÁSICOS

    /**
     * Obtiene el ID de la carpeta.
     *
     * @return El ID de la carpeta.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el ID de la carpeta.
     *
     * @param id El nuevo ID de la carpeta.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la carpeta.
     *
     * @return El nombre de la carpeta.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la carpeta.
     *
     * @param name El nuevo nombre de la carpeta.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene el color de la carpeta.
     *
     * @return El color de la carpeta.
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece el color de la carpeta.
     *
     * @param color El nuevo color de la carpeta.
     */
    public void setColor(String color) {
        this.color = color;
    }

    // MÉTODOS PARA MANEJAR IMÁGENES

    /**
     * Obtiene la lista de imágenes (solo lectura para evitar modificaciones directas).
     *
     * @return Lista inmutable de imágenes.
     */
    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }

    /**
     * Establece una nueva lista de imágenes (crea una copia para mayor seguridad).
     *
     * @param images Lista de imágenes a establecer.
     */
    public void setImages(List<Image> images) {
        this.images = new ArrayList<>(images);
    }

    /**
     * Agrega una nueva imagen a la carpeta.
     *
     * @param image La imagen a agregar.
     */
    public void addImage(Image image) {
        images.add(image);
    }

    /**
     * Elimina una imagen de la carpeta.
     *
     * @param image La imagen a eliminar.
     * @return true si la imagen fue eliminada, false de lo contrario.
     */
    public boolean removeImage(Image image) {
        return images.remove(image);
    }

    // MÉTODOS DE UTILIDAD

    /**
     * Verifica si la carpeta está vacía.
     *
     * @return true si la carpeta no contiene imágenes, false de lo contrario.
     */
    public boolean isEmpty() {
        return images.isEmpty();
    }

    /**
     * Obtiene el número de imágenes en la carpeta.
     *
     * @return Número de imágenes en la carpeta.
     */
    public int getImageCount() {
        return images.size();
    }
}

package com.dev.brain2.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Esta clase representa una carpeta que puede contener imágenes
public class Folder implements Serializable {
    // Propiedades básicas de la carpeta
    private String id;          // Identificador único de la carpeta
    private String name;        // Nombre de la carpeta
    private String color;       // Color para mostrar en la interfaz
    private List<Image> images; // Lista de imágenes en la carpeta

    // Constructor: crea una nueva carpeta con un nombre y color
    public Folder(String name, String color) {
        this.name = name;
        this.color = color;
        this.images = new ArrayList<>(); // Inicializamos la lista de imágenes vacía
    }

    // GETTERS Y SETTERS BÁSICOS

    // Obtiene el ID de la carpeta
    public String getId() {
        return id;
    }

    // Establece el ID de la carpeta
    public void setId(String id) {
        this.id = id;
    }

    // Obtiene el nombre de la carpeta
    public String getName() {
        return name;
    }

    // Establece el nombre de la carpeta
    public void setName(String name) {
        this.name = name;
    }

    // Obtiene el color de la carpeta
    public String getColor() {
        return color;
    }

    // Establece el color de la carpeta
    public void setColor(String color) {
        this.color = color;
    }

    // MÉTODOS PARA MANEJAR IMÁGENES

    // Obtiene la lista de imágenes (solo lectura para evitar modificaciones directas)
    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }

    // Establece una nueva lista de imágenes (crea una copia para mayor seguridad)
    public void setImages(List<Image> images) {
        this.images = new ArrayList<>(images);
    }

    // Agrega una nueva imagen a la carpeta
    public void addImage(Image image) {
        images.add(image);
    }

    // Elimina una imagen de la carpeta
    public boolean removeImage(Image image) {
        return images.remove(image);
    }

    // MÉTODOS DE UTILIDAD

    // Verifica si la carpeta está vacía
    public boolean isEmpty() {
        return images.isEmpty();
    }

    // Obtiene el número de imágenes en la carpeta
    public int getImageCount() {
        return images.size();
    }
}
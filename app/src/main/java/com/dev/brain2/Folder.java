package com.dev.brain2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase que representa una carpeta en la aplicación.
 * Implementa Serializable para permitir el paso de objetos Folder entre actividades.
 *
 * Esta clase sigue el principio de encapsulamiento manteniendo sus atributos privados
 * y proporcionando métodos públicos para acceder y modificar su estado.
 *
 * Responsabilidad única: Gestionar la información y el contenido de una carpeta
 */
public class Folder implements Serializable {

    // Atributos privados de la clase
    private String name;           // Nombre de la carpeta
    private String color;          // Color representativo de la carpeta (en formato hexadecimal)
    private final List<Image> images;  // Lista de imágenes contenidas en la carpeta

    /**
     * Constructor de la clase Folder.
     * Inicializa una nueva carpeta con un nombre y color específicos.
     *
     * @param name Nombre de la carpeta
     * @param color Color de la carpeta en formato hexadecimal (ejemplo: "#FF0000" para rojo)
     */
    public Folder(String name, String color) {
        // Validación de parámetros
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la carpeta no puede estar vacío");
        }
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("El color de la carpeta no puede estar vacío");
        }

        this.name = name.trim();
        this.color = color.trim();
        this.images = new ArrayList<>();
    }

    /**
     * Obtiene el nombre de la carpeta.
     *
     * @return String con el nombre de la carpeta
     */
    public String getName() {
        return name;
    }

    /**
     * Establece un nuevo nombre para la carpeta.
     *
     * @param name Nuevo nombre para la carpeta
     * @throws IllegalArgumentException si el nombre es null o está vacío
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la carpeta no puede estar vacío");
        }
        this.name = name.trim();
    }

    /**
     * Obtiene el color de la carpeta.
     *
     * @return String con el código de color en formato hexadecimal
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece un nuevo color para la carpeta.
     *
     * @param color Nuevo color en formato hexadecimal
     * @throws IllegalArgumentException si el color es null o está vacío
     */
    public void setColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("El color no puede estar vacío");
        }
        this.color = color.trim();
    }

    /**
     * Obtiene una lista inmutable de las imágenes en la carpeta.
     * Esto previene la modificación directa de la lista de imágenes desde fuera de la clase.
     *
     * @return List<Image> Lista inmutable de imágenes
     */
    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }

    /**
     * Añade una nueva imagen a la carpeta.
     *
     * @param image Imagen a añadir
     * @throws IllegalArgumentException si la imagen es null
     */
    public void addImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("La imagen no puede ser null");
        }
        images.add(image);
    }

    /**
     * Elimina una imagen de la carpeta.
     *
     * @param image Imagen a eliminar
     * @return boolean true si la imagen fue eliminada, false si no se encontró
     */
    public boolean removeImage(Image image) {
        return images.remove(image);
    }

    /**
     * Obtiene el número total de imágenes en la carpeta.
     *
     * @return int cantidad de imágenes en la carpeta
     */
    public int getImageCount() {
        return images.size();
    }

    /**
     * Verifica si la carpeta está vacía.
     *
     * @return boolean true si la carpeta no tiene imágenes, false en caso contrario
     */
    public boolean isEmpty() {
        return images.isEmpty();
    }

    @Override
    public String toString() {
        return "Folder{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", imageCount=" + getImageCount() +
                '}';
    }
}
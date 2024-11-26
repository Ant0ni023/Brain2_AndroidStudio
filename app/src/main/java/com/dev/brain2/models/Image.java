package com.dev.brain2.models;

import android.net.Uri;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Esta clase representa una imagen en la aplicación.
 */
public class Image implements Serializable {
    // Propiedades básicas de la imagen
    private String id;              // Identificador único de la imagen
    private transient Uri uri;      // URI que indica dónde está guardada la imagen (transient porque Uri no es serializable)
    private String name;            // Nombre de la imagen
    private String uriString;       // Guardamos la URI como String para poder serializar

    // Nuevo atributo para las etiquetas de la imagen
    private List<String> tags;      // Lista de etiquetas asociadas a la imagen

    /**
     * Constructor: crea una nueva imagen con una URI y un nombre.
     *
     * @param uri  URI de la imagen.
     * @param name Nombre de la imagen.
     * @throws IllegalArgumentException Si la URI o el nombre son inválidos.
     */
    public Image(Uri uri, String name) {
        // Verificamos que la URI no sea nula
        if (uri == null) {
            throw new IllegalArgumentException("La URI no puede ser nula");
        }
        // Verificamos que el nombre no esté vacío
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        // Inicializamos las propiedades
        this.id = UUID.randomUUID().toString();  // Generamos un ID único
        this.uri = uri;
        this.name = name;
        this.uriString = uri.toString();         // Guardamos la URI como String
        this.tags = new ArrayList<>();           // Inicializamos la lista de etiquetas
    }

    // GETTERS Y SETTERS

    /**
     * Obtiene el ID de la imagen.
     *
     * @return El ID de la imagen.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene la URI de la imagen (con verificaciones de seguridad).
     *
     * @return La URI de la imagen.
     * @throws IllegalStateException Si la URI no está disponible.
     */
    public Uri getUri() {
        // Si la URI es nula pero tenemos el String, la reconstruimos
        if (uri == null && uriString != null) {
            uri = Uri.parse(uriString);
        }
        // Verificamos que la URI esté disponible
        if (uri == null) {
            throw new IllegalStateException("La URI no está disponible");
        }
        return uri;
    }

    /**
     * Establece una nueva URI para la imagen.
     *
     * @param uri La nueva URI de la imagen.
     * @throws IllegalArgumentException Si la URI es nula.
     */
    public void setUri(Uri uri) {
        // Verificamos que la nueva URI no sea nula
        if (uri == null) {
            throw new IllegalArgumentException("La URI no puede ser nula");
        }
        this.uri = uri;
        this.uriString = uri.toString();  // Actualizamos también el String
    }

    /**
     * Obtiene el nombre de la imagen.
     *
     * @return El nombre de la imagen.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece un nuevo nombre para la imagen.
     *
     * @param name El nuevo nombre de la imagen.
     * @throws IllegalArgumentException Si el nombre es nulo o vacío.
     */
    public void setName(String name) {
        // Verificamos que el nuevo nombre no esté vacío
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.name = name;
    }

    /**
     * Obtiene las etiquetas asociadas a la imagen.
     *
     * @return Lista de etiquetas de la imagen.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Añade una etiqueta a la imagen.
     *
     * @param tag La etiqueta a añadir.
     */
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag);
        }
    }

    /**
     * Este método se llama automáticamente al deserializar la imagen.
     *
     * @return La instancia de la imagen reconstruida.
     */
    private Object readResolve() {
        // Reconstruimos la URI desde el String guardado
        if (uriString != null) {
            uri = Uri.parse(uriString);
        }
        return this;
    }
}

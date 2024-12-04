package com.dev.brain2.models;

import android.net.Uri;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Esta clase representa una imagen en la aplicación.
 */
public class Image implements Serializable {

    private String id;              // Identificador único de la imagen
    private transient Uri uri;      // URI que indica dónde está guardada la imagen
    private String name;            // Nombre de la imagen
    private String uriString;       // URI como String para serialización
    private List<String> tags;      // Lista de etiquetas asociadas a la imagen

    /**
     * Constructor: crea una nueva imagen con una URI y un nombre.
     *
     * @param uri  URI de la imagen.
     * @param name Nombre de la imagen.
     * @throws IllegalArgumentException Si la URI o el nombre son inválidos.
     */
    public Image(Uri uri, String name) {
        validateUri(uri);
        validateName(name);
        this.id = UUID.randomUUID().toString();  // Generamos un ID único
        this.uri = uri;
        this.uriString = uri.toString();         // Guardamos la URI como String
        this.name = name;
        this.tags = new ArrayList<>();
    }

    /**
     * Valida que la URI no sea nula.
     *
     * @param uri URI a validar.
     * @throws IllegalArgumentException Si la URI es nula.
     */
    private void validateUri(Uri uri) {
        if (uri == null) {
            throw new IllegalArgumentException("La URI no puede ser nula");
        }
    }

    /**
     * Valida que el nombre no sea nulo o vacío.
     *
     * @param name Nombre a validar.
     * @throws IllegalArgumentException Si el nombre es nulo o vacío.
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
    }

    /**
     * Obtiene el ID de la imagen.
     *
     * @return El ID de la imagen.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene la URI de la imagen.
     *
     * @return La URI de la imagen.
     */
    public Uri getUri() {
        if (uri == null && uriString != null) {
            reconstructUri();
        }
        return uri;
    }

    /**
     * Reconstruye la URI a partir del String guardado.
     */
    private void reconstructUri() {
        uri = Uri.parse(uriString);
    }

    /**
     * Establece una nueva URI para la imagen.
     *
     * @param uri La nueva URI de la imagen.
     * @throws IllegalArgumentException Si la URI es nula.
     */
    public void setUri(Uri uri) {
        validateUri(uri);
        this.uri = uri;
        this.uriString = uri.toString();
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
        validateName(name);
        this.name = name;
    }

    /**
     * Obtiene la lista de etiquetas asociadas a la imagen.
     *
     * @return Lista de etiquetas de la imagen.
     */
    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    /**
     * Añade una etiqueta a la imagen.
     *
     * @param tag La etiqueta a añadir.
     * @throws IllegalArgumentException Si la etiqueta es nula o vacía.
     */
    public void addTag(String tag) {
        validateTag(tag);
        tags.add(tag);
    }

    /**
     * Valida que la etiqueta no sea nula o vacía.
     *
     * @param tag Etiqueta a validar.
     * @throws IllegalArgumentException Si la etiqueta es nula o vacía.
     */
    private void validateTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("La etiqueta no puede estar vacía");
        }
    }

    /**
     * Este método se llama automáticamente al deserializar la imagen.
     *
     * @return La instancia de la imagen reconstruida.
     */
    private Object readResolve() {
        if (uri == null && uriString != null) {
            reconstructUri();
        }
        return this;
    }
}

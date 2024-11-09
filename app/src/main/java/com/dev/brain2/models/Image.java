package com.dev.brain2.models;

import android.net.Uri;
import java.io.Serializable;
import java.util.UUID;

/**
 * Clase que representa una imagen en la aplicación.
 * Implementa Serializable para permitir su paso entre componentes de Android.
 *
 * Responsabilidad única: Representar y mantener los datos de una imagen.
 */
public class Image implements Serializable {

    private String id;    // Identificador único de la imagen
    private transient Uri uri;      // URI que indica la ubicación de la imagen (transient para evitar la serialización)
    private String name;  // Nombre descriptivo de la imagen

    // Campo adicional para la serialización del URI
    private String uriString;

    /**
     * Constructor que crea una nueva imagen con URI y nombre específicos.
     *
     * @param uri  URI que indica la ubicación de la imagen
     * @param name Nombre descriptivo de la imagen
     */
    public Image(Uri uri, String name) {
        this.id = UUID.randomUUID().toString();
        this.uri = uri;
        this.name = name;
        this.uriString = uri.toString();
    }

    /**
     * Obtiene el ID de la imagen.
     *
     * @return ID de la imagen
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene la URI de la imagen.
     *
     * @return URI de la imagen
     */
    public Uri getUri() {
        if (uri == null && uriString != null) {
            uri = Uri.parse(uriString);
        }
        return uri;
    }

    /**
     * Establece una nueva URI para la imagen.
     *
     * @param uri Nueva URI de la imagen
     */
    public void setUri(Uri uri) {
        this.uri = uri;
        this.uriString = uri.toString();
    }

    /**
     * Obtiene el nombre de la imagen.
     *
     * @return Nombre de la imagen
     */
    public String getName() {
        return name;
    }

    /**
     * Establece un nuevo nombre para la imagen.
     *
     * @param name Nuevo nombre de la imagen
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método especial que se llama después de la deserialización.
     * Reconstruye el objeto Uri a partir de uriString.
     *
     * @return este objeto Image con el Uri reconstruido
     */
    private Object readResolve() {
        if (uriString != null) {
            uri = Uri.parse(uriString);
        }
        return this;
    }
}

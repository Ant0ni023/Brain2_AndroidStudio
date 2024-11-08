package com.dev.brain2;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase que representa una imagen en la aplicación.
 * Esta clase es una entidad básica que almacena la información de una imagen,
 * incluyendo su URI (ubicación) y nombre.
 * Implementa Serializable para permitir su paso entre componentes de Android.
 *
 * Responsabilidad única: Representar y mantener los datos de una imagen.
 */
public class Image implements Serializable {

    // Número de versión para la serialización
    private static final long serialVersionUID = 1L;

    // Atributos privados
    private String uri;    // URI que indica la ubicación de la imagen
    private String name;   // Nombre descriptivo de la imagen

    /**
     * Constructor que crea una nueva imagen con URI y nombre específicos.
     *
     * @param uri  URI que indica la ubicación de la imagen
     * @param name Nombre descriptivo de la imagen
     * @throws IllegalArgumentException si uri o name son null o están vacíos
     */
    public Image(String uri, String name) {
        validateParameters(uri, name);
        this.uri = uri.trim();
        this.name = name.trim();
    }

    /**
     * Obtiene la URI de la imagen.
     *
     * @return String que representa la URI de la imagen
     */
    public String getUri() {
        return uri;
    }

    /**
     * Establece una nueva URI para la imagen.
     *
     * @param uri Nueva URI para la imagen
     * @throws IllegalArgumentException si uri es null o está vacía
     */
    public void setUri(String uri) {
        validateUri(uri);
        this.uri = uri.trim();
    }

    /**
     * Obtiene el nombre de la imagen.
     *
     * @return String que representa el nombre de la imagen
     */
    public String getName() {
        return name;
    }

    /**
     * Establece un nuevo nombre para la imagen.
     *
     * @param name Nuevo nombre para la imagen
     * @throws IllegalArgumentException si name es null o está vacío
     */
    public void setName(String name) {
        validateName(name);
        this.name = name.trim();
    }

    /**
     * Valida los parámetros del constructor.
     *
     * @param uri  URI a validar
     * @param name Nombre a validar
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    private void validateParameters(String uri, String name) {
        validateUri(uri);
        validateName(name);
    }

    /**
     * Valida la URI de la imagen.
     *
     * @param uri URI a validar
     * @throws IllegalArgumentException si la URI es inválida
     */
    private void validateUri(String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            throw new IllegalArgumentException("La URI de la imagen no puede ser null o estar vacía");
        }
    }

    /**
     * Valida el nombre de la imagen.
     *
     * @param name Nombre a validar
     * @throws IllegalArgumentException si el nombre es inválido
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la imagen no puede ser null o estar vacío");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(uri, image.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public String toString() {
        return "Image{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }

    /**
     * Crea una copia de la imagen actual.
     *
     * @return Una nueva instancia de Image con los mismos valores
     */
    public Image copy() {
        return new Image(this.uri, this.name);
    }

    /**
     * Verifica si la imagen es válida (tiene URI y nombre válidos).
     *
     * @return true si la imagen es válida, false en caso contrario
     */
    public boolean isValid() {
        try {
            validateParameters(uri, name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
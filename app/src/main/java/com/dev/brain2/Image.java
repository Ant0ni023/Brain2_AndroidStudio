package com.dev.brain2;

import java.io.Serializable;

public class Image implements Serializable {  // Implementa Serializable

    private String uri;
    private String name; // Nueva propiedad para almacenar el nombre de la imagen

    public Image(String uri) {
        this.uri = uri;
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

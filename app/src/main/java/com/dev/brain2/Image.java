package com.dev.brain2;

import java.io.Serializable;

public class Image implements Serializable {  // Implementa Serializable

    private String uri;

    public Image(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

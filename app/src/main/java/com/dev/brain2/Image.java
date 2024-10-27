package com.dev.brain2;

import java.io.Serializable;

public class Image implements Serializable {

    private String uri;
    private String name;

    public Image(String uri, String name) {
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

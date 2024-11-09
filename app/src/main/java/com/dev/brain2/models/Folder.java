package com.dev.brain2.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Folder implements Serializable {

    private String id;             // Identificador único de la carpeta
    private String name;           // Nombre de la carpeta
    private String color;          // Color representativo de la carpeta
    private List<Image> images;    // Lista de imágenes contenidas en la carpeta


    public Folder(String name, String color) {
        this.name = name;
        this.color = color;
        this.images = new ArrayList<>();
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getColor() {
        return color;
    }


    public void setColor(String color) {
        this.color = color;
    }


    public void addImage(Image image) {
        images.add(image);
    }


    public boolean removeImage(Image image) {
        return images.remove(image);
    }


    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }


    public void setImages(List<Image> images) {
        this.images = new ArrayList<>(images);
    }


    public boolean isEmpty() {
        return images.isEmpty();
    }


    public int getImageCount() {
        return images.size();
    }
}

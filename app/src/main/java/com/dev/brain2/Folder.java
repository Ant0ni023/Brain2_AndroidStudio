package com.dev.brain2;

import java.util.ArrayList;
import java.util.List;

public class Folder {

    private String name;
    private String color;
    private List<Image> images;

    public Folder(String name, String color) {
        this.name = name;
        this.color = color;
        this.images = new ArrayList<>();
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

    public List<Image> getImages() {
        return images;
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public int getImageCount() {
        return images.size();
    }

    public boolean removeImage(Image image) {
        return images.remove(image);
    }
}

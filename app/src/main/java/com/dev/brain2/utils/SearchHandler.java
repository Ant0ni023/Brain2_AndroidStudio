package com.dev.brain2.utils;

import com.dev.brain2.models.Image;
import java.util.ArrayList;
import java.util.List;

public class SearchHandler {

    private List<Image> allImages;

    public SearchHandler(List<Image> allImages) {
        this.allImages = allImages;
    }

    public List<Image> performSearch(String query) {
        List<Image> filteredImages = new ArrayList<>();
        for (Image image : allImages) {
            if (image.getName().contains(query) || image.getTags().contains(query)) {
                filteredImages.add(image);
            }
        }
        return filteredImages;
    }
}

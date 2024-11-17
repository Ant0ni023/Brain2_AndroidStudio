package com.dev.brain2.utils;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dev.brain2.adapters.ImageAdapter;
import com.dev.brain2.interfaces.OnImageClickListener;
import com.dev.brain2.models.Image;
import java.util.List;

public class RecyclerViewHandler {

    private final Context context;
    private final RecyclerView recyclerView;
    private final List<Image> images;
    private final OnImageClickListener listener;
    private ImageAdapter imageAdapter;

    public RecyclerViewHandler(Context context, RecyclerView recyclerView, List<Image> images, OnImageClickListener listener) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.images = images;
        this.listener = listener;
    }

    public void setupRecyclerView() {
        // Configura el layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Crea y configura el adaptador
        imageAdapter = new ImageAdapter(context, images, listener);
        recyclerView.setAdapter(imageAdapter);
    }

    public void updateUIWithResults(List<Image> filteredImages) {
        // Actualiza el adaptador con las imágenes filtradas y refresca la vista
        imageAdapter.updateImages(filteredImages);
    }
}

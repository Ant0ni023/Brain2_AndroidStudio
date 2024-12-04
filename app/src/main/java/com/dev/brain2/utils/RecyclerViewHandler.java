package com.dev.brain2.utils;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.brain2.adapters.ImageAdapter;
import com.dev.brain2.interfaces.OnImageClickListener;
import com.dev.brain2.models.Image;

import java.util.List;

/**
 * Clase que maneja la configuración y actualización de un RecyclerView para imágenes.
 */
public class RecyclerViewHandler {

    private final Context appContext;
    private final RecyclerView recyclerView;
    private final OnImageClickListener listener;
    private ImageAdapter imageAdapter;

    /**
     * Constructor.
     *
     * @param context      Contexto de la aplicación.
     * @param recyclerView RecyclerView a manejar.
     * @param listener     Listener para eventos de clic en imágenes.
     */
    public RecyclerViewHandler(Context context, RecyclerView recyclerView, OnImageClickListener listener) {
        this.appContext = context;
        this.recyclerView = recyclerView;
        this.listener = listener;
    }

    /**
     * Configura el RecyclerView con el adaptador y layout manager.
     *
     * @param images Lista de imágenes a mostrar.
     */
    public void setupRecyclerView(List<Image> images) {
        setupLayoutManager();
        setupAdapter(images);
    }

    /**
     * Configura el LayoutManager para el RecyclerView.
     */
    private void setupLayoutManager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(appContext));
    }

    /**
     * Configura el adaptador para el RecyclerView.
     *
     * @param images Lista de imágenes a mostrar.
     */
    private void setupAdapter(List<Image> images) {
        imageAdapter = new ImageAdapter(appContext, images, listener);
        recyclerView.setAdapter(imageAdapter);
    }

    /**
     * Actualiza la interfaz de usuario con los resultados filtrados.
     *
     * @param filteredImages Lista de imágenes filtradas.
     */
    public void updateUIWithResults(List<Image> filteredImages) {
        imageAdapter.updateImages(filteredImages);
    }
}

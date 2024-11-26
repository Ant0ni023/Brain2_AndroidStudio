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

    private final Context context;
    private final RecyclerView recyclerView;
    private final List<Image> images;
    private final OnImageClickListener listener;
    private ImageAdapter imageAdapter;

    /**
     * Constructor.
     *
     * @param context      Contexto de la aplicación.
     * @param recyclerView RecyclerView a manejar.
     * @param images       Lista de imágenes a mostrar.
     * @param listener     Listener para eventos de clic en imágenes.
     */
    public RecyclerViewHandler(Context context, RecyclerView recyclerView, List<Image> images, OnImageClickListener listener) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.images = images;
        this.listener = listener;
    }

    /**
     * Configura el RecyclerView con el adaptador y layout manager.
     */
    public void setupRecyclerView() {
        // Configura el layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Crea y configura el adaptador
        imageAdapter = new ImageAdapter(context, images, listener);
        recyclerView.setAdapter(imageAdapter);
    }

    /**
     * Actualiza la interfaz de usuario con los resultados filtrados.
     *
     * @param filteredImages Lista de imágenes filtradas.
     */
    public void updateUIWithResults(List<Image> filteredImages) {
        // Actualiza el adaptador con las imágenes filtradas y refresca la vista
        imageAdapter.updateImages(filteredImages);
    }
}

package com.dev.brain2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Actividad que muestra el contenido de una carpeta específica.
 * Esta actividad se encarga de mostrar las imágenes contenidas en una carpeta
 * y gestionar las interacciones del usuario con estas imágenes.
 *
 * Responsabilidad única: Mostrar y gestionar el contenido de una carpeta específica.
 */
public class FolderContentActivity extends AppCompatActivity implements ImageAdapter.ImageActionListener {

    // Constantes
    private static final int GRID_COLUMNS = 3;
    private static final String EXTRA_FOLDER = "folder";

    // Componentes de la UI
    private TextView folderTitleView;
    private RecyclerView imagesRecyclerView;

    // Gestores y adaptador
    private ImageAdapter imageAdapter;
    private FolderManager folderManager;
    private ImageManager imageManager;

    // Datos
    private Folder currentFolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_content);

        initializeManagers();
        initializeViews();
        loadFolderFromIntent();
        setupRecyclerView();
        displayFolderContent();
    }

    /**
     * Inicializa los gestores necesarios para la actividad.
     */
    private void initializeManagers() {
        folderManager = new FolderManager(this);
        imageManager = new ImageManager(this, folderManager);
    }

    /**
     * Inicializa las vistas de la actividad.
     */
    private void initializeViews() {
        folderTitleView = findViewById(R.id.folderTitle);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
    }

    /**
     * Carga la carpeta desde el Intent que inició la actividad.
     * Si no se puede cargar la carpeta, finaliza la actividad.
     */
    private void loadFolderFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_FOLDER)) {
            currentFolder = (Folder) intent.getSerializableExtra(EXTRA_FOLDER);
            if (currentFolder == null) {
                handleFolderLoadError();
                return;
            }
            updateFolderTitle();
        } else {
            handleFolderLoadError();
        }
    }

    /**
     * Maneja el error cuando no se puede cargar la carpeta.
     */
    private void handleFolderLoadError() {
        showToast("No se pudo cargar la carpeta");
        finish();
    }

    /**
     * Actualiza el título de la carpeta en la UI.
     */
    private void updateFolderTitle() {
        if (currentFolder != null) {
            folderTitleView.setText(currentFolder.getName());
        }
    }

    /**
     * Configura el RecyclerView con sus propiedades y adaptador.
     */
    private void setupRecyclerView() {
        if (currentFolder == null) return;

        // Configura el layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_COLUMNS);
        imagesRecyclerView.setLayoutManager(layoutManager);

        // Configura el adaptador
        List<Image> images = currentFolder.getImages();
        imageAdapter = new ImageAdapter(this, images, imageManager, currentFolder, this);
        imagesRecyclerView.setAdapter(imageAdapter);

        // Añade decoración a la cuadrícula si es necesario
        addGridDecoration();
    }

    /**
     * Añade decoración al grid de imágenes para mejorar su apariencia.
     */
    private void addGridDecoration() {
        // Aquí podrías añadir un ItemDecoration personalizado
        // Por ejemplo, para agregar espaciado entre items
    }

    /**
     * Muestra el contenido de la carpeta en la UI.
     */
    private void displayFolderContent() {
        if (currentFolder == null) {
            finish();
            return;
        }

        // Si la carpeta está vacía, será eliminada y la actividad se cerrará
        if (currentFolder.isEmpty()) {
            folderManager.deleteFolderIfEmpty(currentFolder);
            finish();
            return;
        }

        imagesRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Actualiza el contenido de la carpeta.
     */
    private void refreshContent() {
        if (currentFolder != null && imageAdapter != null) {
            imageAdapter.notifyDataSetChanged();
            displayFolderContent();
            updateFolderInManager();
        }
    }

    /**
     * Actualiza la carpeta en el FolderManager.
     */
    private void updateFolderInManager() {
        if (currentFolder != null) {
            folderManager.updateFolder(currentFolder);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    // Implementación de ImageAdapter.ImageActionListener
    @Override
    public void onImageClick(Image image) {
        // Manejar el click en la imagen si es necesario
    }

    @Override
    public void onImageDeleted(Image image) {
        refreshContent();
    }

    @Override
    public void onImageMoved(Image image) {
        refreshContent();
    }

    @Override
    public void onImageRenamed(Image image) {
        refreshContent();
    }

    /**
     * Muestra un mensaje Toast.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
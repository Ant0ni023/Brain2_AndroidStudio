package com.dev.brain2.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.dev.brain2.R;
import com.dev.brain2.adapters.ImageAdapter;
import com.dev.brain2.interfaces.OnImageClickListener;
import com.dev.brain2.interfaces.Searchable;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.models.Image;
import com.dev.brain2.utils.RecyclerViewHandler;
import com.dev.brain2.utils.SearchHandler;
import java.util.List;

public class ImageGalleryActivity extends AppCompatActivity implements Searchable, OnImageClickListener {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;

    private SearchHandler searchHandler;
    private RecyclerViewHandler recyclerViewHandler;
    private ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        // Inicializa componentes
        initializeComponents();

        // Configura RecyclerView y su adaptador a través de RecyclerViewHandler
        recyclerViewHandler.setupRecyclerView();

        // Configura el botón de búsqueda
        setupSearchButton();
    }

    private void initializeComponents() {
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.imageRecyclerView);

        imageManager = new ImageManager(this, new FolderManager(this));
        List<Image> allImages = imageManager.getAllImages();

        // Inicializa los manejadores de búsqueda y RecyclerView
        searchHandler = new SearchHandler(allImages);
        recyclerViewHandler = new RecyclerViewHandler(this, recyclerView, allImages, this);
    }

    private void setupSearchButton() {
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            onSearch(query);
        });
    }

    @Override
    public void onSearch(String query) {
        List<Image> filteredImages = searchHandler.performSearch(query);
        recyclerViewHandler.updateUIWithResults(filteredImages);
    }

    @Override
    public void onImageClick(Image image) {
        // Maneja el clic en la imagen
    }

    @Override
    public void onImageLongClick(Image image, int position) {
        // Maneja el clic largo en la imagen
    }
}

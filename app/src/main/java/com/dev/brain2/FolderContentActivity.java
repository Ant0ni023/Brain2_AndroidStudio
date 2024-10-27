package com.dev.brain2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FolderContentActivity extends AppCompatActivity {

    private Folder folder;
    private RecyclerView imagesRecyclerView;
    private ImageAdapter imageAdapter;
    private FolderManager folderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_content);

        // Inicializa FolderManager
        folderManager = new FolderManager(this);

        // Obtiene la carpeta seleccionada desde el Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("folder")) {
            folder = (Folder) intent.getSerializableExtra("folder");
        }

        if (folder == null) {
            Toast.makeText(this, "Carpeta no encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configura el título de la carpeta
        TextView folderTitle = findViewById(R.id.folderTitle);
        folderTitle.setText(folder.getName());

        // Configura el RecyclerView para mostrar las imágenes
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columnas de imágenes

        List<Image> images = folder.getImages();
        imageAdapter = new ImageAdapter(this, images, folderManager, folder);
        imagesRecyclerView.setAdapter(imageAdapter);
    }
}

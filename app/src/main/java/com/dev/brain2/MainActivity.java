package com.dev.brain2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements FolderAdapter.OnFolderClickListener {
    private FolderManager folderManager;
    private FolderAdapter folderAdapter;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private static final int REQUEST_IMAGE_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        folderManager = new FolderManager(this);

        setupRecyclerView();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Ya estamos en inicio
                return true;
            } else if (itemId == R.id.nav_search) {
                // Acción de buscar
                Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_add_photo) {
                // Iniciar ImagePickerActivity
                Intent intent = new Intent(this, ImagePickerActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_settings) {
                // Acción de configuración
                Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.foldersRecyclerView);
        folderAdapter = new FolderAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(folderAdapter);
    }

    private void loadFolders() {
        if (folderManager != null) {
            folderAdapter.updateFolders(folderManager.getFolders());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFolders();
    }

    @Override
    public void onFolderClick(Folder folder) {
        Toast.makeText(this, "Carpeta seleccionada: " + folder.getName(), Toast.LENGTH_SHORT).show();
    }
}

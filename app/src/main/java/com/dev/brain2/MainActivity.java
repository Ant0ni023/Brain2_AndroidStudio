package com.dev.brain2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.graphics.Insets;

public class MainActivity extends AppCompatActivity implements FolderAdapter.OnFolderClickListener {

    private FolderManager folderManager;
    private FolderAdapter folderAdapter;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

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
                return true;
            } else if (itemId == R.id.nav_search) {
                Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_add_photo) {
                Intent intent = new Intent(this, ImagePickerActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_settings) {
                Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Uso de WindowInsets para ajustar el padding en Android API 30 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            findViewById(R.id.main).setOnApplyWindowInsetsListener((v, insets) -> {
                Insets systemBarsInsets = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
                return WindowInsets.CONSUMED;
            });
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.foldersRecyclerView);
        folderAdapter = new FolderAdapter(this, this); // Asegúrate de pasar el contexto y el listener
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    @Override
    public void onFolderEdit(Folder folder, int position) {
        // Implementa la lógica para editar la carpeta aquí
        folderManager.updateFolder(folder); // Asumiendo que tienes este método en FolderManager
        folderAdapter.notifyItemChanged(position); // Actualizar el adaptador
        Toast.makeText(this, "Carpeta actualizada: " + folder.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFolderDelete(Folder folder) {
        folderManager.deleteFolder(folder); // Asumiendo que tienes este método en FolderManager
        loadFolders(); // Recargar la lista de carpetas
        Toast.makeText(this, "Carpeta eliminada: " + folder.getName(), Toast.LENGTH_SHORT).show();
    }
}

package com.dev.brain2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Actividad principal que muestra la lista de carpetas y gestiona la navegación principal.
 *
 * Responsabilidad única: Gestionar la vista principal y la navegación entre funcionalidades.
 */
public class MainActivity extends AppCompatActivity implements FolderAdapter.OnFolderClickListener {

    private static final int REQUEST_CODE_MEDIA_PERMISSION = 101;

    // Vistas
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

    // Gestores
    private FolderManager folderManager;
    private FolderAdapter folderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeManagers();
        initializeViews();
        setupRecyclerView();
        setupBottomNavigationView();
        checkPermissions();
    }

    private void initializeManagers() {
        folderManager = new FolderManager(this);
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.foldersRecyclerView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        folderAdapter = new FolderAdapter(this, this);
        recyclerView.setAdapter(folderAdapter);
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                showToast("Búsqueda próximamente disponible");
                return true;
            } else if (itemId == R.id.nav_add_photo) {
                startActivity(new Intent(this, ImagePickerActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                showToast("Configuración próximamente disponible");
                return true;
            }
            return false;
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestMediaPermissions();
        } else {
            requestLegacyStoragePermission();
        }
    }

    private void requestLegacyStoragePermission() {
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissions(permissions, REQUEST_CODE_MEDIA_PERMISSION);
    }

    private void requestMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_VIDEO,
                    android.Manifest.permission.READ_MEDIA_AUDIO
            };
            requestPermissions(permissions, REQUEST_CODE_MEDIA_PERMISSION);
        }
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
        Intent intent = new Intent(this, FolderContentActivity.class);
        intent.putExtra("folder", folder);
        startActivity(intent);
    }

    @Override
    public void onFolderEdit(Folder folder, int position) {
        folderManager.updateFolder(folder);
        folderAdapter.notifyItemChanged(position);
        showToast("Carpeta actualizada: " + folder.getName());
    }

    @Override
    public void onFolderDelete(Folder folder) {
        folderManager.deleteFolder(folder);
        loadFolders();
        showToast("Carpeta eliminada: " + folder.getName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_MEDIA_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                showToast("Se requieren permisos para acceder a los archivos multimedia");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
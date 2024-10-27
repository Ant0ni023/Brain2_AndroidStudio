package com.dev.brain2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.graphics.Insets;

public class MainActivity extends AppCompatActivity implements FolderAdapter.OnFolderClickListener {

    private static final int REQUEST_CODE_MEDIA_PERMISSION = 101;

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
        setupBottomNavigationView();

        // Solicita permisos de almacenamiento dependiendo de la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestMediaPermissions();
        } else {
            requestLegacyStoragePermission();
        }

        // Ajuste de padding para Android API 30 o superior
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
        folderAdapter = new FolderAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(folderAdapter);
    }

    private void setupBottomNavigationView() {
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
    }

    private void requestLegacyStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_MEDIA_PERMISSION);
        }
    }

    private void requestMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO,
                                Manifest.permission.READ_MEDIA_AUDIO
                        },
                        REQUEST_CODE_MEDIA_PERMISSION);
            }
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
        Toast.makeText(this, "Carpeta actualizada: " + folder.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFolderDelete(Folder folder) {
        folderManager.deleteFolder(folder); // Llama a deleteFolder en FolderManager
        loadFolders(); // Recarga las carpetas en la interfaz después de eliminar
        Toast.makeText(this, "Carpeta eliminada: " + folder.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                Toast.makeText(this, "Se necesitan permisos para acceder a los archivos multimedia", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

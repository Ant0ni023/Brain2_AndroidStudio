package com.dev.brain2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.brain2.activities.FolderContentActivity;
import com.dev.brain2.activities.ImagePickerActivity;
import com.dev.brain2.adapters.FolderAdapter;
import com.dev.brain2.interfaces.OnFolderClickListener;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.models.Folder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnFolderClickListener {

    private static final int REQUEST_CODE_MEDIA_PERMISSION = 101;

    // Vistas
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

    // Gestores
    private FolderManager folderManager;
    private FolderAdapter folderAdapter;
    private List<Folder> folderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asegúrate de tener este layout

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
        folderList = folderManager.getFolders();
        folderAdapter = new FolderAdapter(this, folderList, this);
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
        // Solicitamos los permisos necesarios directamente si no están concedidos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestMediaPermissions();
        } else {
            requestLegacyStoragePermission();
        }
    }

    private void requestLegacyStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_MEDIA_PERMISSION);
        }
    }

    private void requestMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
            };
            requestPermissions(permissions, REQUEST_CODE_MEDIA_PERMISSION);
        }
    }

    private void loadFolders() {
        folderList = folderManager.getFolders();
        folderAdapter.updateFolders(folderList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFolders();
    }

    @Override
    public void onFolderClick(Folder folder) {
        Intent intent = new Intent(this, FolderContentActivity.class);
        intent.putExtra("folderId", folder.getId()); // Pasamos el ID de la carpeta
        startActivity(intent);
    }

    @Override
    public void onFolderLongClick(Folder folder, int position) {
        showFolderOptionsDialog(folder, position);
    }

    private void showFolderOptionsDialog(Folder folder, int position) {
        String[] options = {"Modificar carpeta", "Eliminar carpeta"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones de carpeta")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditFolderDialog(folder, position);
                    } else if (which == 1) {
                        showDeleteFolderConfirmation(folder);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showEditFolderDialog(Folder folder, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_folder, null);
        EditText folderNameInput = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

        // Configura el spinner de colores
        String[] COLOR_NAMES = {"Azul", "Rojo", "Verde", "Amarillo", "Naranja", "Morado"};
        String[] COLOR_VALUES = {"#1E90FF", "#FF0000", "#00FF00", "#FFFF00", "#FFA500", "#800080"};

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                COLOR_NAMES
        );
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);

        // Establece los valores actuales
        folderNameInput.setText(folder.getName());
        int colorPosition = Arrays.asList(COLOR_VALUES).indexOf(folder.getColor());
        if (colorPosition >= 0) {
            colorSpinner.setSelection(colorPosition);
        }

        new AlertDialog.Builder(this)
                .setTitle("Modificar Carpeta")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String newName = folderNameInput.getText().toString().trim();
                    String newColor = COLOR_VALUES[colorSpinner.getSelectedItemPosition()];

                    if (!newName.isEmpty()) {
                        folder.setName(newName);
                        folder.setColor(newColor);
                        folderManager.updateFolder(folder);
                        folderAdapter.notifyItemChanged(position);
                        showToast("Carpeta actualizada: " + folder.getName());
                    } else {
                        showToast("El nombre no puede estar vacío");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDeleteFolderConfirmation(Folder folder) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar carpeta")
                .setMessage("¿Está seguro de que desea eliminar esta carpeta?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    folderManager.deleteFolder(folder);
                    loadFolders();
                    showToast("Carpeta eliminada: " + folder.getName());
                })
                .setNegativeButton("Cancelar", null)
                .show();
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

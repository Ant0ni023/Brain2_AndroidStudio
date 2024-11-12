package com.dev.brain2;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.dev.brain2.activities.FolderContentActivity;
import com.dev.brain2.activities.ImagePickerActivity;
import com.dev.brain2.adapters.FolderAdapter;
import com.dev.brain2.interfaces.OnFolderClickListener;
import com.dev.brain2.managers.DialogManager;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.FolderGridManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.Notifier;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import android.widget.Button;
import com.dev.brain2.activities.ImageGalleryActivity;

public class MainActivity extends AppCompatActivity implements OnFolderClickListener {

    // Componentes de la UI
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

    // Gestores y adaptadores
    private FolderManager folderManager;
    private DialogManager dialogManager;
    private FolderGridManager folderGridManager;
    private FolderAdapter folderAdapter;
    private List<Folder> folderList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos los gestores
        folderManager = new FolderManager(this);
        dialogManager = new DialogManager(this, folderManager, null);

        // Inicializamos las vistas
        recyclerView = findViewById(R.id.foldersRecyclerView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configuramos la interfaz
        setupRecyclerView();
        setupBottomNavigationView();
        loadFolders();
    }

    private void setupRecyclerView() {
        // Configuramos el administrador de cuadrícula personalizado
        folderGridManager = new FolderGridManager(this);
        recyclerView.setLayoutManager(folderGridManager);

        // Configuramos el adaptador
        folderAdapter = new FolderAdapter(this, folderList, this);
        recyclerView.setAdapter(folderAdapter);

        // Agregamos padding para el scroll
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.nav_bar_height);
        recyclerView.setPadding(0, 0, 0, bottomPadding);
        recyclerView.setClipToPadding(false);
    }

    private void loadFolders() {
        folderList = folderManager.getFolders();
        folderAdapter.updateFolders(folderList);
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add_photo) {
                startActivity(new Intent(this, ImagePickerActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                loadFolders();
                return true;
            } else if (itemId == R.id.nav_search) { // Identificador del ícono de lupa
                // Abre la ImageGalleryActivity al hacer clic en el ícono de búsqueda
                Intent intent = new Intent(this, ImageGalleryActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFolders();
    }

    @Override
    public void onFolderClick(Folder folder) {
        // Abrimos el contenido de la carpeta
        Intent intent = new Intent(this, FolderContentActivity.class);
        intent.putExtra("folderId", folder.getId());
        startActivity(intent);
    }

    @Override
    public void onFolderLongClick(Folder folder, int position) {
        showFolderOptionsDialog(folder);
    }

    private void showFolderOptionsDialog(Folder folder) {
        String[] options = {"Modificar carpeta", "Eliminar carpeta"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Opciones de carpeta")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        dialogManager.showFolderEditDialog(folder, updatedFolder -> loadFolders());
                    } else if (which == 1) {
                        Notifier.showDeleteConfirmation(this, "¿Eliminar esta carpeta?", () -> {
                            folderManager.deleteFolder(folder);
                            loadFolders();
                            Notifier.showInfo(this, "Carpeta eliminada: " + folder.getName());
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
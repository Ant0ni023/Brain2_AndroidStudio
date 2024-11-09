package com.dev.brain2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.brain2.R;
import com.dev.brain2.adapters.ImageAdapter;
import com.dev.brain2.interfaces.OnImageClickListener;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.models.Image;

import java.util.List;


public class FolderContentActivity extends AppCompatActivity implements OnImageClickListener {

    private static final String EXTRA_FOLDER_ID = "folderId";

    private TextView folderTitleView;
    private RecyclerView imagesRecyclerView;

    private ImageAdapter imageAdapter;
    private FolderManager folderManager;
    private ImageManager imageManager;

    private Folder currentFolder;
    private List<Image> imageList;

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

    private void initializeManagers() {
        folderManager = new FolderManager(this);
        imageManager = new ImageManager(this, folderManager);
    }

    private void initializeViews() {
        folderTitleView = findViewById(R.id.folderTitle);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
    }

    private void loadFolderFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_FOLDER_ID)) {
            String folderId = intent.getStringExtra(EXTRA_FOLDER_ID);
            currentFolder = folderManager.getFolderById(folderId);
            if (currentFolder == null) {
                handleFolderLoadError();
                return;
            }
            updateFolderTitle();
        } else {
            handleFolderLoadError();
        }
    }

    private void handleFolderLoadError() {
        showToast("No se pudo cargar la carpeta");
        finish();
    }

    private void updateFolderTitle() {
        if (currentFolder != null) {
            folderTitleView.setText(currentFolder.getName());
        }
    }

    private void setupRecyclerView() {
        if (currentFolder == null) return;

        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageList = currentFolder.getImages();
        imageAdapter = new ImageAdapter(this, imageList, this);
        imagesRecyclerView.setAdapter(imageAdapter);
    }

    private void displayFolderContent() {
        if (currentFolder == null) {
            finish();
            return;
        }

        if (currentFolder.isEmpty()) {
            // Mostrar mensaje o vista vacía
            showToast("La carpeta está vacía");
        }

        imagesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void refreshContent() {
        if (currentFolder != null && imageAdapter != null) {
            // Recargar currentFolder desde FolderManager por ID
            currentFolder = folderManager.getFolderById(currentFolder.getId());
            if (currentFolder == null) {
                showToast("La carpeta ya no existe");
                finish();
                return;
            }
            imageList = currentFolder.getImages();
            imageAdapter.updateImages(imageList);
            displayFolderContent();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    // Implementación de OnImageClickListener
    @Override
    public void onImageClick(Image image) {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra("imageUri", image.getUri());
        startActivity(intent);
    }

    @Override
    public void onImageLongClick(Image image, int position) {
        showImageOptionsDialog(image, position);
    }

    private void showImageOptionsDialog(Image image, int position) {
        String[] options = {"Mover a otra carpeta", "Eliminar imagen", "Cambiar nombre de imagen"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Opciones de imagen")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showMoveImageDialog(image);
                            break;
                        case 1:
                            showDeleteConfirmationDialog(image);
                            break;
                        case 2:
                            showRenameImageDialog(image);
                            break;
                    }
                })
                .show();
    }

    private void showMoveImageDialog(Image image) {
        List<Folder> availableFolders = folderManager.getAvailableFolders(currentFolder);

        if (availableFolders.isEmpty()) {
            showToast("No hay otras carpetas disponibles");
            return;
        }

        String[] folderNames = new String[availableFolders.size()];
        for (int i = 0; i < availableFolders.size(); i++) {
            folderNames[i] = availableFolders.get(i).getName();
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Mover imagen a otra carpeta")
                .setItems(folderNames, (dialog, which) -> {
                    Folder targetFolder = availableFolders.get(which);
                    moveImage(image, targetFolder);
                })
                .show();
    }

    private void moveImage(Image image, Folder targetFolder) {
        if (imageManager.moveImage(image, currentFolder, targetFolder)) {
            showToast("Imagen movida a " + targetFolder.getName());
            refreshContent();
        } else {
            showToast("Error al mover la imagen");
        }
    }

    private void showDeleteConfirmationDialog(Image image) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar imagen")
                .setMessage("¿Está seguro de que desea eliminar esta imagen?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteImage(image))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteImage(Image image) {
        if (imageManager.deleteImage(image, currentFolder)) {
            showToast("Imagen eliminada");
            refreshContent();
        } else {
            showToast("Error al eliminar la imagen");
        }
    }

    private void showRenameImageDialog(Image image) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rename_image, null);
        EditText nameInput = dialogView.findViewById(R.id.imageNameInput);
        nameInput.setText(image.getName());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cambiar nombre de imagen")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        if (imageManager.renameImage(image, newName, currentFolder)) {
                            showToast("Nombre cambiado a " + newName);
                            refreshContent();
                        } else {
                            showToast("Error al renombrar la imagen");
                        }
                    } else {
                        showToast("El nombre no puede estar vacío");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

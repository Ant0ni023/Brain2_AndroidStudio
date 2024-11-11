package com.dev.brain2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dev.brain2.R;
import com.dev.brain2.adapters.ImageAdapter;
import com.dev.brain2.interfaces.OnImageClickListener;
import com.dev.brain2.managers.DialogManager;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.models.Image;
import com.dev.brain2.utils.Notifier;
import java.util.List;

// Esta actividad muestra el contenido de una carpeta específica y permite gestionar sus imágenes
public class FolderContentActivity extends AppCompatActivity implements OnImageClickListener {

    // Clave para obtener el ID de la carpeta del Intent
    private static final String EXTRA_FOLDER_ID = "folderId";

    // Componentes de la interfaz de usuario
    private TextView folderTitleView;      // Muestra el nombre de la carpeta
    private RecyclerView imagesRecyclerView; // Lista de imágenes en cuadrícula

    // Gestores y adaptadores
    private ImageAdapter imageAdapter;     // Maneja la visualización de imágenes
    private FolderManager folderManager;   // Gestiona operaciones con carpetas
    private ImageManager imageManager;     // Gestiona operaciones con imágenes
    private DialogManager dialogManager;   // Gestiona los diálogos de la aplicación

    // Datos de la carpeta actual
    private Folder currentFolder;        // La carpeta que se está visualizando
    private List<Image> imageList;       // Lista de imágenes en la carpeta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_content);

        // Inicializamos los gestores necesarios
        folderManager = new FolderManager(this);
        imageManager = new ImageManager(this, folderManager);
        dialogManager = new DialogManager(this, folderManager, imageManager);

        // Obtenemos referencias a las vistas
        folderTitleView = findViewById(R.id.folderTitle);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);

        // Configuramos la actividad
        loadFolderFromIntent();  // Cargamos la carpeta desde el Intent
        setupRecyclerView();     // Configuramos la vista de imágenes
        displayFolderContent();  // Mostramos el contenido
    }

    // Carga la carpeta usando el ID recibido en el Intent
    private void loadFolderFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_FOLDER_ID)) {
            String folderId = intent.getStringExtra(EXTRA_FOLDER_ID);
            currentFolder = folderManager.getFolderById(folderId);
            if (currentFolder == null) {
                showToast("No se pudo cargar la carpeta");
                finish();
                return;
            }
            folderTitleView.setText(currentFolder.getName());
        } else {
            showToast("No se pudo cargar la carpeta");
            finish();
        }
    }

    // Configura el RecyclerView en formato de cuadrícula
    private void setupRecyclerView() {
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageList = currentFolder.getImages();
        imageAdapter = new ImageAdapter(this, imageList, this);
        imagesRecyclerView.setAdapter(imageAdapter);
    }

    // Muestra el contenido de la carpeta
    private void displayFolderContent() {
        if (currentFolder == null) {
            finish();
            return;
        }

        if (currentFolder.getImages().isEmpty()) {
            showToast("La carpeta está vacía");
        }
        imagesRecyclerView.setVisibility(View.VISIBLE);
    }

    // Actualiza el contenido cuando hay cambios
    private void refreshContent() {
        if (currentFolder != null && imageAdapter != null) {
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

    // Se llama cuando la actividad vuelve a primer plano
    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    // Maneja el clic en una imagen
    @Override
    public void onImageClick(Image image) {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra("imageUri", image.getUri());
        startActivity(intent);
    }

    // Maneja el clic largo en una imagen
    @Override
    public void onImageLongClick(Image image, int position) {
        showImageOptionsDialog(image);
    }

    // Muestra el diálogo con opciones para la imagen seleccionada
    private void showImageOptionsDialog(Image image) {
        // Opciones disponibles para la imagen
        String[] options = {"Mover a otra carpeta", "Eliminar imagen", "Cambiar nombre de imagen"};

        // Creamos y mostramos el diálogo
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Opciones de imagen")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Mover imagen
                            dialogManager.showImageMoveDialog(currentFolder, image, this::refreshContent);
                            break;
                        case 1: // Eliminar imagen
                            Notifier.showDeleteConfirmation(this,
                                    "¿Está seguro de que desea eliminar esta imagen?",
                                    () -> {
                                        if (imageManager.deleteImage(image, currentFolder)) {
                                            showToast("Imagen eliminada");
                                            refreshContent();
                                        } else {
                                            showToast("Error al eliminar la imagen");
                                        }
                                    });
                            break;
                        case 2: // Renombrar imagen
                            dialogManager.showImageRenameDialog(currentFolder, image, this::refreshContent);
                            break;
                    }
                })
                .show();
    }

    // Método auxiliar para mostrar mensajes al usuario
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
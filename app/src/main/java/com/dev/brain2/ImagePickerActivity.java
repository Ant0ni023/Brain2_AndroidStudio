package com.dev.brain2;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.LayoutInflater;

/**
 * Actividad que permite al usuario seleccionar o capturar una imagen.
 * Maneja la selección de imágenes desde la galería o la cámara, y su posterior
 * almacenamiento en una carpeta seleccionada.
 */
public class ImagePickerActivity extends AppCompatActivity {

    // Constantes
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String[] PICKER_OPTIONS = {"Tomar foto", "Seleccionar desde galería"};

    // Componentes de UI
    private ImageView previewImageView;
    private Button confirmButton;

    // Gestores y datos
    private ImageManager imageManager;
    private FolderManager folderManager;
    private Uri selectedImageUri;

    // Activity Result Launchers
    private final ActivityResultLauncher<Intent> galleryLauncher;
    private final ActivityResultLauncher<Intent> cameraLauncher;
    private final ActivityResultLauncher<String> permissionLauncher;

    /**
     * Constructor que inicializa los launchers de resultados.
     */
    public ImagePickerActivity() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleGalleryResult(result.getResultCode(), result.getData())
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleCameraResult(result.getResultCode(), result.getData())
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                this::handlePermissionResult
        );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        initializeManagers();
        initializeViews();
        setupListeners();
        showImageSourceDialog();
    }

    /**
     * Inicializa los gestores necesarios.
     */
    private void initializeManagers() {
        folderManager = new FolderManager(this);
        imageManager = new ImageManager(this, folderManager);
    }

    /**
     * Inicializa las vistas de la actividad.
     */
    private void initializeViews() {
        previewImageView = findViewById(R.id.imageView);
        confirmButton = findViewById(R.id.confirmButton);
    }

    /**
     * Configura los listeners de la UI.
     */
    private void setupListeners() {
        confirmButton.setOnClickListener(v -> handleConfirmButton());
    }

    /**
     * Muestra el diálogo para seleccionar el origen de la imagen.
     */
    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar imagen")
                .setItems(PICKER_OPTIONS, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndLaunch();
                    } else {
                        launchGalleryPicker();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /**
     * Verifica el permiso de cámara y lanza la cámara si está permitido.
     */
    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    /**
     * Lanza la aplicación de cámara.
     */
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    /**
     * Lanza el selector de galería.
     */
    private void launchGalleryPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    /**
     * Maneja el resultado de la selección de galería.
     */
    private void handleGalleryResult(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            updateImagePreview();
        } else {
            handleImageSelectionCancelled();
        }
    }

    /**
     * Maneja el resultado de la captura de cámara.
     */
    private void handleCameraResult(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                selectedImageUri = imageManager.saveBitmapAsTemp(photo);
                updateImagePreview();
            }
        } else {
            handleImageSelectionCancelled();
        }
    }

    /**
     * Maneja el resultado del permiso de cámara.
     */
    private void handlePermissionResult(boolean isGranted) {
        if (isGranted) {
            launchCamera();
        } else {
            showToast("Permiso de cámara denegado");
            showImageSourceDialog();
        }
    }

    /**
     * Actualiza la vista previa de la imagen.
     */
    private void updateImagePreview() {
        if (selectedImageUri != null) {
            previewImageView.setImageURI(selectedImageUri);
            confirmButton.setEnabled(true);
        }
    }

    /**
     * Maneja cuando se cancela la selección de imagen.
     */
    private void handleImageSelectionCancelled() {
        showToast("Selección de imagen cancelada");
        showImageSourceDialog();
    }

    /**
     * Maneja el click en el botón de confirmar.
     */
    private void handleConfirmButton() {
        if (selectedImageUri == null) {
            showToast("Por favor, seleccione una imagen primero");
            return;
        }
        showImageNameDialog();
    }

    /**
     * Muestra el diálogo para nombrar la imagen.
     */
    private void showImageNameDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_image_name, null);
        EditText nameInput = dialogView.findViewById(R.id.imageNameInput);

        new AlertDialog.Builder(this)
                .setTitle("Nombre de la imagen")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String imageName = nameInput.getText().toString().trim();
                    if (!imageName.isEmpty()) {
                        showFolderSelectionDialog(imageName);
                    } else {
                        showToast("El nombre no puede estar vacío");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Muestra el diálogo para seleccionar la carpeta destino.
     */
    private void showFolderSelectionDialog(String imageName) {
        new FolderSelectionDialog(this, folderManager, folder ->
                saveImageToFolder(folder, imageName)).show();
    }

    /**
     * Guarda la imagen en la carpeta seleccionada.
     */
    private void saveImageToFolder(Folder folder, String imageName) {
        try {
            imageManager.saveImage(selectedImageUri, imageName, folder);
            showToast("Imagen guardada en " + folder.getName());
            finish();
        } catch (Exception e) {
            showToast("Error al guardar la imagen: " + e.getMessage());
        }
    }

    /**
     * Muestra un mensaje Toast.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Método de prueba para establecer una URI de imagen.
     * Solo se usa en pruebas unitarias.
     */
    public void setSelectedImageUri(Uri uri) {
        this.selectedImageUri = uri;
    }
}
package com.dev.brain2.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.widget.Toast;
import android.view.View;

import com.dev.brain2.R;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.FolderSelectionDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImagePickerActivity extends AppCompatActivity {

    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String[] PICKER_OPTIONS = {"Tomar foto", "Seleccionar desde galería"};

    private ImageView previewImageView;
    private Button confirmButton;

    private ImageManager imageManager;
    private FolderManager folderManager;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private String currentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_picker);
        initializeManagers();
        initializeViews();
        setupListeners();
        setupActivityResultLaunchers();

        if (savedInstanceState != null) {
            selectedImageUri = savedInstanceState.getParcelable("selectedImageUri");
            updateImagePreview();
        } else {
            showImageSourceDialog();
        }
    }

    private void initializeManagers() {
        folderManager = new FolderManager(this);
        imageManager = new ImageManager(this, folderManager);
    }

    private void initializeViews() {
        previewImageView = findViewById(R.id.imageView);
        confirmButton = findViewById(R.id.confirmButton);
    }

    private void setupListeners() {
        confirmButton.setOnClickListener(v -> handleConfirmButton());
    }

    private void setupActivityResultLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleGalleryResult(result.getResultCode(), result.getData())
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        updateImagePreview();
                    } else {
                        handleImageSelectionCancelled();
                    }
                }
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> handlePermissionResult(result)
        );
    }

    private void showImageSourceDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Seleccionar imagen")
                .setItems(PICKER_OPTIONS, (dialog, which) -> {
                    if (which == 0) {
                        checkPermissionsAndLaunchCamera();
                    } else {
                        launchGalleryPicker();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void checkPermissionsAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(new String[]{CAMERA_PERMISSION});
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                selectedImageUri = FileProvider.getUriForFile(this,
                        "com.dev.brain2.fileprovider",
                        photoFile);
                cameraLauncher.launch(selectedImageUri);
            }
        } catch (IOException ex) {
            showToast("Error al crear el archivo de imagen");
        }
    }

    private File createImageFile() throws IOException {
        // Crear un nombre de archivo único
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefijo */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */
        );

        // Guardar la ruta para usarla después
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void launchGalleryPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void handleGalleryResult(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            updateImagePreview();
        } else {
            handleImageSelectionCancelled();
        }
    }

    private void handlePermissionResult(java.util.Map<String, Boolean> permissions) {
        boolean allGranted = true;
        for (Boolean granted : permissions.values()) {
            if (!granted) {
                allGranted = false;
                break;
            }
        }
        if (allGranted) {
            launchCamera();
        } else {
            showToast("Permisos necesarios denegados");
            showImageSourceDialog();
        }
    }

    private void updateImagePreview() {
        if (selectedImageUri != null) {
            previewImageView.setImageURI(selectedImageUri);
            confirmButton.setEnabled(true);
        }
    }

    private void handleImageSelectionCancelled() {
        showToast("Selección de imagen cancelada");
        showImageSourceDialog();
    }

    private void handleConfirmButton() {
        if (selectedImageUri == null) {
            showToast("Por favor, seleccione una imagen primero");
            return;
        }
        showImageNameDialog();
    }

    private void showImageNameDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_image_name, null);
        EditText nameInput = dialogView.findViewById(R.id.imageNameInput);

        new androidx.appcompat.app.AlertDialog.Builder(this)
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

    private void showFolderSelectionDialog(String imageName) {
        FolderSelectionDialog folderSelectionDialog = new FolderSelectionDialog(this, folderManager, folder -> {
            saveImageToFolder(folder, imageName);
        });
        folderSelectionDialog.show();
    }

    private void saveImageToFolder(Folder folder, String imageName) {
        try {
            imageManager.saveImage(selectedImageUri, imageName, folder);
            showToast("Imagen guardada en " + folder.getName());
            finish();
        } catch (Exception e) {
            showToast("Error al guardar la imagen: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Manejo de cambios de configuración
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectedImageUri", selectedImageUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedImageUri = savedInstanceState.getParcelable("selectedImageUri");
        updateImagePreview();
    }
}

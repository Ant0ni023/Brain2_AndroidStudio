package com.dev.brain2.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.dev.brain2.R;
import com.dev.brain2.managers.DialogManager;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.managers.PermissionManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.ImageFileHandler;
import com.dev.brain2.utils.Notifier;

// Esta actividad permite seleccionar una imagen de la galería o tomar una foto
public class ImagePickerActivity extends AppCompatActivity {
    // Códigos para permisos y resultados
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    // Elementos de la interfaz
    private ImageView previewImageView;
    private Button confirmButton;

    // Gestores de la aplicación
    private ImageManager imageManager;
    private FolderManager folderManager;
    private DialogManager dialogManager;
    private PermissionManager permissionManager;
    private ImageFileHandler imageFileHandler;

    // URI de la imagen seleccionada
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        // Inicializamos los gestores
        initializeManagers();

        // Configuramos la interfaz
        setupInterface();

        // Restauramos estado o iniciamos selección
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            startImageSelection();
        }
    }

    // Inicializa todos los gestores necesarios
    private void initializeManagers() {
        folderManager = new FolderManager(this);
        imageManager = new ImageManager(this, folderManager);
        dialogManager = new DialogManager(this, folderManager, imageManager);
        permissionManager = new PermissionManager(this);
        imageFileHandler = new ImageFileHandler(this);
    }

    // Configura la interfaz de usuario
    private void setupInterface() {
        // Inicializamos las vistas
        previewImageView = findViewById(R.id.imageView);
        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setEnabled(false);

        // Configuramos el botón de confirmar
        confirmButton.setOnClickListener(v -> handleConfirmButton());
    }

    // Inicia el proceso de selección de imagen
    private void startImageSelection() {
        dialogManager.showImageSourceDialog(
                this::openGallery,
                this::checkCameraPermission
        );
    }

    // Maneja el clic en el botón confirmar
    private void handleConfirmButton() {
        if (selectedImageUri == null) {
            Notifier.showError(this, "Seleccione o capture una imagen primero");
            return;
        }

        // Pedimos nombre y carpeta para la imagen
        dialogManager.showImageNameDialog(imageName ->
                dialogManager.showFolderSelectionDialog(folder ->
                        saveImageToFolder(folder, imageName)
                )
        );
    }

    // Verifica los permisos de cámara
    private void checkCameraPermission() {
        permissionManager.requestPermission(
                this,
                CAMERA_PERMISSION,
                CAMERA_PERMISSION_REQUEST,
                new PermissionManager.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied() {
                        handlePermissionDenied();
                    }
                }
        );
    }

    // Abre la cámara
    private void openCamera() {
        try {
            selectedImageUri = imageFileHandler.createTemporaryImageFile();
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, selectedImageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            Notifier.showError(this, "Error al iniciar la cámara");
        }
    }

    // Abre la galería
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    // Maneja el resultado de la selección/captura de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                updatePreview();
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                selectedImageUri = data.getData();
                updatePreview();
            }
        } else {
            Notifier.showError(this, "No se seleccionó ninguna imagen");
        }
    }

    // Actualiza la vista previa de la imagen
    private void updatePreview() {
        if (selectedImageUri != null) {
            previewImageView.setImageURI(selectedImageUri);
            confirmButton.setEnabled(true);
        }
    }

    // Guarda la imagen en la carpeta seleccionada
    private void saveImageToFolder(Folder folder, String imageName) {
        try {
            imageManager.saveImage(selectedImageUri, imageName, folder);
            Notifier.showInfo(this, "Imagen guardada en " + folder.getName());
            finish();
        } catch (Exception e) {
            Notifier.showError(this, "Error al guardar la imagen: " + e.getMessage());
        }
    }

    // Maneja cuando se deniegan los permisos de cámara
    private void handlePermissionDenied() {
        Notifier.showError(this, "Permisos necesarios denegados");
        startImageSelection();
    }

    // Guarda el estado de la actividad
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectedImageUri", selectedImageUri);
    }

    // Restaura el estado de la actividad
    private void restoreState(Bundle savedInstanceState) {
        selectedImageUri = savedInstanceState.getParcelable("selectedImageUri");
        updatePreview();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreState(savedInstanceState);
    }
}
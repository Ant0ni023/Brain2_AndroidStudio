package com.dev.brain2.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.dev.brain2.databinding.FragmentAddPhotoBinding;
import com.dev.brain2.managers.DialogManager;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.managers.PermissionManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.ImageFileHandler;
import com.dev.brain2.utils.Notifier;

/**
 * Fragmento que maneja la funcionalidad de agregar fotos en la aplicación.
 */
public class AddPhotoFragment extends Fragment {

    private FragmentAddPhotoBinding binding;

    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String KEY_SELECTED_IMAGE_URI = "selectedImageUri";

    private ImageManager imageManager;
    private FolderManager folderManager;
    private DialogManager dialogManager;
    private PermissionManager permissionManager;
    private ImageFileHandler imageFileHandler;

    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private boolean isReturningFromIntent = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPhotoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeManagers();
        setupUIComponents();
        setupActivityResultLaunchers();

        if (savedInstanceState != null) {
            selectedImageUri = savedInstanceState.getParcelable(KEY_SELECTED_IMAGE_URI);
        }

        if (!isReturningFromIntent) {
            startImageSelectionProcess();
        } else {
            isReturningFromIntent = false;
            updateImagePreview();
        }
    }

    /**
     * Inicializa los managers necesarios para el fragmento.
     */
    private void initializeManagers() {
        Activity activity = requireActivity();
        folderManager = new FolderManager(activity);
        imageManager = new ImageManager(activity, folderManager);
        dialogManager = new DialogManager(activity, folderManager, imageManager);
        permissionManager = new PermissionManager(activity);
        imageFileHandler = new ImageFileHandler(activity);
    }

    /**
     * Configura los componentes de la interfaz de usuario.
     */
    private void setupUIComponents() {
        binding.confirmButton.setEnabled(false);
        binding.confirmButton.setOnClickListener(v -> handleConfirmButtonClick());
    }

    /**
     * Configura los ActivityResultLaunchers para manejar los resultados de las actividades.
     */
    private void setupActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleActivityResult(result, true));

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleActivityResult(result, false));

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        handlePermissionDenied();
                    }
                });
    }

    /**
     * Inicia el proceso de selección de imagen.
     */
    private void startImageSelectionProcess() {
        dialogManager.showImageSourceDialog(
                this::openGallery,
                this::checkCameraPermission
        );
    }

    /**
     * Verifica y solicita el permiso de cámara si es necesario.
     */
    private void checkCameraPermission() {
        permissionLauncher.launch(CAMERA_PERMISSION);
    }

    /**
     * Abre la cámara para capturar una imagen.
     */
    private void openCamera() {
        try {
            selectedImageUri = imageFileHandler.createTemporaryImageFile();
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, selectedImageUri);
            cameraLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            Notifier.showError(requireContext(), "Error al iniciar la cámara");
        }
    }

    /**
     * Abre la galería para seleccionar una imagen.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    /**
     * Maneja el resultado de la actividad de cámara o galería.
     *
     * @param result     Resultado de la actividad.
     * @param fromCamera Indica si el resultado es de la cámara.
     */
    private void handleActivityResult(ActivityResult result, boolean fromCamera) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            isReturningFromIntent = true;
            if (fromCamera) {
                updateImagePreview();
            } else if (result.getData() != null) {
                selectedImageUri = result.getData().getData();
                updateImagePreview();
            }
        } else {
            Notifier.showError(requireContext(), "No se seleccionó ninguna imagen");
        }
    }

    /**
     * Actualiza la vista previa de la imagen seleccionada.
     */
    private void updateImagePreview() {
        if (selectedImageUri != null) {
            binding.imageView.setImageURI(selectedImageUri);
            binding.confirmButton.setEnabled(true);
        }
    }

    /**
     * Maneja el clic en el botón de confirmación.
     */
    private void handleConfirmButtonClick() {
        if (selectedImageUri == null) {
            Notifier.showError(requireContext(), "Seleccione o capture una imagen primero");
            return;
        }

        dialogManager.showImageNameDialog(imageName ->
                dialogManager.showFolderSelectionDialog(folder ->
                        saveImageToFolder(folder, imageName)
                )
        );
    }

    /**
     * Guarda la imagen en la carpeta seleccionada.
     *
     * @param folder    Carpeta donde se guardará la imagen.
     * @param imageName Nombre de la imagen.
     */
    private void saveImageToFolder(Folder folder, String imageName) {
        try {
            imageManager.saveImage(selectedImageUri, imageName, folder);
            Notifier.showInfo(requireContext(), "Imagen guardada en " + folder.getName());
        } catch (Exception e) {
            Notifier.showError(requireContext(), "Error al guardar la imagen: " + e.getMessage());
        }
    }

    /**
     * Maneja la denegación del permiso de cámara.
     */
    private void handlePermissionDenied() {
        Notifier.showError(requireContext(), "Permisos necesarios denegados");
        startImageSelectionProcess();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SELECTED_IMAGE_URI, selectedImageUri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

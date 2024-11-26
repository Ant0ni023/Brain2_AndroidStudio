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
import androidx.activity.result.ActivityResultCallback;
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
 * Permite al usuario tomar fotos con la cámara o seleccionarlas desde la galería,
 * y guardarlas en carpetas específicas.
 */
public class AddPhotoFragment extends Fragment {

    /** Binding para acceder a los elementos de la vista del fragmento */
    private FragmentAddPhotoBinding binding;

    /** Constantes para permisos y códigos de solicitud */
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    /** Managers para manejar diferentes aspectos de la aplicación */
    private ImageManager imageManager;
    private FolderManager folderManager;
    private DialogManager dialogManager;
    private PermissionManager permissionManager;
    private ImageFileHandler imageFileHandler;

    /** URI de la imagen seleccionada */
    private Uri selectedImageUri;

    /** Launchers para manejar resultados de actividades */
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    /** Bandera para controlar el retorno de intents */
    private boolean isReturningFromIntent = false;

    /**
     * Crea y retorna la vista inflada del fragmento.
     * @param inflater El inflador de layouts
     * @param container El contenedor padre donde se inflará el fragmento
     * @param savedInstanceState Estado guardado del fragmento
     * @return La vista raíz del fragmento
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddPhotoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Se llama después de que onCreateView() ha terminado.
     * Inicializa los managers, configura la UI y los launchers de resultados.
     * @param view La vista raíz del fragmento
     * @param savedInstanceState Estado guardado del fragmento
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeManagers();
        setupUI();
        setupActivityResultLaunchers();

        if (savedInstanceState != null) {
            selectedImageUri = savedInstanceState.getParcelable("selectedImageUri");
        }

        if (!isReturningFromIntent) {
            startImageSelection();
        } else {
            isReturningFromIntent = false;
            updatePreview();
        }
    }

    /**
     * Inicializa todos los managers necesarios para el funcionamiento del fragmento.
     * Incluye FolderManager, ImageManager, DialogManager, PermissionManager y ImageFileHandler.
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
     * Configura los elementos de la interfaz de usuario.
     * Establece el estado inicial del botón de confirmación y su listener.
     */
    private void setupUI() {
        binding.confirmButton.setEnabled(false);
        binding.confirmButton.setOnClickListener(v -> handleConfirmButton());
    }

    /**
     * Configura los launchers para manejar resultados de actividades.
     * Incluye launchers para cámara, galería y permisos.
     */
    private void setupActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> handleActivityResult(result, true));

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> handleActivityResult(result, false));

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        handlePermissionDenied();
                    }
                });
    }

    /**
     * Inicia el proceso de selección de imagen mostrando un diálogo
     * con opciones para cámara o galería.
     */
    private void startImageSelection() {
        dialogManager.showImageSourceDialog(
                this::openGallery,
                this::checkCameraPermission
        );
    }

    /**
     * Verifica y solicita los permisos necesarios para usar la cámara.
     */
    private void checkCameraPermission() {
        permissionLauncher.launch(CAMERA_PERMISSION);
    }

    /**
     * Abre la cámara para capturar una imagen.
     * Crea un archivo temporal para almacenar la foto.
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
     * Abre la galería para seleccionar una imagen existente.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    /**
     * Maneja el resultado de la captura de imagen o selección desde galería.
     * @param result Resultado de la actividad
     * @param fromCamera true si proviene de la cámara, false si es de la galería
     */
    private void handleActivityResult(ActivityResult result, boolean fromCamera) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            isReturningFromIntent = true;
            if (fromCamera) {
                updatePreview();
            } else if (result.getData() != null) {
                selectedImageUri = result.getData().getData();
                updatePreview();
            }
        } else {
            Notifier.showError(requireContext(), "No se seleccionó ninguna imagen");
        }
    }

    /**
     * Actualiza la vista previa de la imagen seleccionada.
     * Habilita el botón de confirmación si hay una imagen válida.
     */
    private void updatePreview() {
        if (selectedImageUri != null) {
            binding.imageView.setImageURI(selectedImageUri);
            binding.confirmButton.setEnabled(true);
        }
    }

    /**
     * Maneja el click en el botón de confirmación.
     * Muestra diálogos para nombre de imagen y selección de carpeta.
     */
    private void handleConfirmButton() {
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
     * @param folder Carpeta destino para guardar la imagen
     * @param imageName Nombre asignado a la imagen
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
     * Maneja el caso cuando se deniegan los permisos de cámara.
     * Muestra un mensaje de error y reinicia el proceso de selección.
     */
    private void handlePermissionDenied() {
        Notifier.showError(requireContext(), "Permisos necesarios denegados");
        startImageSelection();
    }

    /**
     * Guarda el estado del fragmento.
     * @param outState Bundle donde se guarda el estado
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectedImageUri", selectedImageUri);
    }

    /**
     * Limpia las referencias cuando se destruye la vista del fragmento.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.dev.brain2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import com.dev.brain2.R;
import com.dev.brain2.adapters.ImageAdapter;
import com.dev.brain2.databinding.FragmentFolderContentBinding;
import com.dev.brain2.interfaces.OnImageClickListener;
import com.dev.brain2.managers.DialogManager;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.models.Image;
import com.dev.brain2.utils.Notifier;
import java.util.List;

/**
 * Fragmento que muestra el contenido de una carpeta.
 */
public class FolderContentFragment extends Fragment implements OnImageClickListener {

    private FragmentFolderContentBinding binding;

    private static final String ARG_FOLDER_ID = "folderId";

    private FolderManager folderManager;
    private ImageManager imageManager;
    private DialogManager dialogManager;

    private Folder currentFolder;
    private List<Image> imageList;
    private ImageAdapter imageAdapter;

    public FolderContentFragment() {

    }

    /**
     * Crea una nueva instancia del fragmento con el ID de carpeta proporcionado.
     *
     * @param folderId ID de la carpeta.
     * @return Nueva instancia de FolderContentFragment.
     */
    public static FolderContentFragment newInstance(String folderId) {
        FolderContentFragment fragment = new FolderContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FOLDER_ID, folderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeManagers();
    }

    /**
     * Inicializa los managers necesarios.
     */
    private void initializeManagers() {
        folderManager = new FolderManager(requireContext());
        imageManager = new ImageManager(requireContext(), folderManager);
        dialogManager = new DialogManager(requireContext(), folderManager, imageManager);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFolderContentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadFolderFromArguments();
        setupRecyclerView();
        displayFolderContent();
    }

    /**
     * Carga la carpeta actual desde los argumentos proporcionados.
     */
    private void loadFolderFromArguments() {
        if (getArguments() != null && getArguments().containsKey(ARG_FOLDER_ID)) {
            String folderId = getArguments().getString(ARG_FOLDER_ID);
            currentFolder = folderManager.getFolderById(folderId);

            if (currentFolder != null) {
                binding.folderTitle.setText(currentFolder.getName());
            } else {
                showToast("Error al cargar la carpeta");
                requireActivity().onBackPressed();
            }
        } else {
            showToast("No se proporcionó un ID de carpeta");
            requireActivity().onBackPressed();
        }
    }

    /**
     * Configura el RecyclerView para mostrar las imágenes.
     */
    private void setupRecyclerView() {
        binding.imagesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        imageList = currentFolder != null ? currentFolder.getImages() : null;
        imageAdapter = new ImageAdapter(requireContext(), imageList, this);
        binding.imagesRecyclerView.setAdapter(imageAdapter);
    }

    /**
     * Muestra el contenido de la carpeta.
     */
    private void displayFolderContent() {
        if (currentFolder == null) {
            requireActivity().onBackPressed();
            return;
        }

        if (currentFolder.getImages().isEmpty()) {
            showToast("La carpeta está vacía");
        }
        binding.imagesRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Refresca el contenido del fragmento.
     */
    private void refreshContent() {
        if (currentFolder != null && imageAdapter != null) {
            currentFolder = folderManager.getFolderById(currentFolder.getId());
            if (currentFolder == null) {
                showToast("La carpeta ya no existe");
                requireActivity().onBackPressed();
                return;
            }
            imageList = currentFolder.getImages();
            imageAdapter.updateImages(imageList);
            displayFolderContent();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContent();
    }

    /**
     * Maneja el clic en una imagen.
     *
     * @param image Imagen que fue clickeada.
     */
    @Override
    public void onImageClick(Image image) {
        navigateToImageViewer(image);
    }

    /**
     * Navega al fragmento de visualización de imagen.
     *
     * @param image Imagen seleccionada.
     */
    private void navigateToImageViewer(Image image) {
        Bundle args = new Bundle();
        args.putString("imageUri", image.getUri().toString());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_folderContentFragment_to_imageViewerFragment, args);
    }

    /**
     * Maneja el clic largo en una imagen.
     *
     * @param image Imagen que fue presionada.
     */
    @Override
    public void onImageLongClick(Image image) {
        showImageOptionsDialog(image);
    }

    /**
     * Muestra un diálogo con opciones para la imagen.
     *
     * @param image Imagen seleccionada.
     */
    private void showImageOptionsDialog(Image image) {
        String[] options = {"Mover a otra carpeta", "Eliminar imagen", "Renombrar imagen"};

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Opciones de imagen")
                .setItems(options, (dialog, which) -> handleImageOptionSelected(which, image))
                .show();
    }

    /**
     * Maneja la opción seleccionada en el diálogo de imagen.
     *
     * @param which Índice de la opción seleccionada.
     * @param image Imagen sobre la que se realizará la acción.
     */
    private void handleImageOptionSelected(int which, Image image) {
        switch (which) {
            case 0:
                dialogManager.showImageMoveDialog(currentFolder, image, this::refreshContent);
                break;
            case 1:
                confirmImageDeletion(image);
                break;
            case 2:
                dialogManager.showImageRenameDialog(currentFolder, image, this::refreshContent);
                break;
        }
    }

    /**
     * Muestra una confirmación antes de eliminar una imagen.
     *
     * @param image Imagen a eliminar.
     */
    private void confirmImageDeletion(Image image) {
        Notifier.showDeleteConfirmation(requireContext(),
                "¿Está seguro de eliminar esta imagen?",
                () -> deleteImage(image));
    }

    /**
     * Elimina la imagen y actualiza el contenido.
     *
     * @param image Imagen a eliminar.
     */
    private void deleteImage(Image image) {
        if (imageManager.deleteImage(image, currentFolder)) {
            showToast("Imagen eliminada");
            refreshContent();
        } else {
            showToast("Error al eliminar la imagen");
        }
    }

    /**
     * Muestra un mensaje Toast.
     *
     * @param message Mensaje a mostrar.
     */
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

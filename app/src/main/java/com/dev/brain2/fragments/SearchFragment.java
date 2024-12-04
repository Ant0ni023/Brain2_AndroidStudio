package com.dev.brain2.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dev.brain2.R;
import com.dev.brain2.databinding.FragmentSearchBinding;
import com.dev.brain2.interfaces.OnImageClickListener;
import com.dev.brain2.interfaces.Searchable;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.managers.ImageManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.models.Image;
import com.dev.brain2.utils.RecyclerViewHandler;
import com.dev.brain2.utils.SearchHandler;

import java.util.List;

/**
 * Fragmento para buscar imágenes con autocompletado.
 */
public class SearchFragment extends Fragment implements Searchable, OnImageClickListener {

    private FragmentSearchBinding binding;
    private SearchHandler searchHandler;
    private RecyclerViewHandler recyclerViewHandler;
    private ImageManager imageManager;
    private FolderManager folderManager;
    private List<Image> allImages;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeManagers();
        initializeImageList();
        setupRecyclerViewHandler();
        setupSearchEditText();
    }

    /**
     * Inicializa los managers necesarios.
     */
    private void initializeManagers() {
        folderManager = new FolderManager(requireContext());
        imageManager = new ImageManager(requireContext(), folderManager);
    }

    /**
     * Inicializa la lista de todas las imágenes.
     */
    private void initializeImageList() {
        allImages = imageManager.getAllImages();
    }

    /**
     * Configura el RecyclerViewHandler y el SearchHandler.
     */
    private void setupRecyclerViewHandler() {
        searchHandler = new SearchHandler(allImages);
        recyclerViewHandler = new RecyclerViewHandler(requireContext(), binding.imageRecyclerView, this);
        recyclerViewHandler.setupRecyclerView(allImages);
    }

    /**
     * Configura el EditText de búsqueda con un TextWatcher.
     */
    private void setupSearchEditText() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * Realiza la búsqueda y actualiza la interfaz.
     *
     * @param query Consulta de búsqueda.
     */
    @Override
    public void onSearch(String query) {
        List<Image> filteredImages = searchHandler.performSearch(query.toLowerCase());
        recyclerViewHandler.updateUIWithResults(filteredImages);
    }

    /**
     * Maneja el clic en una imagen de los resultados de búsqueda.
     *
     * @param clickedImage Imagen clickeada.
     */
    @Override
    public void onImageClick(Image clickedImage) {
        String matchedFolderId = findFolderIdByImage(clickedImage);

        if (matchedFolderId != null) {
            navigateToFolderContent(matchedFolderId);
        } else {
            Toast.makeText(requireContext(), "No se encontró la imagen en ninguna carpeta.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Encuentra el ID de la carpeta que contiene la imagen.
     *
     * @param image Imagen buscada.
     * @return ID de la carpeta o null si no se encuentra.
     */
    private String findFolderIdByImage(Image image) {
        List<Folder> allFolders = folderManager.getAllFolders();

        for (Folder folder : allFolders) {
            for (Image img : folder.getImages()) {
                if (img.getId().equals(image.getId()) && img.getName().equals(image.getName())) {
                    return folder.getId();
                }
            }
        }
        return null;
    }

    /**
     * Navega al fragmento de contenido de carpeta.
     *
     * @param folderId ID de la carpeta.
     */
    private void navigateToFolderContent(String folderId) {
        Bundle args = new Bundle();
        args.putString("folderId", folderId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_search_to_folderContentFragment, args);
    }

    @Override
    public void onImageLongClick(Image longClickedImage) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        folderManager = new FolderManager(requireContext());
        // Inicializa componentes
        initializeComponents();
        // Configura RecyclerView y su adaptador a través de RecyclerViewHandler
        recyclerViewHandler.setupRecyclerView();
        setupSearchEditText();
    }

    /**
     * Inicializa los componentes necesarios.
     */
    private void initializeComponents() {
        imageManager = new ImageManager(requireContext(), new FolderManager(requireContext()));
        List<Image> allImages = imageManager.getAllImages();
        // Inicializa los manejadores de búsqueda y RecyclerView
        searchHandler = new SearchHandler(allImages);
        recyclerViewHandler = new RecyclerViewHandler(requireContext(), binding.imageRecyclerView, allImages, this);
    }

    /**
     * Configura el EditText de búsqueda con un TextWatcher.
     */
    private void setupSearchEditText() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            // Realiza la búsqueda cuando cambia el texto
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
        // Obtener todas las carpetas del FolderManager
        List<Folder> allFolders = folderManager.getAllFolders();

        String matchedFolderId = null;

        // Iterar a través de todas las carpetas y sus imágenes
        for (Folder folder : allFolders) {
            for (Image image : folder.getImages()) {
                if (image.getId().equals(clickedImage.getId()) && image.getName().equals(clickedImage.getName())) {
                    matchedFolderId = folder.getId();
                    break;
                }
            }
            if (matchedFolderId != null) {
                break; // Salir del bucle si se encuentra coincidencia
            }
        }

        // Verificar si se encontró una coincidencia
        if (matchedFolderId != null) {
            // Navegar al siguiente fragmento con el ID de la carpeta
            Bundle args = new Bundle();
            args.putString("folderId", matchedFolderId);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_nav_search_to_folderContentFragment, args);
        } else {
            // Mostrar mensaje si no se encontró coincidencia
            Toast.makeText(requireContext(), "No se encontró la imagen en ninguna carpeta.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onImageLongClick(Image image, int position) {
        // Maneja el clic largo en la imagen
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

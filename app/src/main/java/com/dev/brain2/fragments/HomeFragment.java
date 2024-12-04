package com.dev.brain2.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.brain2.R;
import com.dev.brain2.adapters.FolderAdapter;
import com.dev.brain2.databinding.FragmentHomeBinding;
import com.dev.brain2.interfaces.OnFolderClickListener;
import com.dev.brain2.managers.DialogManager;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.Notifier;
import com.dev.brain2.utils.SettingsPrefHelper;
import com.google.gson.Gson;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragmento principal que muestra la lista de carpetas.
 */
public class HomeFragment extends Fragment implements OnFolderClickListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private FolderManager folderManager;
    private DialogManager dialogManager;
    private FolderAdapter folderAdapter;
    private List<Folder> folderList;
    private SettingsPrefHelper settingsPrefHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        initializeManagers();
        initializeViews();
        setupRecyclerView();
        loadFolders();
    }

    /**
     * Inicializa los managers necesarios.
     */
    private void initializeManagers() {
        folderManager = new FolderManager(requireContext());
        dialogManager = new DialogManager(requireContext(), folderManager, null);
        settingsPrefHelper = new SettingsPrefHelper(requireActivity());
    }

    /**
     * Inicializa las vistas del fragmento.
     */
    private void initializeViews() {
        recyclerView = binding.foldersRecyclerView;
    }

    /**
     * Configura el RecyclerView para mostrar las carpetas.
     */
    private void setupRecyclerView() {
        int numberOfColumns = calculateNumberOfColumns();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(),
                numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        folderList = new ArrayList<>();
        folderAdapter = new FolderAdapter(requireContext(), folderList, this);
        recyclerView.setAdapter(folderAdapter);

        int bottomPadding = getBottomPadding();
        recyclerView.setPadding(0, 0, 0, bottomPadding);
        recyclerView.setClipToPadding(false);
    }

    /**
     * Calcula el número de columnas basado en el ancho de la pantalla.
     *
     * @return Número de columnas.
     */
    private int calculateNumberOfColumns() {
        int minItemWidthDp = 120;
        float screenWidthDp = getResources().getDisplayMetrics().widthPixels /
                getResources().getDisplayMetrics().density;
        int numberOfColumns = (int) (screenWidthDp / minItemWidthDp);
        return Math.max(2, numberOfColumns);
    }

    /**
     * Obtiene el padding inferior para el RecyclerView.
     *
     * @return Padding inferior en píxeles.
     */
    private int getBottomPadding() {
        int bottomPaddingDp = 56; // Altura aproximada de la barra de navegación en dp
        float density = getResources().getDisplayMetrics().density;
        return (int) (bottomPaddingDp * density);
    }

    /**
     * Carga las carpetas y actualiza el adaptador.
     */
    private void loadFolders() {
        folderList = folderManager.getFolders();
        if (folderAdapter != null) {
            folderAdapter.updateFolders(folderList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFolders();
    }

    /**
     * Maneja el clic en una carpeta.
     *
     * @param clickedFolder Carpeta que fue clickeada.
     */
    @Override
    public void onFolderClick(Folder clickedFolder) {
        updateRecentFoldersList(clickedFolder.getId());
        navigateToFolderContent(clickedFolder);
    }

    /**
     * Navega al fragmento de contenido de carpeta.
     *
     * @param folder Carpeta seleccionada.
     */
    private void navigateToFolderContent(Folder folder) {
        Bundle args = new Bundle();
        args.putString("folderId", folder.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_home_to_folderContentFragment, args);
    }

    /**
     * Actualiza la lista de carpetas recientes en las preferencias.
     *
     * @param folderId ID de la carpeta abierta.
     */
    private void updateRecentFoldersList(String folderId) {
        String recentFoldersJson = settingsPrefHelper.getString("recentFolders", "[]");
        Gson gson = new Gson();
        List<String> recentFolderIds = new ArrayList<>(Arrays.asList(
                gson.fromJson(recentFoldersJson, String[].class)));

        recentFolderIds.remove(folderId);
        recentFolderIds.add(0, folderId);

        String updatedRecentFoldersJson = gson.toJson(recentFolderIds);
        settingsPrefHelper.saveString("recentFolders", updatedRecentFoldersJson);
    }

    /**
     * Maneja el clic largo en una carpeta.
     *
     * @param longClickedFolder Carpeta que fue presionada.
     */
    @Override
    public void onFolderLongClick(Folder longClickedFolder) {
        showFolderOptionsDialog(longClickedFolder);
    }

    /**
     * Muestra un diálogo con opciones para la carpeta.
     *
     * @param folder Carpeta seleccionada.
     */
    private void showFolderOptionsDialog(Folder folder) {
        String[] options = {"Modificar carpeta", "Eliminar carpeta"};

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Opciones de carpeta")
                .setItems(options, (dialog, which) ->
                        handleFolderOptionSelected(which, folder))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Maneja la opción seleccionada en el diálogo de carpeta.
     *
     * @param which  Índice de la opción seleccionada.
     * @param folder Carpeta sobre la que se realizará la acción.
     */
    private void handleFolderOptionSelected(int which, Folder folder) {
        if (which == 0) {
            dialogManager.showFolderEditDialog(folder, updatedFolder -> loadFolders());
        } else if (which == 1) {
            confirmFolderDeletion(folder);
        }
    }

    /**
     * Muestra una confirmación antes de eliminar una carpeta.
     *
     * @param folder Carpeta a eliminar.
     */
    private void confirmFolderDeletion(Folder folder) {
        Notifier.showDeleteConfirmation(requireContext(),
                "¿Eliminar esta carpeta?", () -> {
                    folderManager.deleteFolder(folder);
                    loadFolders();
                    Notifier.showInfo(requireContext(),
                            "Carpeta eliminada: " + folder.getName());
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

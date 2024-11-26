package com.dev.brain2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dev.brain2.R;
import com.dev.brain2.adapters.FolderAdapter;
import com.dev.brain2.databinding.FragmentHomeBinding;
import com.dev.brain2.interfaces.OnFolderClickListener;
import com.dev.brain2.managers.DialogManager;
import com.dev.brain2.managers.FolderGridManager;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.Notifier;
import com.dev.brain2.utils.SettingsPrefHelper;
import com.google.gson.Gson;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
    private FolderGridManager folderGridManager;
    private FolderAdapter folderAdapter;
    private List<Folder> folderList;
    private SettingsPrefHelper settingsPrefHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inicializar managers
        folderManager = new FolderManager(requireContext());
        dialogManager = new DialogManager(requireContext(), folderManager, null);
        settingsPrefHelper = new SettingsPrefHelper(requireActivity());
        // Inicializar vistas
        recyclerView = binding.foldersRecyclerView;

        // Configurar UI
        setupRecyclerView();
        loadFolders();
    }

    /**
     * Configura el RecyclerView para mostrar las carpetas.
     */
    private void setupRecyclerView() {
        folderGridManager = new FolderGridManager(requireContext());
        recyclerView.setLayoutManager(folderGridManager);

        folderAdapter = new FolderAdapter(requireContext(), folderList, this);
        recyclerView.setAdapter(folderAdapter);

        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.nav_bar_height);
        recyclerView.setPadding(0, 0, 0, bottomPadding);
        recyclerView.setClipToPadding(false);
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
     * @param folder Carpeta que fue clickeada.
     */
    @Override
    public void onFolderClick(Folder folder) {
        updateRecentFoldersList(folder.getId());
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
        // Obtener la lista actual de carpetas recientes
        String recentFoldersJson = settingsPrefHelper.getString("recentFolders", "[]");
        Gson gson = new Gson();
        List<String> recentFolderIds = new ArrayList<>(Arrays.asList(gson.fromJson(recentFoldersJson, String[].class)));

        // Remover el ID si ya existe
        recentFolderIds.remove(folderId);
        // Agregar el ID al inicio de la lista
        recentFolderIds.add(0, folderId);

        // Guardar la lista actualizada
        String updatedRecentFoldersJson = gson.toJson(recentFolderIds);
        settingsPrefHelper.saveString("recentFolders", updatedRecentFoldersJson);
    }

    /**
     * Maneja el clic largo en una carpeta.
     *
     * @param folder   Carpeta que fue presionada.
     * @param position Posición en la lista.
     */
    @Override
    public void onFolderLongClick(Folder folder, int position) {
        showFolderOptionsDialog(folder);
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
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        dialogManager.showFolderEditDialog(folder, updatedFolder -> loadFolders());
                    } else if (which == 1) {
                        Notifier.showDeleteConfirmation(requireContext(), "¿Eliminar esta carpeta?", () -> {
                            folderManager.deleteFolder(folder);
                            loadFolders();
                            Notifier.showInfo(requireContext(), "Carpeta eliminada: " + folder.getName());
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

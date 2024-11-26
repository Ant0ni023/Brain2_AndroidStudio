package com.dev.brain2.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import com.dev.brain2.fragments.SettingsFragment;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.SettingsPrefHelper;
import com.google.gson.Gson;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Esta clase se encarga de gestionar las carpetas de la aplicación.
 */
public class FolderManager {
    // Nombres para guardar las preferencias
    private static final String PREFS_NAME = "Brain2Prefs";
    private static final String FOLDERS_KEY = "folders";

    // Variables para manejar el almacenamiento
    private SharedPreferences sharedPreferences;
    private SettingsPrefHelper settingsPrefHelper;
    private Gson gson;
    private Context context;

    /**
     * Constructor.
     *
     * @param context Contexto de la aplicación.
     */
    public FolderManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.settingsPrefHelper = new SettingsPrefHelper(context);
        this.gson = new Gson();
    }

    /**
     * Crea una nueva carpeta.
     *
     * @param folder Carpeta a agregar.
     */
    public void addFolder(Folder folder) {
        // Si la carpeta no tiene ID, le creamos uno
        if (folder.getId() == null) {
            folder.setId(java.util.UUID.randomUUID().toString());
        }

        // Obtenemos la lista actual de carpetas y añadimos la nueva
        List<Folder> folders = getFolders();
        folders.add(folder);

        // Guardamos los cambios
        saveFolders(folders);
        createFolderOnDisk(folder.getName());
    }

    /**
     * Obtiene la lista de carpetas.
     *
     * @return Lista de carpetas.
     */
    public List<Folder> getFolders() {
        // Obtener JSON guardado y preferencias
        String foldersJson = sharedPreferences.getString(FOLDERS_KEY, "[]");
        boolean shouldShowLastOpenFirst = settingsPrefHelper.getBoolean(SettingsFragment.KEY_LAST_OPENED, false);
        String recentFoldersJson = settingsPrefHelper.getString("recentFolders", "[]");

        // Convertir JSON a array
        Folder[] folderArray = gson.fromJson(foldersJson, Folder[].class);
        if (folderArray == null) {
            return new ArrayList<>();
        }

        List<Folder> folders = new ArrayList<>(Arrays.asList(folderArray));

        if (shouldShowLastOpenFirst) {
            // Obtener lista de IDs de carpetas recientes
            List<String> recentFolderIds = new ArrayList<>(Arrays.asList(gson.fromJson(recentFoldersJson, String[].class)));
            // Crear un mapa de IDs a carpetas
            HashMap<String, Folder> folderMap = new HashMap<>();
            for (Folder folder : folders) {
                folderMap.put(folder.getId(), folder);
            }
            // Construir una nueva lista de carpetas
            List<Folder> reorderedFolders = new ArrayList<>();
            // Agregar carpetas según el orden de recientes
            for (String id : recentFolderIds) {
                Folder folder = folderMap.get(id);
                if (folder != null) {
                    reorderedFolders.add(folder);
                    folders.remove(folder); // Eliminar de la lista original
                }
            }
            // Agregar las carpetas restantes
            reorderedFolders.addAll(folders);
            return reorderedFolders;
        } else {
            return folders;
        }
    }

    /**
     * Actualiza una carpeta existente.
     *
     * @param folder Carpeta a actualizar.
     */
    public void updateFolder(Folder folder) {
        List<Folder> folders = getFolders();

        // Buscamos la carpeta a actualizar
        for (int i = 0; i < folders.size(); i++) {
            if (folders.get(i).getId().equals(folder.getId())) {
                // Si el nombre cambió, renombramos la carpeta en el sistema
                if (!folders.get(i).getName().equals(folder.getName())) {
                    renameFolderOnDisk(folders.get(i).getName(), folder.getName());
                }
                folders.set(i, folder);
                break;
            }
        }

        // Guardamos los cambios
        saveFolders(folders);

        // Si la carpeta está vacía, la eliminamos
        if (folder.getImages().isEmpty()) {
            deleteFolder(folder);
        }
    }

    /**
     * Elimina una carpeta.
     *
     * @param folder Carpeta a eliminar.
     */
    public void deleteFolder(Folder folder) {
        List<Folder> folders = getFolders();
        // Eliminamos la carpeta de la lista
        folders.removeIf(f -> f.getId().equals(folder.getId()));

        // Guardamos los cambios
        saveFolders(folders);
        deleteFolderOnDisk(folder.getName());
    }

    /**
     * Guarda la lista de carpetas en las preferencias.
     *
     * @param folders Lista de carpetas.
     */
    private void saveFolders(List<Folder> folders) {
        String json = gson.toJson(folders);
        sharedPreferences.edit()
                .putString(FOLDERS_KEY, json)
                .apply();
    }

    /**
     * Crea una carpeta en el almacenamiento.
     *
     * @param folderName Nombre de la carpeta.
     * @return Archivo de la carpeta creada.
     */
    public File createFolderOnDisk(String folderName) {
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File newFolder = new File(baseDir, folderName);
        if (!newFolder.exists()) {
            newFolder.mkdirs();
        }
        return newFolder;
    }

    /**
     * Renombra una carpeta en el almacenamiento.
     *
     * @param oldName Nombre antiguo.
     * @param newName Nuevo nombre.
     */
    private void renameFolderOnDisk(String oldName, String newName) {
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File oldFolder = new File(baseDir, oldName);
        File newFolder = new File(baseDir, newName);
        if (oldFolder.exists()) {
            oldFolder.renameTo(newFolder);
        }
    }

    /**
     * Elimina una carpeta del almacenamiento.
     *
     * @param folderName Nombre de la carpeta.
     */
    private void deleteFolderOnDisk(String folderName) {
        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), folderName);
        deleteRecursive(folder);
    }

    /**
     * Elimina una carpeta y todo su contenido recursivamente.
     *
     * @param file Archivo o carpeta a eliminar.
     */
    private void deleteRecursive(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteRecursive(child);
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * Busca una carpeta por su ID.
     *
     * @param folderId ID de la carpeta.
     * @return Carpeta encontrada o null si no existe.
     */
    public Folder getFolderById(String folderId) {
        for (Folder folder : getFolders()) {
            if (folder.getId().equals(folderId)) {
                return folder;
            }
        }
        return null;
    }

    /**
     * Busca una carpeta por su nombre.
     *
     * @param folderName Nombre de la carpeta.
     * @return Carpeta encontrada o null si no existe.
     */
    public Folder getFolderByName(String folderName) {
        for (Folder folder : getFolders()) {
            if (folder.getName().equals(folderName)) {
                return folder;
            }
        }
        return null;
    }

    /**
     * Obtiene todas las carpetas excepto una específica.
     *
     * @param excludeFolder Carpeta a excluir.
     * @return Lista de carpetas disponibles.
     */
    public List<Folder> getAvailableFolders(Folder excludeFolder) {
        List<Folder> availableFolders = getFolders();
        availableFolders.removeIf(folder -> folder.getId().equals(excludeFolder.getId()));
        return availableFolders;
    }

    /**
     * Obtiene la lista de todas las carpetas.
     *
     * @return Lista de carpetas.
     */
    public List<Folder> getAllFolders() {
        return getFolders();
    }
}

package com.dev.brain2.managers;

import android.content.Context;
import android.os.Environment;

import com.dev.brain2.fragments.SettingsFragment;
import com.dev.brain2.models.Folder;
import com.dev.brain2.utils.DataStorage;
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

    private final Context appContext;
    private final SettingsPrefHelper settingsPrefHelper;
    private final DataStorage dataStorage;
    private final Gson gson;

    /**
     * Constructor.
     *
     * @param context Contexto de la aplicación.
     */
    public FolderManager(Context context) {
        this.appContext = context.getApplicationContext();
        this.settingsPrefHelper = new SettingsPrefHelper(context);
        this.dataStorage = new DataStorage(context);
        this.gson = new Gson();
    }

    /**
     * Agrega una nueva carpeta.
     *
     * @param folder Carpeta a agregar.
     */
    public void addFolder(Folder folder) {
        if (folder.getId() == null) {
            folder.setId(java.util.UUID.randomUUID().toString());
        }

        List<Folder> folders = getFolders();
        folders.add(folder);
        saveFolders(folders);
        createFolderOnDisk(folder.getName());
    }

    /**
     * Obtiene la lista de carpetas.
     *
     * @return Lista de carpetas.
     */
    public List<Folder> getFolders() {
        List<Folder> folders = dataStorage.readFolders();
        if (folders == null) {
            folders = new ArrayList<>();
        }
        return reorderFoldersIfNeeded(folders);
    }

    /**
     * Reordena las carpetas si la opción de mostrar las últimas abiertas está activada.
     *
     * @param folders Lista original de carpetas.
     * @return Lista posiblemente reordenada.
     */
    private List<Folder> reorderFoldersIfNeeded(List<Folder> folders) {
        boolean shouldShowLastOpenFirst = settingsPrefHelper.getBoolean(SettingsFragment.KEY_LAST_OPENED, false);

        if (!shouldShowLastOpenFirst) {
            return folders;
        }

        String recentFoldersJson = settingsPrefHelper.getString("recentFolders", "[]");
        List<String> recentFolderIds = new ArrayList<>(Arrays.asList(
                gson.fromJson(recentFoldersJson, String[].class)
        ));

        HashMap<String, Folder> folderMap = new HashMap<>();
        for (Folder folder : folders) {
            folderMap.put(folder.getId(), folder);
        }

        List<Folder> reorderedFolders = new ArrayList<>();
        for (String id : recentFolderIds) {
            Folder folder = folderMap.get(id);
            if (folder != null) {
                reorderedFolders.add(folder);
                folders.remove(folder);
            }
        }

        reorderedFolders.addAll(folders);
        return reorderedFolders;
    }

    /**
     * Actualiza una carpeta existente.
     *
     * @param folder Carpeta a actualizar.
     */
    public void updateFolder(Folder folder) {
        List<Folder> folders = getFolders();
        updateFolderInList(folders, folder);
        saveFolders(folders);
        checkAndDeleteEmptyFolder(folder);
    }

    /**
     * Actualiza la carpeta en la lista y en el almacenamiento si es necesario.
     *
     * @param folders Lista de carpetas.
     * @param folder  Carpeta a actualizar.
     */
    private void updateFolderInList(List<Folder> folders, Folder folder) {
        for (int i = 0; i < folders.size(); i++) {
            if (folders.get(i).getId().equals(folder.getId())) {
                if (!folders.get(i).getName().equals(folder.getName())) {
                    renameFolderOnDisk(folders.get(i).getName(), folder.getName());
                }
                folders.set(i, folder);
                break;
            }
        }
    }

    /**
     * Elimina una carpeta vacía si es necesario.
     *
     * @param folder Carpeta a verificar.
     */
    private void checkAndDeleteEmptyFolder(Folder folder) {
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
        folders.removeIf(f -> f.getId().equals(folder.getId()));
        saveFolders(folders);
        deleteFolderOnDisk(folder.getName());
    }

    /**
     * Guarda la lista de carpetas en el archivo JSON.
     *
     * @param folders Lista de carpetas.
     */
    private void saveFolders(List<Folder> folders) {
        dataStorage.writeFolders(folders);
    }

    /**
     * Crea una carpeta en el almacenamiento.
     *
     * @param folderName Nombre de la carpeta.
     * @return Archivo de la carpeta creada.
     */
    public File createFolderOnDisk(String folderName) {
        File baseDir = appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        File baseDir = appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        File folder = new File(appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), folderName);
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

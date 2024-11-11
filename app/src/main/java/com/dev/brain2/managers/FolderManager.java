package com.dev.brain2.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import com.google.gson.Gson;
import com.dev.brain2.models.Folder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Esta clase se encarga de gestionar las carpetas de la aplicación
public class FolderManager {
    // Nombres para guardar las preferencias
    private static final String PREFS_NAME = "Brain2Prefs";
    private static final String FOLDERS_KEY = "folders";

    // Variables para manejar el almacenamiento
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Context context;

    // Constructor
    public FolderManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // Crea una nueva carpeta
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

    // Obtiene la lista de todas las carpetas
    public List<Folder> getFolders() {
        // Obtenemos el JSON guardado
        String foldersJson = sharedPreferences.getString(FOLDERS_KEY, "[]");

        // Convertimos el JSON a un array de carpetas
        Folder[] folderArray = gson.fromJson(foldersJson, Folder[].class);

        // Creamos una lista y añadimos todas las carpetas
        List<Folder> folders = new ArrayList<>();
        if (folderArray != null) {
            for (Folder folder : folderArray) {
                folders.add(folder);
            }
        }
        return folders;
    }

    // Actualiza una carpeta existente
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

    // Elimina una carpeta
    public void deleteFolder(Folder folder) {
        List<Folder> folders = getFolders();
        // Eliminamos la carpeta de la lista
        folders.removeIf(f -> f.getId().equals(folder.getId()));

        // Guardamos los cambios
        saveFolders(folders);
        deleteFolderOnDisk(folder.getName());
    }

    // Guarda la lista de carpetas en las preferencias
    private void saveFolders(List<Folder> folders) {
        String json = gson.toJson(folders);
        sharedPreferences.edit()
                .putString(FOLDERS_KEY, json)
                .apply();
    }

    // Crea una carpeta en el almacenamiento
    public File createFolderOnDisk(String folderName) {
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File newFolder = new File(baseDir, folderName);
        if (!newFolder.exists()) {
            newFolder.mkdirs();
        }
        return newFolder;
    }

    // Renombra una carpeta en el almacenamiento
    private void renameFolderOnDisk(String oldName, String newName) {
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File oldFolder = new File(baseDir, oldName);
        File newFolder = new File(baseDir, newName);
        if (oldFolder.exists()) {
            oldFolder.renameTo(newFolder);
        }
    }

    // Elimina una carpeta del almacenamiento
    private void deleteFolderOnDisk(String folderName) {
        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), folderName);
        deleteRecursive(folder);
    }

    // Elimina una carpeta y todo su contenido
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

    // Busca una carpeta por su ID
    public Folder getFolderById(String folderId) {
        for (Folder folder : getFolders()) {
            if (folder.getId().equals(folderId)) {
                return folder;
            }
        }
        return null;
    }

    // Busca una carpeta por su nombre
    public Folder getFolderByName(String folderName) {
        for (Folder folder : getFolders()) {
            if (folder.getName().equals(folderName)) {
                return folder;
            }
        }
        return null;
    }

    // Obtiene todas las carpetas excepto una específica
    public List<Folder> getAvailableFolders(Folder excludeFolder) {
        List<Folder> availableFolders = getFolders();
        availableFolders.removeIf(folder -> folder.getId().equals(excludeFolder.getId()));
        return availableFolders;
    }
}
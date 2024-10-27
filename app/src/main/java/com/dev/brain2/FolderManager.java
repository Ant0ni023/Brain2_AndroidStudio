package com.dev.brain2;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FolderManager {

    private static final String PREFS_NAME = "Brain2Prefs";
    private static final String FOLDERS_KEY = "folders";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public FolderManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addFolder(String folderName, String color) {
        List<Folder> folders = getFolders();
        Folder folder = new Folder(folderName, color);
        folders.add(folder);
        saveFolders(folders);
    }

    public List<Folder> getFolders() {
        String foldersJson = sharedPreferences.getString(FOLDERS_KEY, "[]");
        Type folderListType = new TypeToken<ArrayList<Folder>>() {}.getType();
        return gson.fromJson(foldersJson, folderListType);
    }

    private void saveFolders(List<Folder> folders) {
        String foldersJson = gson.toJson(folders);
        sharedPreferences.edit().putString(FOLDERS_KEY, foldersJson).apply();
    }

    public void updateFolder(Folder updatedFolder) {
        List<Folder> folders = getFolders();
        for (int i = 0; i < folders.size(); i++) {
            if (folders.get(i).getName().equals(updatedFolder.getName())) {
                folders.set(i, updatedFolder);
                break;
            }
        }
        saveFolders(folders);
    }

    public Folder getFolderByName(String folderName) {
        for (Folder folder : getFolders()) {
            if (folder.getName().equals(folderName)) {
                return folder;
            }
        }
        return null;
    }

    public List<String> getFolderNames() {
        List<Folder> folders = getFolders();
        List<String> folderNames = new ArrayList<>();
        for (Folder folder : folders) {
            folderNames.add(folder.getName());
        }
        return folderNames;
    }

    // Método para eliminar una carpeta sin importar si está vacía
    public void deleteFolder(Folder folder) {
        List<Folder> folders = getFolders();
        folders.removeIf(f -> f.getName().equals(folder.getName()));
        saveFolders(folders);
    }

    // Método para eliminar una carpeta si está vacía
    public void deleteFolderIfEmpty(Folder folder) {
        if (folder.getImages().isEmpty()) {
            deleteFolder(folder);
        }
    }

    // Método para obtener carpetas disponibles para mover imágenes (excluyendo la carpeta actual)
    public List<Folder> getAvailableFolders(Folder excludeFolder) {
        List<Folder> folders = getFolders();
        folders.removeIf(folder -> folder.getName().equals(excludeFolder.getName())); // Excluye la carpeta actual
        return folders;
    }
}

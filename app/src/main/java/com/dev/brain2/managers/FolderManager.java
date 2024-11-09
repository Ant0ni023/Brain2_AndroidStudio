package com.dev.brain2.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.google.gson.Gson;

import com.dev.brain2.models.Folder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FolderManager {

    private static final String PREFS_NAME = "Brain2Prefs";
    private static final String FOLDERS_KEY = "folders";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Context context; // Necesario para acceder al sistema de archivos


    public FolderManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.context = context;
    }


    public void addFolder(Folder folder) {
        // Asignar un ID único si no tiene uno
        if (folder.getId() == null) {
            folder.setId(UUID.randomUUID().toString());
        }
        List<Folder> folders = getFolders();
        folders.add(folder);
        saveFolders(folders);

        // Crear carpeta física
        createFolder(folder.getName());
    }


    public List<Folder> getFolders() {
        String foldersJson = sharedPreferences.getString(FOLDERS_KEY, "[]");
        Folder[] folderArray = gson.fromJson(foldersJson, Folder[].class);
        List<Folder> folders = new ArrayList<>();
        if (folderArray != null) {
            for (Folder folder : folderArray) {
                folders.add(folder);
            }
        }
        return folders;
    }

    public void updateFolder(Folder folder) {
        List<Folder> folders = getFolders();
        for (int i = 0; i < folders.size(); i++) {
            if (Objects.equals(folders.get(i).getId(), folder.getId())) {
                // Renombrar carpeta física si el nombre ha cambiado
                if (!Objects.equals(folders.get(i).getName(), folder.getName())) {
                    renameFolderOnDisk(folders.get(i).getName(), folder.getName());
                }
                folders.set(i, folder);
                break;
            }
        }
        saveFolders(folders);
    }


    public void deleteFolder(Folder folder) {
        List<Folder> folders = getFolders();
        for (int i = 0; i < folders.size(); i++) {
            if (Objects.equals(folders.get(i).getId(), folder.getId())) {
                folders.remove(i);
                break;
            }
        }
        saveFolders(folders);

        // Eliminar carpeta física
        File folderFile = getFolder(folder.getName());
        deleteRecursive(folderFile);
    }

    private void saveFolders(List<Folder> folders) {
        String foldersJson = gson.toJson(folders);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FOLDERS_KEY, foldersJson);
        editor.apply();
    }

    public File createFolder(String folderName) {
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File newFolder = new File(baseDir, folderName);

        if (!newFolder.exists()) {
            newFolder.mkdirs();
        }

        return newFolder;
    }

    private void renameFolderOnDisk(String oldName, String newName) {
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File oldFolder = new File(baseDir, oldName);
        File newFolder = new File(baseDir, newName);

        if (oldFolder.exists()) {
            oldFolder.renameTo(newFolder);
        }
    }

    public File getFolder(String folderName) {
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(baseDir, folderName);
    }

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

    public Folder getFolderByName(String folderName) {
        List<Folder> folders = getFolders();
        for (Folder folder : folders) {
            if (Objects.equals(folder.getName(), folderName)) {
                return folder;
            }
        }
        return null;
    }

    public Folder getFolderById(String folderId) {
        List<Folder> folders = getFolders();
        for (Folder folder : folders) {
            if (Objects.equals(folder.getId(), folderId)) {
                return folder;
            }
        }
        return null;
    }

    public List<Folder> getAvailableFolders(Folder excludeFolder) {
        List<Folder> availableFolders = getFolders();
        availableFolders.removeIf(folder -> Objects.equals(folder.getId(), excludeFolder.getId()));
        return availableFolders;
    }
}

package com.dev.brain2;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Clase responsable de la gestión y persistencia de carpetas en la aplicación.
 * Utiliza SharedPreferences para almacenar los datos de las carpetas en formato JSON.
 *
 * Esta clase sigue el patrón Singleton y el principio de Responsabilidad Única (SRP),
 * encargándose exclusivamente de la gestión del ciclo de vida de las carpetas.
 */
public class FolderManager {

    // Constantes para SharedPreferences
    private static final String PREFS_NAME = "Brain2Prefs";
    private static final String FOLDERS_KEY = "folders";

    // Dependencias
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    /**
     * Constructor que inicializa el FolderManager con el contexto necesario.
     *
     * @param context Contexto de la aplicación necesario para SharedPreferences
     * @throws IllegalArgumentException si el contexto es null
     */
    public FolderManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("El contexto no puede ser null");
        }
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Crea y añade una nueva carpeta al almacenamiento.
     *
     * @param folderName Nombre de la nueva carpeta
     * @param color Color de la nueva carpeta
     * @throws IllegalArgumentException si el nombre o color son inválidos
     * @throws IllegalStateException si ya existe una carpeta con el mismo nombre
     */
    public void addFolder(String folderName, String color) {
        // Validación de parámetros
        validateFolderParameters(folderName, color);

        List<Folder> folders = getFolders();

        // Verificar si ya existe una carpeta con el mismo nombre
        if (folders.stream().anyMatch(f -> f.getName().equals(folderName))) {
            throw new IllegalStateException("Ya existe una carpeta con el nombre: " + folderName);
        }

        // Crear y añadir la nueva carpeta
        Folder newFolder = new Folder(folderName, color);
        folders.add(newFolder);
        saveFolders(folders);
    }

    /**
     * Obtiene todas las carpetas almacenadas.
     *
     * @return List<Folder> Lista inmutable de carpetas
     */
    public List<Folder> getFolders() {
        String foldersJson = sharedPreferences.getString(FOLDERS_KEY, "[]");
        Type folderListType = new TypeToken<ArrayList<Folder>>(){}.getType();
        List<Folder> folders = gson.fromJson(foldersJson, folderListType);
        return folders != null ? folders : new ArrayList<>();
    }

    /**
     * Actualiza una carpeta existente.
     *
     * @param updatedFolder Carpeta con los datos actualizados
     * @throws IllegalArgumentException si la carpeta es null
     * @throws IllegalStateException si la carpeta no existe
     */
    public void updateFolder(Folder updatedFolder) {
        if (updatedFolder == null) {
            throw new IllegalArgumentException("La carpeta no puede ser null");
        }

        List<Folder> folders = getFolders();
        boolean folderUpdated = false;

        for (int i = 0; i < folders.size(); i++) {
            if (folders.get(i).getName().equals(updatedFolder.getName())) {
                folders.set(i, updatedFolder);
                folderUpdated = true;
                break;
            }
        }

        if (!folderUpdated) {
            throw new IllegalStateException("No se encontró la carpeta a actualizar");
        }

        saveFolders(folders);
    }

    /**
     * Busca y retorna una carpeta por su nombre.
     *
     * @param folderName Nombre de la carpeta a buscar
     * @return Optional<Folder> Carpeta encontrada o Optional vacío si no existe
     */
    public Optional<Folder> getFolderByName(String folderName) {
        if (folderName == null || folderName.trim().isEmpty()) {
            return Optional.empty();
        }

        return getFolders().stream()
                .filter(folder -> folder.getName().equals(folderName))
                .findFirst();
    }

    /**
     * Obtiene los nombres de todas las carpetas.
     *
     * @return List<String> Lista inmutable con los nombres de las carpetas
     */
    public List<String> getFolderNames() {
        List<String> folderNames = new ArrayList<>();
        for (Folder folder : getFolders()) {
            folderNames.add(folder.getName());
        }
        return Collections.unmodifiableList(folderNames);
    }

    /**
     * Elimina una carpeta del almacenamiento.
     *
     * @param folder Carpeta a eliminar
     * @throws IllegalArgumentException si la carpeta es null
     */
    public void deleteFolder(Folder folder) {
        if (folder == null) {
            throw new IllegalArgumentException("La carpeta no puede ser null");
        }

        List<Folder> folders = getFolders();
        folders.removeIf(f -> f.getName().equals(folder.getName()));
        saveFolders(folders);
    }

    /**
     * Elimina una carpeta solo si está vacía.
     *
     * @param folder Carpeta a eliminar
     * @return boolean true si la carpeta fue eliminada, false si no estaba vacía
     */
    public boolean deleteFolderIfEmpty(Folder folder) {
        if (folder != null && folder.isEmpty()) {
            deleteFolder(folder);
            return true;
        }
        return false;
    }

    /**
     * Obtiene las carpetas disponibles para mover imágenes, excluyendo una carpeta específica.
     *
     * @param excludeFolder Carpeta a excluir de la lista
     * @return List<Folder> Lista de carpetas disponibles
     */
    public List<Folder> getAvailableFolders(Folder excludeFolder) {
        List<Folder> availableFolders = new ArrayList<>(getFolders());
        if (excludeFolder != null) {
            availableFolders.removeIf(folder ->
                    folder.getName().equals(excludeFolder.getName()));
        }
        return availableFolders;
    }

    /**
     * Guarda la lista de carpetas en SharedPreferences.
     *
     * @param folders Lista de carpetas a guardar
     */
    private void saveFolders(List<Folder> folders) {
        String foldersJson = gson.toJson(folders);
        sharedPreferences.edit()
                .putString(FOLDERS_KEY, foldersJson)
                .apply();
    }

    /**
     * Valida los parámetros de entrada para una carpeta.
     *
     * @param folderName Nombre de la carpeta a validar
     * @param color Color de la carpeta a validar
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    private void validateFolderParameters(String folderName, String color) {
        if (folderName == null || folderName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la carpeta no puede estar vacío");
        }
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("El color de la carpeta no puede estar vacío");
        }
    }
}
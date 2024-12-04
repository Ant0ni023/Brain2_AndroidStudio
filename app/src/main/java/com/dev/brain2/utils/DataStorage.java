package com.dev.brain2.utils;

import android.content.Context;

import com.dev.brain2.models.Folder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Clase que maneja la persistencia y almacenamiento de datos en formato JSON.
 */
public class DataStorage {

    private static final String FOLDERS_FILE_NAME = "folders.json";
    private static final String SETTINGS_FILE_NAME = "settings.json";

    private final Context appContext;
    private final Gson gson;

    /**
     * Constructor.
     *
     * @param context Contexto de la aplicación.
     */
    public DataStorage(Context context) {
        this.appContext = context.getApplicationContext();
        this.gson = new Gson();
    }

    // Métodos para manejar los datos de Folder

    /**
     * Lee la lista de carpetas desde el archivo JSON.
     *
     * @return Lista de carpetas.
     */
    public List<Folder> readFolders() {
        File dataFile = getDataFile(FOLDERS_FILE_NAME);

        if (!dataFile.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            return gson.fromJson(reader, new TypeToken<List<Folder>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Escribe la lista de carpetas al archivo JSON.
     *
     * @param folders Lista de carpetas a guardar.
     */
    public void writeFolders(List<Folder> folders) {
        File dataFile = getDataFile(FOLDERS_FILE_NAME);

        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(folders, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Métodos para manejar las configuraciones (settings)

    /**
     * Lee las configuraciones desde el archivo JSON.
     *
     * @return Mapa de configuraciones.
     */
    public Map<String, Object> readSettings() {
        File dataFile = getDataFile(SETTINGS_FILE_NAME);

        if (!dataFile.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            return gson.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Escribe las configuraciones al archivo JSON.
     *
     * @param settings Mapa de configuraciones a guardar.
     */
    public void writeSettings(Map<String, Object> settings) {
        File dataFile = getDataFile(SETTINGS_FILE_NAME);

        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(settings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene el archivo de datos para un nombre de archivo dado.
     *
     * @param fileName Nombre del archivo.
     * @return Archivo de datos.
     */
    private File getDataFile(String fileName) {
        return new File(appContext.getFilesDir(), fileName);
    }
}

package com.dev.brain2.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que ayuda a manejar las preferencias de configuración.
 */
public class SettingsPrefHelper {

    private final DataStorage dataStorage;
    private Map<String, Object> settingsCache;

    /**
     * Constructor.
     *
     * @param context Contexto de la aplicación.
     */
    public SettingsPrefHelper(Context context) {
        this.dataStorage = new DataStorage(context);
        this.settingsCache = loadSettingsFromFile();
    }

    /**
     * Guarda un valor entero en las configuraciones.
     *
     * @param key   Clave para almacenar el valor.
     * @param value Valor entero a guardar.
     */
    public void saveInt(String key, int value) {
        settingsCache.put(key, value);
        saveSettingsToFile();
    }

    /**
     * Obtiene un valor entero de las configuraciones.
     *
     * @param key          Clave del valor a obtener.
     * @param defaultValue Valor predeterminado si la clave no existe.
     * @return Valor entero almacenado o el predeterminado.
     */
    public int getInt(String key, int defaultValue) {
        Object value = settingsCache.get(key);
        return value instanceof Number ? ((Number) value).intValue() : defaultValue;
    }

    /**
     * Guarda un valor booleano en las configuraciones.
     *
     * @param key   Clave para almacenar el valor.
     * @param value Valor booleano a guardar.
     */
    public void saveBoolean(String key, boolean value) {
        settingsCache.put(key, value);
        saveSettingsToFile();
    }

    /**
     * Obtiene un valor booleano de las configuraciones.
     *
     * @param key          Clave del valor a obtener.
     * @param defaultValue Valor predeterminado si la clave no existe.
     * @return Valor booleano almacenado o el predeterminado.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = settingsCache.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    /**
     * Guarda un valor String en las configuraciones.
     *
     * @param key   Clave para almacenar el valor.
     * @param value Valor String a guardar.
     */
    public void saveString(String key, String value) {
        settingsCache.put(key, value);
        saveSettingsToFile();
    }

    /**
     * Obtiene un valor String de las configuraciones.
     *
     * @param key          Clave del valor a obtener.
     * @param defaultValue Valor predeterminado si la clave no existe.
     * @return Valor String almacenado o el predeterminado.
     */
    public String getString(String key, String defaultValue) {
        Object value = settingsCache.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    /**
     * Limpia todas las configuraciones guardadas.
     */
    public void clear() {
        settingsCache.clear();
        saveSettingsToFile();
    }

    /**
     * Carga las configuraciones desde el archivo JSON.
     *
     * @return Mapa de configuraciones.
     */
    private Map<String, Object> loadSettingsFromFile() {
        Map<String, Object> settings = dataStorage.readSettings();
        return settings != null ? settings : new HashMap<>();
    }

    /**
     * Guarda las configuraciones al archivo JSON.
     */
    private void saveSettingsToFile() {
        dataStorage.writeSettings(settingsCache);
    }
}

package com.dev.brain2.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase que ayuda a manejar las preferencias de configuración.
 */
public class SettingsPrefHelper {

    private static final String SHARED_PREFS = "settings_prefs";
    private final SharedPreferences sharedPreferences;

    /**
     * Constructor.
     *
     * @param context Contexto de la aplicación.
     */
    public SettingsPrefHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    // Guarda un valor entero

    /**
     * Guarda un valor entero en las preferencias.
     *
     * @param key   Clave para almacenar el valor.
     * @param value Valor entero a guardar.
     */
    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // Obtiene un valor entero con valor predeterminado

    /**
     * Obtiene un valor entero de las preferencias.
     *
     * @param key          Clave del valor a obtener.
     * @param defaultValue Valor predeterminado si la clave no existe.
     * @return Valor entero almacenado o el predeterminado.
     */
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    // Sobrecarga: Obtiene un valor entero con valor predeterminado 0

    /**
     * Obtiene un valor entero de las preferencias (predeterminado 0).
     *
     * @param key Clave del valor a obtener.
     * @return Valor entero almacenado o 0 si no existe.
     */
    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    // Guarda un valor booleano

    /**
     * Guarda un valor booleano en las preferencias.
     *
     * @param key   Clave para almacenar el valor.
     * @param value Valor booleano a guardar.
     */
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Obtiene un valor booleano con valor predeterminado

    /**
     * Obtiene un valor booleano de las preferencias.
     *
     * @param key          Clave del valor a obtener.
     * @param defaultValue Valor predeterminado si la clave no existe.
     * @return Valor booleano almacenado o el predeterminado.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Sobrecarga: Obtiene un valor booleano con valor predeterminado false

    /**
     * Obtiene un valor booleano de las preferencias (predeterminado false).
     *
     * @param key Clave del valor a obtener.
     * @return Valor booleano almacenado o false si no existe.
     */
    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    // Guarda un valor String

    /**
     * Guarda un valor String en las preferencias.
     *
     * @param key   Clave para almacenar el valor.
     * @param value Valor String a guardar.
     */
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Obtiene un valor String con valor predeterminado

    /**
     * Obtiene un valor String de las preferencias.
     *
     * @param key          Clave del valor a obtener.
     * @param defaultValue Valor predeterminado si la clave no existe.
     * @return Valor String almacenado o el predeterminado.
     */
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Sobrecarga: Obtiene un valor String con valor predeterminado ""

    /**
     * Obtiene un valor String de las preferencias (predeterminado "").
     *
     * @param key Clave del valor a obtener.
     * @return Valor String almacenado o "" si no existe.
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    // Limpia todas las preferencias guardadas

    /**
     * Limpia todas las preferencias guardadas.
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

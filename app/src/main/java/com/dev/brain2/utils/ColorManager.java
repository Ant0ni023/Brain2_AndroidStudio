package com.dev.brain2.utils;

/**
 * Esta clase maneja los colores disponibles para las carpetas, barras y iconos.
 */
public class ColorManager {

    // Constructor privado para evitar instancias de esta clase
    private ColorManager() {}

    // Colores para las carpetas
    public static final String[] FOLDER_COLOR_NAMES = {
            "Azul",     // Color alternativo
            "Rojo",     // Color alternativo
            "Verde",    // Color alternativo
            "Amarillo", // Color alternativo
            "Naranja",  // Color alternativo
            "Morado",   // Color alternativo
            "Negro",    // Color adicional
            "Café"      // Color adicional
    };

    // Valores hexadecimales de los colores de las carpetas
    public static final String[] FOLDER_COLOR_VALUES = {
            "#1E90FF", // Azul claro
            "#FF0000", // Rojo puro
            "#00FF00", // Verde brillante
            "#FFFF00", // Amarillo brillante
            "#FFA500", // Naranja
            "#800080", // Morado
            "#000000", // Negro
            "#A52A2A"  // Café
    };

    /**
     * Obtiene el valor hexadecimal de un color de carpeta por su índice.
     *
     * @param index Índice del color.
     * @return Valor hexadecimal del color.
     */
    public static String getFolderColorByIndex(int index) {
        // Verifica que el índice sea válido
        if (index >= 0 && index < FOLDER_COLOR_VALUES.length) {
            return FOLDER_COLOR_VALUES[index];
        }
        // Si el índice no es válido, devuelve el primer color (Azul)
        return FOLDER_COLOR_VALUES[0];
    }

    /**
     * Busca el índice de un color de carpeta por su valor hexadecimal.
     *
     * @param color Valor hexadecimal del color.
     * @return Índice del color, -1 si no se encuentra.
     */
    public static int getFolderColorIndex(String color) {
        // Recorre la lista de colores buscando una coincidencia
        for (int i = 0; i < FOLDER_COLOR_VALUES.length; i++) {
            if (FOLDER_COLOR_VALUES[i].equalsIgnoreCase(color)) {
                return i;
            }
        }
        // Si no encuentra el color, devuelve -1
        return -1;
    }

    // Colores para las barras
    public static final String[] BAR_COLOR_NAMES = {
            "Negro",    // Color adicional
            "Azul",     // Color alternativo
            "Rojo",     // Color alternativo
            "Verde",    // Color alternativo
            "Amarillo", // Color alternativo
            "Naranja",  // Color alternativo
            "Morado",   // Color alternativo
            "Café"      // Color adicional
    };

    // Valores hexadecimales de los colores de las barras
    public static final String[] BAR_COLOR_VALUES = {
            "#000000", // Negro
            "#1E90FF", // Azul claro
            "#FF0000", // Rojo puro
            "#00FF00", // Verde brillante
            "#FFFF00", // Amarillo brillante
            "#FFA500", // Naranja
            "#800080", // Morado
            "#A52A2A"  // Café
    };

    /**
     * Obtiene el valor hexadecimal de un color de barra por su índice.
     *
     * @param index Índice del color.
     * @return Valor hexadecimal del color.
     */
    public static String getBarColorByIndex(int index) {
        // Verifica que el índice sea válido
        if (index >= 0 && index < BAR_COLOR_VALUES.length) {
            return BAR_COLOR_VALUES[index];
        }
        // Si el índice no es válido, devuelve el primer color (Negro)
        return BAR_COLOR_VALUES[0];
    }

    /**
     * Busca el índice de un color de barra por su valor hexadecimal.
     *
     * @param color Valor hexadecimal del color.
     * @return Índice del color, -1 si no se encuentra.
     */
    public static int getBarColorIndex(String color) {
        // Recorre la lista de colores buscando una coincidencia
        for (int i = 0; i < BAR_COLOR_VALUES.length; i++) {
            if (BAR_COLOR_VALUES[i].equalsIgnoreCase(color)) {
                return i;
            }
        }
        // Si no encuentra el color, devuelve -1
        return -1;
    }

    // Colores para los iconos
    public static final String[] ICON_COLOR_NAMES = {
            "Blanco",   // Color adicional
            "Negro",    // Color adicional
            "Azul",     // Color alternativo
            "Rojo",     // Color alternativo
            "Verde",    // Color alternativo
            "Amarillo", // Color alternativo
            "Naranja",  // Color alternativo
            "Morado",   // Color alternativo
            "Café"      // Color adicional
    };

    // Valores hexadecimales de los colores de los iconos
    public static final String[] ICON_COLOR_VALUES = {
            "#FFFFFF", // Blanco
            "#000000", // Negro
            "#1E90FF", // Azul claro
            "#FF0000", // Rojo puro
            "#00FF00", // Verde brillante
            "#FFFF00", // Amarillo brillante
            "#FFA500", // Naranja
            "#800080", // Morado
            "#A52A2A"  // Café
    };

    /**
     * Obtiene el valor hexadecimal de un color de icono por su índice.
     *
     * @param index Índice del color.
     * @return Valor hexadecimal del color.
     */
    public static String getIconColorByIndex(int index) {
        // Verifica que el índice sea válido
        if (index >= 0 && index < ICON_COLOR_VALUES.length) {
            return ICON_COLOR_VALUES[index];
        }
        // Si el índice no es válido, devuelve el primer color (Blanco)
        return ICON_COLOR_VALUES[0];
    }

    /**
     * Busca el índice de un color de icono por su valor hexadecimal.
     *
     * @param color Valor hexadecimal del color.
     * @return Índice del color, -1 si no se encuentra.
     */
    public static int getIconColorIndex(String color) {
        // Recorre la lista de colores buscando una coincidencia
        for (int i = 0; i < ICON_COLOR_VALUES.length; i++) {
            if (ICON_COLOR_VALUES[i].equalsIgnoreCase(color)) {
                return i;
            }
        }
        // Si no encuentra el color, devuelve -1
        return -1;
    }

    // Métodos existentes

    // Lista de nombres de colores (original)
    public static final String[] COLOR_NAMES = {
            "Azul",     // Color alternativo
            "Rojo",     // Color alternativo
            "Verde",    // Color alternativo
            "Amarillo", // Color alternativo
            "Naranja",  // Color alternativo
            "Morado"    // Color alternativo
    };

    // Valores hexadecimales de los colores (original)
    public static final String[] COLOR_VALUES = {
            "#1E90FF", // Azul claro
            "#FF0000", // Rojo puro
            "#00FF00", // Verde brillante
            "#FFFF00", // Amarillo brillante
            "#FFA500", // Naranja
            "#800080"  // Morado
    };

    /**
     * Obtiene el valor hexadecimal de un color por su índice.
     *
     * @param index Índice del color.
     * @return Valor hexadecimal del color.
     */
    public static String getColorByIndex(int index) {
        // Verifica que el índice sea válido
        if (index >= 0 && index < COLOR_VALUES.length) {
            return COLOR_VALUES[index];
        }
        // Si el índice no es válido, devuelve el primer color (Azul)
        return COLOR_VALUES[0];
    }

    /**
     * Busca el índice de un color por su valor hexadecimal.
     *
     * @param color Valor hexadecimal del color.
     * @return Índice del color, -1 si no se encuentra.
     */
    public static int getColorIndex(String color) {
        // Recorre la lista de colores buscando una coincidencia
        for (int i = 0; i < COLOR_VALUES.length; i++) {
            if (COLOR_VALUES[i].equalsIgnoreCase(color)) {
                return i;
            }
        }
        // Si no encuentra el color, devuelve -1
        return -1;
    }

}

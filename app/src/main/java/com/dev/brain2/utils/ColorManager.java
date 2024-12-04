package com.dev.brain2.utils;

/**
 * Esta clase maneja los colores disponibles para las carpetas, barras y iconos.
 */
public class ColorManager {

    // Constructor privado para evitar instancias de esta clase
    private ColorManager() {}

    // ====== COLORES PARA LAS CARPETAS ======

    public static final String[] FOLDER_COLOR_NAMES = {
            "Azul",
            "Rojo",
            "Verde",
            "Amarillo",
            "Naranja",
            "Morado",
            "Negro",
            "Café"
    };

    public static final String[] FOLDER_COLOR_VALUES = {
            "#1E90FF", // Azul
            "#FF0000", // Rojo
            "#00FF00", // Verde
            "#FFFF00", // Amarillo
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
        return getColorByIndex(index, FOLDER_COLOR_VALUES);
    }

    /**
     * Busca el índice de un color de carpeta por su valor hexadecimal.
     *
     * @param color Valor hexadecimal del color.
     * @return Índice del color, -1 si no se encuentra.
     */
    public static int getFolderColorIndex(String color) {
        return getColorIndex(color, FOLDER_COLOR_VALUES);
    }

    // ====== COLORES PARA LAS BARRAS ======

    public static final String[] BAR_COLOR_NAMES = {
            "Negro",
            "Azul",
            "Rojo",
            "Verde",
            "Amarillo",
            "Naranja",
            "Morado",
            "Café"
    };

    public static final String[] BAR_COLOR_VALUES = {
            "#000000", // Negro
            "#1E90FF", // Azul
            "#FF0000", // Rojo
            "#00FF00", // Verde
            "#FFFF00", // Amarillo
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
        return getColorByIndex(index, BAR_COLOR_VALUES);
    }

    /**
     * Busca el índice de un color de barra por su valor hexadecimal.
     *
     * @param color Valor hexadecimal del color.
     * @return Índice del color, -1 si no se encuentra.
     */
    public static int getBarColorIndex(String color) {
        return getColorIndex(color, BAR_COLOR_VALUES);
    }

    // ====== COLORES PARA LOS ICONOS ======

    public static final String[] ICON_COLOR_NAMES = {
            "Blanco",
            "Negro",
            "Azul",
            "Rojo",
            "Verde",
            "Amarillo",
            "Naranja",
            "Morado",
            "Café"
    };

    public static final String[] ICON_COLOR_VALUES = {
            "#FFFFFF", // Blanco
            "#000000", // Negro
            "#1E90FF", // Azul
            "#FF0000", // Rojo
            "#00FF00", // Verde
            "#FFFF00", // Amarillo
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
        return getColorByIndex(index, ICON_COLOR_VALUES);
    }

    /**
     * Busca el índice de un color de icono por su valor hexadecimal.
     *
     * @param color Valor hexadecimal del color.
     * @return Índice del color, -1 si no se encuentra.
     */
    public static int getIconColorIndex(String color) {
        return getColorIndex(color, ICON_COLOR_VALUES);
    }

    // ====== MÉTODOS GENÉRICOS PARA COLORES ======

    /**
     * Obtiene el valor hexadecimal de un color por su índice y arreglo de colores.
     *
     * @param index Índice del color.
     * @param colorValues Arreglo de valores de colores.
     * @return Valor hexadecimal del color.
     */
    private static String getColorByIndex(int index, String[] colorValues) {
        if (index >= 0 && index < colorValues.length) {
            return colorValues[index];
        }
        return colorValues[0];
    }

    /**
     * Busca el índice de un color por su valor hexadecimal y arreglo de colores.
     *
     * @param color Valor hexadecimal del color.
     * @param colorValues Arreglo de valores de colores.
     * @return Índice del color, -1 si no se encuentra.
     */
    private static int getColorIndex(String color, String[] colorValues) {
        for (int i = 0; i < colorValues.length; i++) {
            if (colorValues[i].equalsIgnoreCase(color)) {
                return i;
            }
        }
        return -1;
    }
}

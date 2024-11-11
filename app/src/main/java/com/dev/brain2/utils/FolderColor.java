package com.dev.brain2.utils;

// Esta clase maneja los colores disponibles para las carpetas
public class FolderColor {
    // Lista de nombres de colores
    public static final String[] COLOR_NAMES = {
            "Azul",     // Color alternativo
            "Rojo",     // Color alternativo
            "Verde",    // Color alternativo
            "Amarillo", // Color alternativo
            "Naranja",  // Color alternativo
            "Morado"    // Color alternativo
    };

    // Lista de valores hexadecimales de los colores
    public static final String[] COLOR_VALUES = {
            "#1E90FF", // Azul claro
            "#FF0000", // Rojo puro
            "#00FF00", // Verde brillante
            "#FFFF00", // Amarillo brillante
            "#FFA500", // Naranja
            "#800080"  // Morado
    };

    // Constructor privado para evitar instancias de esta clase
    private FolderColor() {}

    // Obtiene el valor hexadecimal de un color por su posición
    public static String getColorByIndex(int index) {
        // Verificamos que el índice sea válido
        if (index >= 0 && index < COLOR_VALUES.length) {
            return COLOR_VALUES[index];
        }
        // Si el índice no es válido, devolvemos el primer color (Azul)
        return COLOR_VALUES[0];
    }

    // Busca la posición de un color por su valor hexadecimal
    public static int getColorIndex(String color) {
        // Recorremos la lista de colores buscando una coincidencia
        for (int i = 0; i < COLOR_VALUES.length; i++) {
            if (COLOR_VALUES[i].equals(color)) {
                return i;
            }
        }
        // Si no encontramos el color, devolvemos -1
        return -1;
    }
}
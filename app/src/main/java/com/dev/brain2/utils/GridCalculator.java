package com.dev.brain2.utils;

import android.content.Context;
import android.util.DisplayMetrics;

// Esta clase se encarga de realizar todos los cálculos relacionados con el grid
public class GridCalculator {
    // Configuración básica del grid
    private static final int COLUMNS = 2;
    private static final int MARGIN_DP = 16;
    private static final int SPACING_DP = 8;

    // Dimensiones calculadas (en píxeles)
    private final int screenWidth;
    private final int itemWidth;
    private final int itemHeight;
    private final int margin;
    private final int spacing;

    public GridCalculator(Context context) {
        // Obtenemos las dimensiones de la pantalla
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;

        // Convertimos DP a píxeles
        float density = metrics.density;
        margin = (int) (MARGIN_DP * density);
        spacing = (int) (SPACING_DP * density);

        // Calculamos el tamaño de cada item
        int availableWidth = screenWidth - (2 * margin) - spacing;
        itemWidth = availableWidth / COLUMNS;
        itemHeight = itemWidth; // Items cuadrados
    }

    // Calcula las coordenadas X,Y para una posición
    public int[] getItemCoordinates(int position) {
        int[] coordinates = new int[2];

        // Calculamos fila y columna
        int row = position / COLUMNS;
        int col = position % COLUMNS;

        // Calculamos X e Y
        coordinates[0] = margin + (col * (itemWidth + spacing));
        coordinates[1] = margin + (row * (itemHeight + spacing));

        return coordinates;
    }

    // Calcula la altura total necesaria para todos los items
    public int calculateTotalHeight(int itemCount) {
        int rows = (int) Math.ceil(itemCount / (float) COLUMNS);
        return (rows * (itemHeight + spacing)) + margin;
    }

    // Getters para las dimensiones
    public int getItemWidth() { return itemWidth; }
    public int getItemHeight() { return itemHeight; }
    public int getMargin() { return margin; }
    public int getSpacing() { return spacing; }
    public int getColumns() { return COLUMNS; }
}
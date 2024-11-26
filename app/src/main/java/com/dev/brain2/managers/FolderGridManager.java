package com.dev.brain2.managers;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.dev.brain2.utils.GridCalculator;

/**
 * LayoutManager que organiza las carpetas en una cuadrícula de 2 columnas
 * y permite desplazarse verticalmente para ver todas las carpetas.
 */
public class FolderGridManager extends RecyclerView.LayoutManager {

    // Clase auxiliar para cálculos
    private final GridCalculator gridCalculator;

    // Valor actual del scroll vertical
    private int currentScrollY = 0;

    /**
     * Constructor que inicializa el GridCalculator.
     *
     * @param context Contexto de la aplicación.
     */
    public FolderGridManager(Context context) {
        gridCalculator = new GridCalculator(context);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // Limpiamos las vistas actuales
        detachAndScrapAttachedViews(recycler);
        if (getItemCount() == 0) return;

        // Colocamos cada carpeta en su posición
        for (int i = 0; i < getItemCount(); i++) {
            // Creamos y medimos la vista
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);

            // Obtenemos las coordenadas para esta posición
            int[] coordinates = gridCalculator.getItemCoordinates(i);

            // Colocamos la vista en su posición, ajustando por el scroll
            layoutDecoratedWithMargins(view,
                    coordinates[0],                    // left
                    coordinates[1] - currentScrollY,   // top (ajustado por scroll)
                    coordinates[0] + gridCalculator.getItemWidth(),  // right
                    coordinates[1] - currentScrollY + gridCalculator.getItemHeight() // bottom
            );
        }
    }

    // Indica que se permite scroll vertical
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    // Maneja el scroll vertical
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        // Calculamos la altura total necesaria
        int totalHeight = gridCalculator.calculateTotalHeight(getItemCount());
        int viewHeight = getAvailableSpace();

        // Calculamos cuánto podemos hacer scroll
        int scrolled;
        if (dy > 0) { // Scroll hacia abajo
            int maxScroll = totalHeight - viewHeight;
            scrolled = Math.min(dy, Math.max(0, maxScroll - currentScrollY));
        } else { // Scroll hacia arriba
            scrolled = Math.max(dy, -currentScrollY);
        }

        // Actualizamos la posición del scroll
        if (scrolled != 0) {
            currentScrollY += scrolled;
            offsetChildrenVertical(-scrolled);
        }

        return scrolled;
    }

    /**
     * Obtiene el espacio disponible para el contenido.
     *
     * @return Espacio vertical disponible.
     */
    private int getAvailableSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
}

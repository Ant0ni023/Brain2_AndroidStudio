package com.dev.brain2;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar carpetas en un RecyclerView.
 * Esta clase actúa como puente entre los datos de las carpetas y su representación visual.
 * Implementa el patrón ViewHolder para optimizar el rendimiento de la lista.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    // Constantes para la selección de colores
    private static final String[] COLOR_NAMES = {"Azul", "Rojo", "Verde", "Amarillo", "Naranja", "Morado"};
    private static final String[] COLOR_VALUES = {"#1E90FF", "#FF0000", "#00FF00", "#FFFF00", "#FFA500", "#800080"};

    // Atributos privados
    private List<Folder> folders;
    private final OnFolderClickListener listener;
    private final Context context;

    /**
     * Interface para manejar eventos de click en las carpetas.
     * Sigue el patrón Observer para notificar a la actividad sobre las interacciones del usuario.
     */
    public interface OnFolderClickListener {
        void onFolderClick(Folder folder);
        void onFolderEdit(Folder folder, int position);
        void onFolderDelete(Folder folder);
    }

    /**
     * Constructor del adaptador.
     *
     * @param context Contexto de la aplicación
     * @param listener Listener para eventos de click
     * @throws IllegalArgumentException si algún parámetro es null
     */
    public FolderAdapter(Context context, OnFolderClickListener listener) {
        if (context == null || listener == null) {
            throw new IllegalArgumentException("Ni el contexto ni el listener pueden ser null");
        }
        this.context = context;
        this.listener = listener;
        this.folders = new ArrayList<>();
    }

    /**
     * Actualiza la lista de carpetas y notifica al RecyclerView.
     *
     * @param newFolders Nueva lista de carpetas
     */
    public void updateFolders(List<Folder> newFolders) {
        this.folders = newFolders != null ? new ArrayList<>(newFolders) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folders.get(position);
        holder.bind(folder);
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    /**
     * ViewHolder que representa visualmente cada carpeta en la lista.
     * Contiene la lógica de interacción y visualización para cada item.
     */
    class FolderViewHolder extends RecyclerView.ViewHolder {
        private final TextView folderName;
        private final TextView imageCount;
        private final View colorIndicator;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista que representa una carpeta
         */
        FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            imageCount = itemView.findViewById(R.id.imageCount);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);

            setupClickListeners();
        }

        /**
         * Configura los listeners de click para la vista de la carpeta.
         */
        private void setupClickListeners() {
            // Click normal para abrir la carpeta
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (isValidPosition(position)) {
                    listener.onFolderClick(folders.get(position));
                }
            });

            // Click largo para mostrar opciones
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (isValidPosition(position)) {
                    showFolderOptionsDialog(folders.get(position), position);
                }
                return true;
            });
        }

        /**
         * Vincula los datos de una carpeta con la vista.
         *
         * @param folder Carpeta a mostrar
         */
        void bind(Folder folder) {
            folderName.setText(folder.getName());
            imageCount.setText(folder.getImageCount() + " imágenes");

            // Configura el indicador de color
            GradientDrawable bgShape = (GradientDrawable) colorIndicator.getBackground();
            bgShape.setColor(android.graphics.Color.parseColor(folder.getColor()));
        }

        /**
         * Muestra un diálogo con opciones para la carpeta.
         */
        private void showFolderOptionsDialog(Folder folder, int position) {
            new AlertDialog.Builder(context)
                    .setTitle("Opciones de carpeta")
                    .setItems(new String[]{"Modificar", "Eliminar"}, (dialog, which) -> {
                        if (which == 0) {
                            showEditFolderDialog(folder, position);
                        } else if (which == 1) {
                            showDeleteConfirmationDialog(folder);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }

        /**
         * Muestra un diálogo para editar una carpeta.
         */
        private void showEditFolderDialog(Folder folder, int position) {
            View dialogView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_create_folder, null);
            setupEditDialogViews(dialogView, folder, position);
        }

        /**
         * Configura las vistas del diálogo de edición.
         */
        private void setupEditDialogViews(View dialogView, Folder folder, int position) {
            EditText folderNameInput = dialogView.findViewById(R.id.folderNameInput);
            Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

            // Configura el spinner de colores
            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_spinner_item,
                    COLOR_NAMES
            );
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorSpinner.setAdapter(colorAdapter);

            // Establece los valores actuales
            folderNameInput.setText(folder.getName());
            int colorPosition = java.util.Arrays.asList(COLOR_VALUES).indexOf(folder.getColor());
            if (colorPosition >= 0) {
                colorSpinner.setSelection(colorPosition);
            }

            showEditDialog(dialogView, folder, position, folderNameInput, colorSpinner);
        }

        /**
         * Muestra el diálogo final de edición.
         */
        private void showEditDialog(View dialogView, Folder folder, int position,
                                    EditText nameInput, Spinner colorSpinner) {
            new AlertDialog.Builder(context)
                    .setTitle("Modificar carpeta")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String newName = nameInput.getText().toString().trim();
                        String newColor = COLOR_VALUES[colorSpinner.getSelectedItemPosition()];

                        if (!newName.isEmpty()) {
                            folder.setName(newName);
                            folder.setColor(newColor);
                            listener.onFolderEdit(folder, position);
                            notifyItemChanged(position);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }

        /**
         * Muestra un diálogo de confirmación para eliminar una carpeta.
         */
        private void showDeleteConfirmationDialog(Folder folder) {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar carpeta")
                    .setMessage("¿Está seguro de que desea eliminar esta carpeta?")
                    .setPositiveButton("Eliminar", (dialog, which) ->
                            listener.onFolderDelete(folder))
                    .setNegativeButton("Cancelar", null)
                    .show();
        }

        /**
         * Verifica si una posición es válida en el RecyclerView.
         */
        private boolean isValidPosition(int position) {
            return position != RecyclerView.NO_POSITION && position < folders.size();
        }
    }
}
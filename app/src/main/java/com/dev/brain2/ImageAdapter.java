package com.dev.brain2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adaptador para mostrar imágenes en un RecyclerView.
 * Esta clase actúa como puente entre los datos de las imágenes y su representación visual.
 * Implementa el patrón ViewHolder para optimizar el rendimiento de la lista.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    // Interfaz para manejar eventos de las imágenes
    public interface ImageActionListener {
        void onImageClick(Image image);
        void onImageDeleted(Image image);
        void onImageMoved(Image image);
        void onImageRenamed(Image image);
    }

    // Atributos privados
    private final List<Image> images;
    private final Context context;
    private final ImageManager imageManager;
    private final Folder currentFolder;
    private final ImageActionListener actionListener;

    /**
     * Constructor del adaptador.
     *
     * @param context Contexto de la aplicación
     * @param images Lista de imágenes a mostrar
     * @param imageManager Gestor de imágenes
     * @param currentFolder Carpeta actual
     * @param actionListener Listener para eventos de imágenes
     */
    public ImageAdapter(Context context, List<Image> images, ImageManager imageManager,
                        Folder currentFolder, ImageActionListener actionListener) {
        validateConstructorParameters(context, images, imageManager, currentFolder, actionListener);

        this.context = context;
        this.images = images;
        this.imageManager = imageManager;
        this.currentFolder = currentFolder;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = images.get(position);
        holder.bind(image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    /**
     * ViewHolder que representa visualmente cada imagen en la lista.
     */
    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView imageName;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageName = itemView.findViewById(R.id.imageName);
            setupClickListeners();
        }

        /**
         * Configura los listeners de click para la vista de la imagen.
         */
        private void setupClickListeners() {
            // Click normal para ver la imagen
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (isValidPosition(position)) {
                    Image image = images.get(position);
                    actionListener.onImageClick(image);
                    openImageViewer(image);
                }
            });

            // Click largo para mostrar opciones
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (isValidPosition(position)) {
                    showOptionsDialog(images.get(position), position);
                }
                return true;
            });
        }

        /**
         * Vincula una imagen con la vista.
         */
        void bind(Image image) {
            // Mostrar la imagen
            Uri imageUri = Uri.parse(image.getUri());
            imageView.setImageURI(imageUri);

            // Mostrar el nombre
            imageName.setText(image.getName());
        }
    }

    /**
     * Muestra el diálogo de opciones para una imagen.
     */
    private void showOptionsDialog(Image image, int position) {
        String[] options = {"Mover a otra carpeta", "Eliminar imagen", "Cambiar nombre de imagen"};

        new AlertDialog.Builder(context)
                .setTitle("Opciones de Imagen")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showMoveImageDialog(image, position);
                            break;
                        case 1:
                            showDeleteConfirmationDialog(image, position);
                            break;
                        case 2:
                            showRenameImageDialog(image, position);
                            break;
                    }
                })
                .show();
    }

    /**
     * Muestra el diálogo para mover una imagen.
     */
    private void showMoveImageDialog(Image image, int position) {
        List<Folder> availableFolders = imageManager.getAvailableFolders(currentFolder);

        if (availableFolders.isEmpty()) {
            showToast("No hay otras carpetas disponibles");
            return;
        }

        String[] folderNames = availableFolders.stream()
                .map(Folder::getName)
                .toArray(String[]::new);

        new AlertDialog.Builder(context)
                .setTitle("Mover imagen a otra carpeta")
                .setItems(folderNames, (dialog, which) -> {
                    Folder targetFolder = availableFolders.get(which);
                    moveImage(image, position, targetFolder);
                })
                .show();
    }

    /**
     * Mueve una imagen a otra carpeta.
     */
    private void moveImage(Image image, int position, Folder targetFolder) {
        if (imageManager.moveImage(image, currentFolder, targetFolder)) {
            images.remove(position);
            notifyItemRemoved(position);
            actionListener.onImageMoved(image);
            showToast("Imagen movida a " + targetFolder.getName());
        } else {
            showToast("Error al mover la imagen");
        }
    }

    /**
     * Muestra el diálogo de confirmación para eliminar una imagen.
     */
    private void showDeleteConfirmationDialog(Image image, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar imagen")
                .setMessage("¿Está seguro de que desea eliminar esta imagen?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteImage(image, position))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina una imagen.
     */
    private void deleteImage(Image image, int position) {
        if (imageManager.deleteImage(image, currentFolder)) {
            images.remove(position);
            notifyItemRemoved(position);
            actionListener.onImageDeleted(image);
            showToast("Imagen eliminada");
        } else {
            showToast("Error al eliminar la imagen");
        }
    }

    /**
     * Muestra el diálogo para renombrar una imagen.
     */
    private void showRenameImageDialog(Image image, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rename_image, null);
        EditText input = dialogView.findViewById(R.id.imageNameInput);
        input.setText(image.getName());

        new AlertDialog.Builder(context)
                .setTitle("Cambiar nombre de imagen")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        renameImage(image, newName, position);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Renombra una imagen.
     */
    private void renameImage(Image image, String newName, int position) {
        try {
            imageManager.renameImage(image, newName, currentFolder);
            notifyItemChanged(position);
            actionListener.onImageRenamed(image);
            showToast("Nombre cambiado a " + newName);
        } catch (IllegalArgumentException e) {
            showToast("Error al cambiar el nombre: " + e.getMessage());
        }
    }

    /**
     * Abre la actividad de visualización de imagen.
     */
    private void openImageViewer(Image image) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra("imageUri", image.getUri());
        context.startActivity(intent);
    }

    /**
     * Verifica si una posición es válida en el RecyclerView.
     */
    private boolean isValidPosition(int position) {
        return position != RecyclerView.NO_POSITION && position < images.size();
    }

    /**
     * Muestra un mensaje Toast.
     */
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Valida los parámetros del constructor.
     */
    private void validateConstructorParameters(Context context, List<Image> images,
                                               ImageManager imageManager, Folder currentFolder, ImageActionListener actionListener) {
        if (context == null) {
            throw new IllegalArgumentException("Context no puede ser null");
        }
        if (images == null) {
            throw new IllegalArgumentException("Lista de imágenes no puede ser null");
        }
        if (imageManager == null) {
            throw new IllegalArgumentException("ImageManager no puede ser null");
        }
        if (currentFolder == null) {
            throw new IllegalArgumentException("Carpeta actual no puede ser null");
        }
        if (actionListener == null) {
            throw new IllegalArgumentException("ImageActionListener no puede ser null");
        }
    }
}
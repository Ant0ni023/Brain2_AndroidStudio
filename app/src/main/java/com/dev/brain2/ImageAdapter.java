package com.dev.brain2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final List<Image> images;
    private final Context context;
    private final FolderManager folderManager;
    private final Folder currentFolder;

    public ImageAdapter(Context context, List<Image> images, FolderManager folderManager, Folder currentFolder) {
        this.context = context;
        this.images = images;
        this.folderManager = folderManager;
        this.currentFolder = currentFolder;
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
        Uri imageUri = Uri.parse(image.getUri());
        holder.imageView.setImageURI(imageUri);

        // Abrir imagen en tamaño completo al tocar
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImageViewerActivity.class);
            intent.putExtra("imageUri", image.getUri()); // Pasar la URI de la imagen como String
            context.startActivity(intent);
        });

        // Mostrar opciones al mantener presionado
        holder.imageView.setOnLongClickListener(v -> {
            showOptionsDialog(image, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    private void showOptionsDialog(Image image, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Opciones de Imagen")
                .setItems(new String[]{"Mover a otra carpeta", "Eliminar imagen", "Cambiar nombre de imagen"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    moveImageToAnotherFolder(image, position);
                                    break;
                                case 1:
                                    deleteImage(position);
                                    break;
                                case 2:
                                    renameImage(image, position);
                                    break;
                            }
                        });
        builder.show();
    }

    private void moveImageToAnotherFolder(Image image, int position) {
        List<Folder> availableFolders = folderManager.getAvailableFolders(currentFolder);
        if (availableFolders.isEmpty()) {
            Toast.makeText(context, "No hay otras carpetas disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mover imagen a otra carpeta");
        String[] folderNames = availableFolders.stream().map(Folder::getName).toArray(String[]::new);

        builder.setItems(folderNames, (dialog, which) -> {
            Folder targetFolder = availableFolders.get(which);
            targetFolder.addImage(image);
            currentFolder.getImages().remove(image);
            folderManager.updateFolder(targetFolder);
            folderManager.updateFolder(currentFolder);
            images.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Imagen movida a " + targetFolder.getName(), Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void deleteImage(int position) {
        Image image = images.get(position);
        images.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(context, "Imagen eliminada", Toast.LENGTH_SHORT).show();

        if (images.isEmpty()) {
            folderManager.deleteFolderIfEmpty(currentFolder);
            Toast.makeText(context, "Carpeta eliminada porque estaba vacía", Toast.LENGTH_SHORT).show();
        }
    }

    private void renameImage(Image image, int position) {
        AlertDialog.Builder renameDialog = new AlertDialog.Builder(context);
        renameDialog.setTitle("Cambiar nombre de imagen");

        final EditText input = new EditText(context);
        input.setHint("Nuevo nombre");
        renameDialog.setView(input);

        renameDialog.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = input.getText().toString();
            if (!newName.isEmpty()) {
                image.setName(newName);
                notifyItemChanged(position);
                Toast.makeText(context, "Nombre cambiado a " + newName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        renameDialog.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        renameDialog.show();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

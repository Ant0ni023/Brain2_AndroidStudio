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

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<Folder> folders;
    private final OnFolderClickListener listener;
    private final Context context;

    public interface OnFolderClickListener {
        void onFolderClick(Folder folder);
        void onFolderEdit(Folder folder, int position);
        void onFolderDelete(Folder folder);
    }

    public FolderAdapter(Context context, OnFolderClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.folders = new ArrayList<>();
    }

    public void updateFolders(List<Folder> newFolders) {
        this.folders = newFolders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        holder.bind(folders.get(position));
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        private final TextView folderName;
        private final TextView imageCount;
        private final View colorIndicator;

        FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            imageCount = itemView.findViewById(R.id.imageCount);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFolderClick(folders.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showFolderOptionsDialog(folders.get(position), position);
                }
                return true;
            });
        }

        void bind(Folder folder) {
            folderName.setText(folder.getName());
            GradientDrawable bgShape = (GradientDrawable) colorIndicator.getBackground();
            bgShape.setColor(android.graphics.Color.parseColor(folder.getColor()));
            imageCount.setText(folder.getImageCount() + " imágenes");
        }

        private void showFolderOptionsDialog(Folder folder, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Opciones de carpeta")
                    .setItems(new String[]{"Modificar", "Eliminar"}, (dialog, which) -> {
                        if (which == 0) {
                            showEditFolderDialog(folder, position);
                        } else if (which == 1) {
                            listener.onFolderDelete(folder);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }

        private void showEditFolderDialog(Folder folder, int position) {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_folder, null);
            EditText folderNameInput = dialogView.findViewById(R.id.folderNameInput);
            Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);

            // Colores
            String[] colorNames = {"Azul", "Rojo", "Verde", "Amarillo", "Naranja", "Morado"};
            String[] colorValues = {"#1E90FF", "#FF0000", "#00FF00", "#FFFF00", "#FFA500", "#800080"};

            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, colorNames);
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorSpinner.setAdapter(colorAdapter);

            folderNameInput.setText(folder.getName());
            int colorPosition = java.util.Arrays.asList(colorValues).indexOf(folder.getColor());
            if (colorPosition >= 0) {
                colorSpinner.setSelection(colorPosition);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Modificar carpeta")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String newName = folderNameInput.getText().toString().trim();
                        String newColor = colorValues[colorSpinner.getSelectedItemPosition()];

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
    }
}

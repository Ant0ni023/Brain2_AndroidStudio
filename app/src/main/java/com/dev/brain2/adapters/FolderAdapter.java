package com.dev.brain2.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dev.brain2.R;
import com.dev.brain2.models.Folder;
import com.dev.brain2.interfaces.OnFolderClickListener;
import java.util.List;

// Adaptador para mostrar la lista de carpetas en un RecyclerView
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    // Variables para mantener los datos y el contexto
    private List<Folder> folders;          // Lista de carpetas a mostrar
    private Context context;               // Contexto de la aplicación
    private OnFolderClickListener listener; // Listener para eventos de clic

    // Constructor del adaptador
    public FolderAdapter(Context context, List<Folder> folders, OnFolderClickListener listener) {
        this.context = context;
        this.folders = folders;
        this.listener = listener;
    }

    // Crea nuevas vistas para los elementos de la lista
    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos la vista de cada elemento usando el layout item_folder
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    // Actualiza el contenido de una vista existente
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        // Obtenemos la carpeta en la posición actual y la vinculamos al ViewHolder
        Folder folder = folders.get(position);
        holder.bind(folder);
    }

    // Devuelve el número total de elementos en la lista
    @Override
    public int getItemCount() {
        return folders.size();
    }

    // ViewHolder que contiene la vista de cada elemento de la lista
    public class FolderViewHolder extends RecyclerView.ViewHolder {
        // Vistas dentro del elemento
        private TextView folderName;   // Nombre de la carpeta
        private TextView imageCount;   // Contador de imágenes
        private ImageView folderIcon;  // Icono de la carpeta

        // Constructor del ViewHolder
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            // Obtenemos referencias a las vistas
            folderName = itemView.findViewById(R.id.folderName);
            imageCount = itemView.findViewById(R.id.imageCount);
            folderIcon = itemView.findViewById(R.id.folderIcon);

            // Configuramos el listener para clics normales
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFolderClick(folders.get(position));
                }
            });

            // Configuramos el listener para clics largos
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFolderLongClick(folders.get(position), position);
                }
                return true;
            });
        }

        // Vincula los datos de una carpeta con las vistas
        public void bind(Folder folder) {
            // Establecemos el nombre de la carpeta
            folderName.setText(folder.getName());

            // Mostramos el número de imágenes
            imageCount.setText(folder.getImageCount() + " imágenes");

            // Aplicamos el color al icono de la carpeta
            int color = Color.parseColor(folder.getColor());
            folderIcon.setColorFilter(color);
        }
    }

    // Método para actualizar la lista de carpetas
    public void updateFolders(List<Folder> newFolders) {
        this.folders = newFolders;
        notifyDataSetChanged();  // Notificamos al RecyclerView que los datos han cambiado
    }
}
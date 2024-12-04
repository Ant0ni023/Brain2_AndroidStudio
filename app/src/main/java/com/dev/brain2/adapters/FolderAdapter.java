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

/**
 * Adaptador para mostrar la lista de carpetas en un RecyclerView.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<Folder> folderList;               // Lista de carpetas a mostrar
    private Context appContext;                    // Contexto de la aplicación
    private OnFolderClickListener clickListener;   // Listener para eventos de clic

    /**
     * Constructor del adaptador.
     *
     * @param context  Contexto de la aplicación.
     * @param folders  Lista de carpetas a mostrar.
     * @param listener Listener para manejar eventos de clic en las carpetas.
     */
    public FolderAdapter(Context context, List<Folder> folders,
                         OnFolderClickListener listener) {
        this.appContext = context;
        this.folderList = folders;
        this.clickListener = listener;
    }

    /**
     * Crea nuevas vistas para los elementos de la lista.
     *
     * @param parent   El ViewGroup padre.
     * @param viewType Tipo de vista.
     * @return Un nuevo FolderViewHolder.
     */
    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(appContext)
                .inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    /**
     * Vincula los datos a la vista en la posición especificada.
     *
     * @param holder   El FolderViewHolder que debe ser actualizado.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        holder.bindFolderData(folder);
    }

    /**
     * Devuelve el número total de elementos en la lista.
     *
     * @return Número de carpetas en la lista.
     */
    @Override
    public int getItemCount() {
        return folderList.size();
    }

    /**
     * ViewHolder que contiene la vista de cada elemento de la lista.
     */
    public class FolderViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private TextView textViewFolderName;   // Nombre de la carpeta
        private TextView textViewImageCount;   // Cantidad de imágenes
        private ImageView imageViewFolderIcon; // Icono de la carpeta

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista del elemento.
         */
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeViews(itemView);
            setListeners();
        }

        /**
         * Inicializa las vistas del ViewHolder.
         *
         * @param itemView La vista del elemento.
         */
        private void initializeViews(View itemView) {
            textViewFolderName = itemView.findViewById(R.id.folderName);
            textViewImageCount = itemView.findViewById(R.id.imageCount);
            imageViewFolderIcon = itemView.findViewById(R.id.folderIcon);
        }

        /**
         * Configura los listeners para los eventos de clic.
         */
        private void setListeners() {
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * Vincula los datos de una carpeta con las vistas.
         *
         * @param folder La carpeta cuyos datos se mostrarán.
         */
        public void bindFolderData(Folder folder) {
            displayFolderName(folder.getName());
            displayImageCount(folder.getImageCount());
            applyFolderIconColor(folder.getColor());
        }

        /**
         * Muestra el nombre de la carpeta.
         *
         * @param folderName Nombre de la carpeta.
         */
        private void displayFolderName(String folderName) {
            textViewFolderName.setText(folderName);
        }

        /**
         * Muestra la cantidad de imágenes en la carpeta.
         *
         * @param imageCount Cantidad de imágenes.
         */
        private void displayImageCount(int imageCount) {
            String imageCountText = imageCount + " imágenes";
            textViewImageCount.setText(imageCountText);
        }

        /**
         * Aplica el color al icono de la carpeta.
         *
         * @param colorHex Código de color en formato hexadecimal.
         */
        private void applyFolderIconColor(String colorHex) {
            int color = Color.parseColor(colorHex);
            imageViewFolderIcon.setColorFilter(color);
        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onFolderClick(folderList.get(position));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onFolderLongClick(folderList.get(position));
                return true;
            }
            return false;
        }
    }

    /**
     * Actualiza la lista de carpetas.
     *
     * @param newFolders Nueva lista de carpetas.
     */
    public void updateFolders(List<Folder> newFolders) {
        this.folderList = newFolders;
        notifyDataSetChanged();
    }
}

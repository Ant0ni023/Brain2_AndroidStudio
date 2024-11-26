package com.dev.brain2.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dev.brain2.R;
import com.dev.brain2.models.Image;
import com.dev.brain2.interfaces.OnImageClickListener;
import java.util.List;

/**
 * Adaptador para mostrar la lista de imágenes en un RecyclerView.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    // Variables para mantener los datos y el contexto
    private List<Image> images;           // Lista de imágenes a mostrar
    private Context context;              // Contexto de la aplicación
    private OnImageClickListener listener; // Listener para eventos de clic

    /**
     * Constructor del adaptador.
     *
     * @param context  Contexto de la aplicación.
     * @param images   Lista de imágenes a mostrar.
     * @param listener Listener para manejar eventos de clic en las imágenes.
     */
    public ImageAdapter(Context context, List<Image> images, OnImageClickListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    /**
     * Crea nuevas vistas para los elementos de la lista.
     *
     * @param parent   El ViewGroup padre.
     * @param viewType Tipo de vista (no usado aquí).
     * @return Un nuevo ImageViewHolder.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos la vista de cada elemento usando el layout item_image
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * Vincula los datos a la vista en la posición especificada.
     *
     * @param holder   El ImageViewHolder que debe ser actualizado.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Obtenemos la imagen en la posición actual y la vinculamos al ViewHolder
        Image image = images.get(position);
        holder.bind(image);
    }

    /**
     * Devuelve el número total de elementos en la lista.
     *
     * @return Número de imágenes en la lista.
     */
    @Override
    public int getItemCount() {
        return images.size();
    }

    /**
     * ViewHolder que contiene la vista de cada elemento de la lista.
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        // Vistas dentro del elemento
        private ImageView imageView;  // Vista para mostrar la imagen
        private TextView imageName;   // Nombre de la imagen

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista del elemento.
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            // Obtenemos referencias a las vistas
            imageView = itemView.findViewById(R.id.imageView);
            imageName = itemView.findViewById(R.id.imageName);

            // Configuramos el listener para clics normales
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onImageClick(images.get(position));
                }
            });

            // Configuramos el listener para clics largos
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onImageLongClick(images.get(position), position);
                }
                return true;
            });
        }

        /**
         * Vincula los datos de una imagen con las vistas.
         *
         * @param image La imagen cuyos datos se mostrarán.
         */
        public void bind(Image image) {
            // Cargamos la imagen en el ImageView
            Uri imageUri = image.getUri();
            imageView.setImageURI(imageUri);

            // Establecemos el nombre de la imagen
            imageName.setText(image.getName());
        }
    }

    /**
     * Método para actualizar la lista de imágenes.
     *
     * @param newImages Nueva lista de imágenes.
     */
    public void updateImages(List<Image> newImages) {
        this.images = newImages;
        notifyDataSetChanged();  // Notificamos al RecyclerView que los datos han cambiado
    }
}

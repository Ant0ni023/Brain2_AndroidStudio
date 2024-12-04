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

    private List<Image> imageList;              // Lista de imágenes a mostrar
    private Context appContext;                 // Contexto de la aplicación
    private OnImageClickListener clickListener; // Listener para eventos de clic

    /**
     * Constructor del adaptador.
     *
     * @param context  Contexto de la aplicación.
     * @param images   Lista de imágenes a mostrar.
     * @param listener Listener para manejar eventos de clic en las imágenes.
     */
    public ImageAdapter(Context context, List<Image> images, OnImageClickListener listener) {
        this.appContext = context;
        this.imageList = images;
        this.clickListener = listener;
    }

    /**
     * Crea nuevas vistas para los elementos de la lista.
     *
     * @param parent   El ViewGroup padre.
     * @param viewType Tipo de vista.
     * @return Un nuevo ImageViewHolder.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(appContext).inflate(R.layout.item_image, parent, false);
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
        Image image = imageList.get(position);
        holder.bindImageData(image);
    }

    /**
     * Devuelve el número total de elementos en la lista.
     *
     * @return Número de imágenes en la lista.
     */
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    /**
     * ViewHolder que contiene la vista de cada elemento de la lista.
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView imageViewItem;     // Vista para mostrar la imagen
        private TextView textViewImageName;  // Nombre de la imagen

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista del elemento.
         */
        public ImageViewHolder(@NonNull View itemView) {
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
            imageViewItem = itemView.findViewById(R.id.imageView);
            textViewImageName = itemView.findViewById(R.id.imageName);
        }

        /**
         * Configura los listeners para los eventos de clic.
         */
        private void setListeners() {
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * Vincula los datos de una imagen con las vistas.
         *
         * @param image La imagen cuyos datos se mostrarán.
         */
        public void bindImageData(Image image) {
            displayImage(image.getUri());
            displayImageName(image.getName());
        }

        /**
         * Muestra la imagen en el ImageView.
         *
         * @param imageUri URI de la imagen a mostrar.
         */
        private void displayImage(Uri imageUri) {
            imageViewItem.setImageURI(imageUri);
        }

        /**
         * Muestra el nombre de la imagen.
         *
         * @param imageName Nombre de la imagen.
         */
        private void displayImageName(String imageName) {
            textViewImageName.setText(imageName);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onImageClick(imageList.get(position));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onImageLongClick(imageList.get(position));
                return true;
            }
            return false;
        }
    }

    /**
     * Actualiza la lista de imágenes.
     *
     * @param newImages Nueva lista de imágenes.
     */
    public void updateImages(List<Image> newImages) {
        this.imageList = newImages;
        notifyDataSetChanged();
    }
}

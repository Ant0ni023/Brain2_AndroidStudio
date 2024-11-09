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


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Image> images;
    private Context context;
    private OnImageClickListener listener;


    public ImageAdapter(Context context, List<Image> images, OnImageClickListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
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


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView imageName;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            imageName = itemView.findViewById(R.id.imageName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onImageClick(images.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onImageLongClick(images.get(position), position);
                }
                return true;
            });
        }

        public void bind(Image image) {
            // Cargar la imagen
            Uri imageUri = image.getUri();
            imageView.setImageURI(imageUri);

            // Mostrar el nombre de la imagen
            imageName.setText(image.getName());
        }
    }


    public void updateImages(List<Image> newImages) {
        this.images = newImages;
        notifyDataSetChanged();
    }
}

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


public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<Folder> folders;
    private Context context;
    private OnFolderClickListener listener;


    public FolderAdapter(Context context, List<Folder> folders, OnFolderClickListener listener) {
        this.context = context;
        this.folders = folders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
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


    public class FolderViewHolder extends RecyclerView.ViewHolder {

        private TextView folderName;
        private TextView imageCount;
        private ImageView folderIcon;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            folderName = itemView.findViewById(R.id.folderName);
            imageCount = itemView.findViewById(R.id.imageCount);
            folderIcon = itemView.findViewById(R.id.folderIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFolderClick(folders.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFolderLongClick(folders.get(position), position);
                }
                return true;
            });
        }

        public void bind(Folder folder) {
            folderName.setText(folder.getName());
            imageCount.setText(folder.getImageCount() + " imágenes");

            // Aplicar el color al ícono de la carpeta
            int color = Color.parseColor(folder.getColor());
            folderIcon.setColorFilter(color);
        }
    }

    public void updateFolders(List<Folder> newFolders) {
        this.folders = newFolders;
        notifyDataSetChanged();
    }
}

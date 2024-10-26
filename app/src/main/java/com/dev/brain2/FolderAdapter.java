package com.dev.brain2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private List<Folder> folders;
    private final OnFolderClickListener listener;

    public interface OnFolderClickListener {
        void onFolderClick(Folder folder);
    }

    public FolderAdapter(OnFolderClickListener listener) {
        this.folders = new ArrayList<>();
        this.listener = listener;
    }

    public void updateFolders(List<Folder> newFolders) {
        this.folders = newFolders;
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
        }

        void bind(Folder folder) {
            folderName.setText(folder.getName());
            // Cambiar el color del indicador
            GradientDrawable bgShape = (GradientDrawable) colorIndicator.getBackground();
            bgShape.setColor(android.graphics.Color.parseColor(folder.getColor()));
            imageCount.setText(itemView.getContext().getString(R.string.image_count, folder.getImageCount()));
        }
    }
}

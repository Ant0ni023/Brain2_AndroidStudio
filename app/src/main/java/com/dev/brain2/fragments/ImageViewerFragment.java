package com.dev.brain2.fragments;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dev.brain2.R;
import com.dev.brain2.databinding.FragmentImageViewerBinding;
import com.dev.brain2.utils.Notifier;

/**
 * Fragmento para visualizar una imagen en pantalla completa.
 */
public class ImageViewerFragment extends Fragment {

    public static final String ARG_IMAGE_URI = "imageUri";

    private FragmentImageViewerBinding binding;

    public ImageViewerFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadImageFromArguments();
    }

    /**
     * Carga y muestra la imagen desde los argumentos.
     */
    private void loadImageFromArguments() {
        if (getArguments() != null) {
            String imageUriString = getArguments().getString(ARG_IMAGE_URI);
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                displayImage(imageUri);
            } else {
                handleImageNotFound();
            }
        } else {
            handleImageNotFound();
        }
    }

    /**
     * Muestra la imagen en el ImageView.
     *
     * @param imageUri URI de la imagen a mostrar.
     */
    private void displayImage(Uri imageUri) {
        binding.fullImageView.setImageURI(imageUri);

        if (binding.fullImageView.getDrawable() == null) {
            Notifier.showError(requireContext(), "No se pudo cargar la imagen");
            binding.fullImageView.setImageResource(R.drawable.ic_error);
        }
    }

    /**
     * Maneja el caso cuando no se encuentra la imagen.
     */
    private void handleImageNotFound() {
        Notifier.showError(requireContext(), "No se encontró la imagen");
        Navigation.findNavController(requireView()).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

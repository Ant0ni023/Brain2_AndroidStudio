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

    // Constante para el argumento de URI de imagen
    public static final String ARG_IMAGE_URI = "imageUri";

    // Binding para el layout del fragmento
    private FragmentImageViewerBinding binding;

    public ImageViewerFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout usando view binding
        binding = FragmentImageViewerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadImageFromArguments(); // Cargar la imagen pasada vía argumentos
    }

    /**
     * Carga y muestra la imagen desde los argumentos.
     */
    private void loadImageFromArguments() {
        if (getArguments() != null) {
            // Obtener el URI de los argumentos
            String image = getArguments().getString(ARG_IMAGE_URI);
            Uri imageUri = Uri.parse(image);

            if (imageUri == null) {
                Notifier.showError(requireContext(), "No se encontró la imagen");
                Navigation.findNavController(requireView()).navigateUp(); // Navegar hacia atrás si no se encuentra la imagen
            } else {
                displayImage(imageUri); // Mostrar la imagen
            }
        } else {
            Notifier.showError(requireContext(), "No se encontró la imagen");
            requireActivity().onBackPressed();
        }
    }

    /**
     * Muestra la imagen en el ImageView.
     *
     * @param imageUri URI de la imagen a mostrar.
     */
    private void displayImage(Uri imageUri) {
        binding.fullImageView.setImageURI(imageUri);

        // Verificar si la imagen se cargó correctamente
        if (binding.fullImageView.getDrawable() == null) {
            Notifier.showError(requireContext(), "No se pudo cargar la imagen");
            binding.fullImageView.setImageResource(R.drawable.ic_error); // Mostrar icono de error
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evitar memory leaks
    }
}

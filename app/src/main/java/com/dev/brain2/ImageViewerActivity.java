package com.dev.brain2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que muestra una imagen en pantalla completa.
 *
 * Responsabilidad única (SRP): Mostrar una imagen en pantalla completa.
 * Esta clase se mantiene simple y enfocada en su única tarea:
 * recibir una URI de imagen y mostrarla.
 */
public class ImageViewerActivity extends AppCompatActivity {

    // Constante para la clave del intent
    private static final String EXTRA_IMAGE_URI = "imageUri";

    // Vista principal
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        initializeView();
        loadImageFromIntent();
    }

    /**
     * Inicializa la vista principal de la actividad.
     */
    private void initializeView() {
        imageView = findViewById(R.id.fullImageView);
    }

    /**
     * Carga la imagen desde el Intent.
     * Si hay algún error, muestra un mensaje y cierra la actividad.
     */
    private void loadImageFromIntent() {
        String imageUriString = getIntent().getStringExtra(EXTRA_IMAGE_URI);

        if (imageUriString == null) {
            showError("No se encontró la imagen");
            finish();
            return;
        }

        displayImage(imageUriString);
    }

    /**
     * Muestra la imagen en el ImageView.
     *
     * @param imageUriString URI de la imagen a mostrar
     */
    private void displayImage(String imageUriString) {
        try {
            Uri imageUri = Uri.parse(imageUriString);
            imageView.setImageURI(imageUri);

            if (imageView.getDrawable() == null) {
                showError("No se pudo cargar la imagen");
                finish();
            }
        } catch (Exception e) {
            showError("Error al mostrar la imagen");
            finish();
        }
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param message Mensaje a mostrar
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
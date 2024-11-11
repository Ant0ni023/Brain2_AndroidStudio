package com.dev.brain2.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.dev.brain2.R;
import com.dev.brain2.utils.Notifier;

// Esta actividad muestra una imagen en pantalla completa
public class ImageViewerActivity extends AppCompatActivity {

    // Constante para obtener la URI de la imagen del Intent
    public static final String EXTRA_IMAGE_URI = "imageUri";

    // Vista que muestra la imagen
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        // Inicializamos la vista
        imageView = findViewById(R.id.fullImageView);

        // Cargamos la imagen desde el Intent
        loadImageFromIntent();
    }

    // Carga y muestra la imagen desde la URI recibida en el Intent
    private void loadImageFromIntent() {
        // Obtenemos la URI de la imagen del Intent
        Uri imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);

        // Verificamos si se recibió la URI correctamente
        if (imageUri == null) {
            Notifier.showError(this, "No se encontró la imagen");
            finish();  // Cerramos la actividad si no hay imagen
        } else {
            displayImage(imageUri);  // Mostramos la imagen
        }
    }

    // Muestra la imagen en el ImageView
    private void displayImage(Uri imageUri) {
        imageView.setImageURI(imageUri);

        // Verificamos si la imagen se cargó correctamente
        if (imageView.getDrawable() == null) {
            Notifier.showError(this, "No se pudo cargar la imagen");
            imageView.setImageResource(R.drawable.ic_error); // Mostramos icono de error
        }
    }
}
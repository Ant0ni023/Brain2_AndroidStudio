package com.dev.brain2.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.brain2.R;


public class ImageViewerActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URI = "imageUri";

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeView();
        loadImageFromIntent();
    }

    private void initializeView() {
        setContentView(R.layout.activity_image_viewer);
        imageView = findViewById(R.id.fullImageView);
    }

    private void loadImageFromIntent() {
        Uri imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);

        if (imageUri == null) {
            showError("No se encontró la imagen");
            finish();
            return;
        }

        displayImage(imageUri);
    }

    private void displayImage(Uri imageUri) {
        try {
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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

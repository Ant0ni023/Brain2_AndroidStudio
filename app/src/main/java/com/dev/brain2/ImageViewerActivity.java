package com.dev.brain2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ImageView imageView = findViewById(R.id.fullImageView);

        // Obtener la URI de la imagen pasada a través del Intent
        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            imageView.setImageURI(imageUri);
        }
    }
}

package com.dev.brain2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePickerActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri selectedImageUri;
    private FolderManager folderManager;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imageView.setImageURI(selectedImageUri);
                } else {
                    finish();
                }
            }
    );

    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    selectedImageUri = saveBitmapToUri(bitmap);
                    imageView.setImageURI(selectedImageUri);
                } else {
                    finish();
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        folderManager = new FolderManager(this);
        imageView = findViewById(R.id.imageView);
        Button confirmButton = findViewById(R.id.confirmButton);

        selectImage();

        confirmButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                askForImageNameAndSave(selectedImageUri); // Llama al método para obtener el nombre y luego abrir la selección de carpeta
            } else {
                Toast.makeText(this, "Por favor, seleccione una imagen primero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        String[] options = {"Tomar foto", "Seleccionar desde galería"};
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar imagen")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                        } else {
                            takePhoto();
                        }
                    } else if (which == 1) {
                        pickImageFromGallery();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoLauncher.launch(intent);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private Uri saveBitmapToUri(Bitmap bitmap) {
        try {
            File imageFile = new File(getCacheDir(), "captured_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void askForImageNameAndSave(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese el nombre de la imagen");

        final EditText input = new EditText(this);
        input.setHint("Nombre de la imagen");
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String imageName = input.getText().toString();
            if (!imageName.isEmpty()) {
                openFolderSelectionDialog(imageUri, imageName); // Pasa el URI y el nombre al diálogo de selección de carpeta
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void openFolderSelectionDialog(Uri imageUri, String imageName) {
        FolderSelectionDialog dialog = new FolderSelectionDialog(this, folderManager, folder -> {
            try {
                File imageFile = FileUtils.saveImageToFolder(this, imageUri, folder.getName());
                folder.addImage(new Image(imageFile.getAbsolutePath(), imageName)); // Asigna el nombre a la imagen
                folderManager.updateFolder(folder);
                Toast.makeText(this, "Imagen guardada en " + folder.getName(), Toast.LENGTH_SHORT).show();
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}

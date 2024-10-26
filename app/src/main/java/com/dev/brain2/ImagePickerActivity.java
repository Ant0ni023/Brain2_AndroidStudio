package com.dev.brain2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePickerActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private Uri selectedImageUri;
    private FolderManager folderManager;

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
                openFolderSelectionDialog(selectedImageUri);
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
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        } else {
                            takePhoto();
                        }
                    } else if (which == 1) {
                        pickImageFromGallery();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> finish())
                .show();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Llamar al método super para mantener la cadena de responsabilidad
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE) {
                selectedImageUri = data.getData();
                imageView.setImageURI(selectedImageUri);
            } else if (requestCode == TAKE_PHOTO) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    selectedImageUri = saveBitmapToUri(bitmap);
                    imageView.setImageURI(selectedImageUri);
                }
            }
        } else {
            finish();
        }
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

    private void openFolderSelectionDialog(Uri imageUri) {
        FolderSelectionDialog dialog = new FolderSelectionDialog(this, folderManager, folder -> {
            try {
                File imageFile = FileUtils.saveImageToFolder(this, imageUri, folder.getName());
                folder.addImage(new Image(imageFile.getAbsolutePath()));
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

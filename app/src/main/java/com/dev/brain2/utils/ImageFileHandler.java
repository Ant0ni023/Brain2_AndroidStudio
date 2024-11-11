package com.dev.brain2.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Esta clase maneja la creación y gestión de archivos de imagen temporales
public class ImageFileHandler {
    private final Context context;

    public ImageFileHandler(Context context) {
        this.context = context;
    }

    // Crea un archivo temporal para guardar una foto
    public Uri createTemporaryImageFile() throws IOException {
        // Creamos el archivo temporal
        File photoFile = createImageFile();

        // Convertimos el archivo a URI usando FileProvider
        return FileProvider.getUriForFile(
                context,
                "com.dev.brain2.fileprovider",
                photoFile
        );
    }

    // Crea un archivo con nombre único basado en la fecha
    private File createImageFile() throws IOException {
        // Creamos un nombre único usando la fecha actual
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Obtenemos el directorio de imágenes de la app
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Creamos el archivo temporal
        return File.createTempFile(
                imageFileName,  // prefijo del nombre
                ".jpg",        // sufijo del nombre
                storageDir     // directorio
        );
    }
}
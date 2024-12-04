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

/**
 * Esta clase maneja la creación y gestión de archivos de imagen temporales.
 */
public class ImageFileHandler {

    private final Context appContext;

    /**
     * Constructor.
     *
     * @param context Contexto de la aplicación.
     */
    public ImageFileHandler(Context context) {
        this.appContext = context.getApplicationContext();
    }

    /**
     * Crea un archivo temporal para guardar una foto y retorna su URI.
     *
     * @return URI del archivo temporal creado.
     * @throws IOException Si ocurre un error al crear el archivo.
     */
    public Uri createTemporaryImageFile() throws IOException {
        File photoFile = createImageFile();
        return getUriForFile(photoFile);
    }

    /**
     * Crea un archivo de imagen con un nombre único basado en la fecha.
     *
     * @return Archivo temporal creado.
     * @throws IOException Si ocurre un error al crear el archivo.
     */
    private File createImageFile() throws IOException {
        String imageFileName = generateUniqueFileName();
        File storageDir = getStorageDirectory();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Genera un nombre de archivo único basado en la fecha y hora actuales.
     *
     * @return Nombre de archivo único.
     */
    private String generateUniqueFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        return "JPEG_" + timeStamp + "_";
    }

    /**
     * Obtiene el directorio de almacenamiento para las imágenes.
     *
     * @return Directorio de almacenamiento.
     */
    private File getStorageDirectory() {
        return appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    /**
     * Obtiene la URI para un archivo dado usando FileProvider.
     *
     * @param file Archivo para el cual se obtendrá la URI.
     * @return URI del archivo.
     */
    private Uri getUriForFile(File file) {
        return FileProvider.getUriForFile(
                appContext,
                "com.dev.brain2.fileprovider",
                file
        );
    }
}

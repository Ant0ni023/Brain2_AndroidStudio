package com.dev.brain2;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.graphics.Bitmap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Clase utilitaria para el manejo de archivos en la aplicación.
 * Esta clase se encarga de operaciones relacionadas con la creación y
 * almacenamiento de archivos de imagen.
 *
 * Responsabilidad única: Gestionar operaciones de archivos de imagen
 */
public class FileUtils {

    // Constantes para el manejo de archivosBrain2
private static final String IMAGE_PREFIX = "image_"; // Prefijo para los nombres de archivos de imagen
private static final String CAPTURE_PREFIX = "captured_image_"; // Prefijo para los nombres de archivos de imágenes capturadas
private static final String IMAGE_EXTENSION = ".jpg"; // Extensión de archivo para las imágenes
private static final int BUFFER_SIZE = 1024; // Tamaño del buffer para la copia de datos entre streams
private static final int IMAGE_QUALITY = 100; // Calidad de la imagen al guardar un Bitmap como archivo JPEG

    /**
     * Crea una carpeta para almacenar imágenes en el almacenamiento externo de la aplicación.
     *
     * @param context Contexto de la aplicación
     * @param folderName Nombre de la carpeta a crear
     * @return File objeto que representa la carpeta creada
     */
    public static File createFolder(Context context, String folderName) {
        // Obtiene el directorio de imágenes de la aplicación
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File newFolder = new File(baseDir, folderName);

        // Crea la carpeta si no existe
        if (!newFolder.exists()) {
            boolean created = newFolder.mkdirs();
            if (!created) {
                // En una aplicación real, aquí deberías manejar el error
                // Por ejemplo, lanzando una excepción o registrando el error
            }
        }

        return newFolder;
    }

    /**
     * Guarda una imagen desde un Uri en una carpeta específica.
     *
     * @param context Contexto de la aplicación
     * @param imageUri Uri de la imagen a guardar
     * @param folderName Nombre de la carpeta donde guardar la imagen
     * @return File objeto que representa el archivo de imagen guardado
     * @throws IOException si ocurre un error durante la operación de archivo
     */
    public static File saveImageToFolder(Context context, Uri imageUri, String folderName)
            throws IOException {
        // Crea la carpeta de destino
        File folder = createFolder(context, folderName);

        // Crea un nuevo archivo con nombre único basado en timestamp
        String fileName = IMAGE_PREFIX + System.currentTimeMillis() + IMAGE_EXTENSION;
        File imageFile = new File(folder, fileName);

        // Usa try-with-resources para asegurar que los streams se cierren correctamente
        try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {

            if (inputStream == null) {
                throw new IOException("No se pudo abrir el stream de entrada");
            }

            // Copia el contenido del archivo
            copyStream(inputStream, outputStream);
        }

        return imageFile;
    }

    /**
     * Guarda un Bitmap como archivo de imagen en una carpeta específica.
     *
     * @param context Contexto de la aplicación
     * @param bitmap Bitmap a guardar
     * @param folderName Nombre de la carpeta donde guardar la imagen
     * @return Uri del archivo guardado, o null si ocurre un error
     */
    public static Uri saveBitmapToFolder(Context context, Bitmap bitmap, String folderName) {
        // Crea la carpeta de destino
        File folder = createFolder(context, folderName);

        // Crea un nuevo archivo con nombre único
        String fileName = CAPTURE_PREFIX + System.currentTimeMillis() + IMAGE_EXTENSION;
        File imageFile = new File(folder, fileName);

        // Guarda el bitmap como archivo JPEG
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream);
            outputStream.flush();
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método auxiliar para copiar datos entre streams.
     *
     * @param in Stream de entrada
     * @param out Stream de salida
     * @throws IOException si ocurre un error durante la copia
     */
    private static void copyStream(InputStream in, FileOutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
}
package com.dev.brain2.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

// Esta clase proporciona métodos estáticos para mostrar diferentes tipos de notificaciones
// al usuario de manera consistente en toda la aplicación
public class Notifier {

    // Muestra un mensaje de error al usuario usando un Toast
    // Parámetros:
    //   context: contexto de la aplicación necesario para mostrar el Toast
    //   message: mensaje de error a mostrar
    public static void showError(Context context, String message) {
        Toast.makeText(
                context,                 // Contexto necesario para el Toast
                "Error: " + message,     // Agregamos el prefijo "Error:" al mensaje
                Toast.LENGTH_SHORT       // Mostramos el mensaje por un tiempo corto
        ).show();
    }

    // Muestra un mensaje informativo al usuario usando un Toast
    // Parámetros:
    //   context: contexto de la aplicación necesario para mostrar el Toast
    //   message: mensaje informativo a mostrar
    public static void showInfo(Context context, String message) {
        Toast.makeText(
                context,                 // Contexto necesario para el Toast
                message,                 // Mensaje a mostrar
                Toast.LENGTH_SHORT       // Mostramos el mensaje por un tiempo corto
        ).show();
    }

    // Muestra un diálogo de confirmación para eliminar elementos
    // Este diálogo muestra dos botones: Eliminar y Cancelar
    // Parámetros:
    //   context: contexto de la aplicación necesario para mostrar el diálogo
    //   message: mensaje de confirmación a mostrar
    //   onConfirm: acción a ejecutar si el usuario confirma la eliminación
    public static void showDeleteConfirmation(Context context,
                                              String message,
                                              Runnable onConfirm) {
        // Construimos y configuramos el diálogo
        new AlertDialog.Builder(context)
                .setTitle("Confirmación")              // Título del diálogo
                .setMessage(message)                   // Mensaje de confirmación
                .setPositiveButton(                    // Botón de confirmación
                        "Eliminar",                        // Texto del botón
                        (dialog, which) -> {
                            onConfirm.run();              // Ejecutamos la acción de eliminar
                            showInfo(context,             // Mostramos mensaje de éxito
                                    "Elemento eliminado");
                        }
                )
                .setNegativeButton(                   // Botón para cancelar
                        "Cancelar",                       // Texto del botón
                        null                              // No hacemos nada al cancelar
                )
                .show();                              // Mostramos el diálogo
    }
}
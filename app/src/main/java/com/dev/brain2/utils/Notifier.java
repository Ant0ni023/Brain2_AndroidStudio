package com.dev.brain2.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/**
 * Esta clase proporciona métodos estáticos para mostrar diferentes tipos de notificaciones
 * al usuario de manera consistente en toda la aplicación.
 */
public class Notifier {

    /**
     * Muestra un mensaje de error al usuario usando un Toast.
     *
     * @param context Contexto de la aplicación necesario para mostrar el Toast.
     * @param message Mensaje de error a mostrar.
     */
    public static void showError(Context context, String message) {
        showToast(context, "Error: " + message);
    }

    /**
     * Muestra un mensaje informativo al usuario usando un Toast.
     *
     * @param context Contexto de la aplicación necesario para mostrar el Toast.
     * @param message Mensaje informativo a mostrar.
     */
    public static void showInfo(Context context, String message) {
        showToast(context, message);
    }

    /**
     * Muestra un Toast con el mensaje proporcionado.
     *
     * @param context Contexto de la aplicación.
     * @param message Mensaje a mostrar.
     */
    private static void showToast(Context context, String message) {
        Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Muestra un diálogo de confirmación para eliminar elementos.
     * Este diálogo muestra dos botones: Eliminar y Cancelar.
     *
     * @param context   Contexto de la aplicación necesario para mostrar el diálogo.
     * @param message   Mensaje de confirmación a mostrar.
     * @param onConfirm Acción a ejecutar si el usuario confirma la eliminación.
     */
    public static void showDeleteConfirmation(Context context,
                                              String message,
                                              Runnable onConfirm) {
        showConfirmationDialog(context, "Confirmación", message, "Eliminar", onConfirm);
    }

    /**
     * Muestra un diálogo de confirmación genérico.
     *
     * @param context      Contexto de la aplicación.
     * @param title        Título del diálogo.
     * @param message      Mensaje a mostrar.
     * @param positiveText Texto del botón positivo.
     * @param onConfirm    Acción a ejecutar al confirmar.
     */
    private static void showConfirmationDialog(Context context,
                                               String title,
                                               String message,
                                               String positiveText,
                                               Runnable onConfirm)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        positiveText,
                        (dialog, which) -> onConfirm.run()
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

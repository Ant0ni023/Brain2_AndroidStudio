package com.dev.brain2;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Clase utilitaria para manejar los permisos de la aplicación.
 *
 * Responsabilidad única (SRP): Gestionar la verificación y solicitud de permisos
 * necesarios para el funcionamiento de la aplicación.
 */
public class PermissionManager {

    // Constantes para los permisos requeridos
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    /**
     * Verifica si todos los permisos necesarios están concedidos.
     *
     * @param activity Activity desde la que se verifican los permisos
     * @return true si todos los permisos están concedidos, false en caso contrario
     * @throws IllegalArgumentException si activity es null
     */
    public static boolean hasPermissions(@NonNull Activity activity) {
        validateActivity(activity);

        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Solicita los permisos necesarios.
     *
     * @param activity Activity desde la que se solicitan los permisos
     * @param requestCode Código de solicitud para identificar la respuesta
     * @throws IllegalArgumentException si activity es null o requestCode es negativo
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode) {
        validateActivity(activity);
        validateRequestCode(requestCode);

        ActivityCompat.requestPermissions(activity, PERMISSIONS, requestCode);
    }

    /**
     * Valida que la activity no sea null.
     *
     * @param activity Activity a validar
     * @throws IllegalArgumentException si activity es null
     */
    private static void validateActivity(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity no puede ser null");
        }
    }

    /**
     * Valida que el código de solicitud sea válido.
     *
     * @param requestCode Código de solicitud a validar
     * @throws IllegalArgumentException si requestCode es negativo
     */
    private static void validateRequestCode(int requestCode) {
        if (requestCode < 0) {
            throw new IllegalArgumentException("RequestCode no puede ser negativo");
        }
    }
}
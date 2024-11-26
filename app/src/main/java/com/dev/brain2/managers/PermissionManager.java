package com.dev.brain2.managers;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Esta clase se encarga de manejar los permisos de la aplicación.
 */
public class PermissionManager {
    // Contexto necesario para verificar permisos
    private final Context context;

    // Callback para informar sobre el resultado de la solicitud de permisos
    private PermissionCallback callback;

    /**
     * Constructor.
     *
     * @param context Contexto de la aplicación.
     */
    public PermissionManager(Context context) {
        this.context = context;
    }

    /**
     * Interfaz para manejar las respuestas de los permisos.
     */
    public interface PermissionCallback {
        /**
         * Se llama cuando el usuario acepta el permiso.
         */
        void onPermissionGranted();

        /**
         * Se llama cuando el usuario rechaza el permiso.
         */
        void onPermissionDenied();
    }

    /**
     * Solicita un permiso específico.
     *
     * @param activity    Actividad desde la cual se solicita el permiso.
     * @param permission  Permiso a solicitar.
     * @param requestCode Código de solicitud.
     * @param callback    Callback para manejar la respuesta.
     */
    public void requestPermission(AppCompatActivity activity,
                                  String permission,
                                  int requestCode,
                                  PermissionCallback callback) {
        // Guardamos el callback para usarlo después
        this.callback = callback;

        // Verificamos si ya tenemos el permiso
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            // Si ya tenemos el permiso, notificamos inmediatamente
            callback.onPermissionGranted();
        } else {
            // Si no tenemos el permiso, lo solicitamos
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{permission},
                    requestCode
            );
        }
    }

    /**
     * Maneja el resultado de la solicitud de permisos.
     *
     * @param requestCode  Código de solicitud.
     * @param grantResults Resultados de los permisos solicitados.
     */
    public void handlePermissionsResult(int requestCode, int[] grantResults) {
        // Verificamos si se concedió el permiso
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Si se concedió el permiso, notificamos el éxito
            if (callback != null) {
                callback.onPermissionGranted();
            }
        } else {
            // Si se denegó el permiso, notificamos el fallo
            if (callback != null) {
                callback.onPermissionDenied();
            }
        }
    }
}

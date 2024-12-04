package com.dev.brain2.managers;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

/**
 * Esta clase se encarga de manejar los permisos de la aplicación.
 */
public class PermissionManager {

    private final Context appContext;

    /**
     * Constructor.
     *
     * @param context Contexto de la aplicación.
     */
    public PermissionManager(Context context) {
        this.appContext = context;
    }

    /**
     * Verifica si un permiso específico está concedido.
     *
     * @param permission Permiso a verificar.
     * @return Verdadero si el permiso está concedido, falso de lo contrario.
     */
    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED;
    }
}

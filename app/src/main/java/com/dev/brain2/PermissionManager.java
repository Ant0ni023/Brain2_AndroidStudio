package com.dev.brain2;

import android.app.Activity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

public class PermissionManager {

    public static final String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static boolean hasPermissions(Activity activity) {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS, requestCode);
    }
}

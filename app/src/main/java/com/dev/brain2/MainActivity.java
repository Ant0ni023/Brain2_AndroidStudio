package com.dev.brain2;

import static com.dev.brain2.fragments.SettingsFragment.KEY_BAR_COLOR;
import static com.dev.brain2.fragments.SettingsFragment.KEY_ICON_COLOR;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.dev.brain2.utils.ColorManager;
import com.dev.brain2.utils.SettingsPrefHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dev.brain2.databinding.ActivityDashboardBinding;

/**
 * Actividad principal que maneja la navegación y configuración global de la aplicación.
 */
public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityDashboardBinding binding;
    SettingsPrefHelper settingsPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        settingsPrefHelper = new SettingsPrefHelper(this);

        // Establecer valores predeterminados
        int defaultBarColorPosition = 0; // Negro
        int defaultIconColorPosition = 0; // Blanco

        // Obtener los colores guardados o usar los predeterminados
        int barColorPosition = settingsPrefHelper.getInt(KEY_BAR_COLOR, defaultBarColorPosition);
        int iconColorPosition = settingsPrefHelper.getInt(KEY_ICON_COLOR, defaultIconColorPosition);

        // Aplicar los colores
        changeBottomNavigationIconColor(ColorManager.getIconColorByIndex(iconColorPosition));
        changeSystemBarsColor(ColorManager.getBarColorByIndex(barColorPosition));

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_add_photo, R.id.nav_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_dashboard);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Cambia el color de las barras de sistema (barra de estado y navegación).
     *
     * @param colorHex Código hexadecimal del color a aplicar.
     */
    public void changeSystemBarsColor(String colorHex) {
        // Parsear el color desde la cadena hexadecimal
        int color = ContextCompat.getColor(this, android.R.color.transparent);
        try {
            color = android.graphics.Color.parseColor(colorHex);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // Cambiar el color de la barra de estado y navegación
        if (getWindow() != null) {
            getWindow().setStatusBarColor(color);
            getWindow().setNavigationBarColor(color);
        }
        binding.navView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }

    /**
     * Cambia el color de los iconos y texto en la barra de navegación inferior.
     *
     * @param colorHex Código hexadecimal del color a aplicar.
     */
    public void changeBottomNavigationIconColor(String colorHex) {
        int color = Color.parseColor(colorHex);
        binding.navView.setItemIconTintList(android.content.res.ColorStateList.valueOf(color));
        binding.navView.setItemTextColor(android.content.res.ColorStateList.valueOf(color));
    }
}

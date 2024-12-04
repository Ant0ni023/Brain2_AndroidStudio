package com.dev.brain2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.dev.brain2.fragments.SettingsFragment;
import com.dev.brain2.utils.ColorManager;
import com.dev.brain2.utils.SettingsPrefHelper;

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
    private SettingsPrefHelper settingsPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeBinding();
        initializeSettingsHelper();
        applySavedColors();
        setupNavigation();
    }

    /**
     * Inicializa el objeto de enlace de vistas (binding).
     */
    private void initializeBinding() {
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /**
     * Inicializa el helper para las preferencias de configuración.
     */
    private void initializeSettingsHelper() {
        settingsPrefHelper = new SettingsPrefHelper(this);
    }

    /**
     * Aplica los colores guardados en las preferencias o los valores predeterminados.
     */
    private void applySavedColors() {
        int defaultBarColorPosition = 0;  // Negro
        int defaultIconColorPosition = 0; // Blanco

        int barColorPosition = settingsPrefHelper.getInt(
                SettingsFragment.KEY_BAR_COLOR, defaultBarColorPosition);
        int iconColorPosition = settingsPrefHelper.getInt(
                SettingsFragment.KEY_ICON_COLOR, defaultIconColorPosition);

        String barColorHex = ColorManager.getBarColorByIndex(barColorPosition);
        String iconColorHex = ColorManager.getIconColorByIndex(iconColorPosition);

        changeSystemBarsColor(barColorHex);
        changeBottomNavigationIconColor(iconColorHex);
    }

    /**
     * Configura la navegación entre fragmentos y el controlador de navegación.
     */
    private void setupNavigation() {
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_add_photo, R.id.nav_settings)
                .build();

        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_activity_dashboard);

        NavigationUI.setupActionBarWithNavController(this,
                navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigateUpOrFinish();
    }

    /**
     * Maneja la acción de navegación "hacia arriba".
     *
     * @return Verdadero si pudo navegar hacia arriba, falso de lo contrario.
     */
    private boolean navigateUpOrFinish() {
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_activity_dashboard);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Cambia el color de las barras de sistema (barra de estado y navegación).
     *
     * @param colorHex Código hexadecimal del color a aplicar.
     */
    public void changeSystemBarsColor(String colorHex) {
        int color = parseColorOrDefault(colorHex, android.R.color.transparent);

        setStatusBarAndNavigationBarColor(color);
        setNavigationViewBackgroundColor(color);
        setActionBarColor(color);
    }

    /**
     * Parsea un color hexadecimal a entero, devolviendo un color predeterminado si falla.
     *
     * @param colorHex         Código hexadecimal del color.
     * @param defaultColorResId ID de recurso del color predeterminado.
     * @return Color en formato entero.
     */
    private int parseColorOrDefault(String colorHex, int defaultColorResId) {
        int color = ContextCompat.getColor(this, defaultColorResId);
        try {
            color = Color.parseColor(colorHex);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return color;
    }

    /**
     * Establece el color de la barra de estado y de navegación.
     *
     * @param color Color a aplicar.
     */
    private void setStatusBarAndNavigationBarColor(int color) {
        if (getWindow() != null) {
            getWindow().setStatusBarColor(color);
            getWindow().setNavigationBarColor(color);
        }
    }

    /**
     * Establece el color de fondo de la vista de navegación inferior.
     *
     * @param color Color a aplicar.
     */
    private void setNavigationViewBackgroundColor(int color) {
        binding.navView.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color));
    }

    /**
     * Establece el color de fondo de la ActionBar.
     *
     * @param color Color a aplicar.
     */
    private void setActionBarColor(int color) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(color));
        }
    }

    /**
     * Cambia el color de los iconos y texto en la barra de navegación inferior.
     *
     * @param colorHex Código hexadecimal del color a aplicar.
     */
    public void changeBottomNavigationIconColor(String colorHex) {
        int color = parseColorOrDefault(colorHex, android.R.color.black);

        setNavigationViewIconColor(color);
        setNavigationViewTextColor(color);
    }

    /**
     * Establece el color de los iconos en la vista de navegación inferior.
     *
     * @param color Color a aplicar.
     */
    private void setNavigationViewIconColor(int color) {
        binding.navView.setItemIconTintList(
                android.content.res.ColorStateList.valueOf(color));
    }

    /**
     * Establece el color del texto en la vista de navegación inferior.
     *
     * @param color Color a aplicar.
     */
    private void setNavigationViewTextColor(int color) {
        binding.navView.setItemTextColor(
                android.content.res.ColorStateList.valueOf(color));
    }
}

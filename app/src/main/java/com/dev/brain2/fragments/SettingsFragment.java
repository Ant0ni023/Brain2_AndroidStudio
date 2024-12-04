package com.dev.brain2.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dev.brain2.MainActivity;
import com.dev.brain2.databinding.FragmentSettingsBinding;
import com.dev.brain2.utils.ColorManager;
import com.dev.brain2.utils.SettingsPrefHelper;

/**
 * Fragmento para las configuraciones de la aplicación.
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsPrefHelper settingsPrefHelper;

    // Claves para las preferencias
    public static final String KEY_BAR_COLOR = "bar_color";
    public static final String KEY_ICON_COLOR = "icon_color";
    public static final String KEY_LAST_OPENED = "last_opened";

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeSettingsPrefHelper();
        setupSpinners();
        loadPreferences();
        setupListeners();
    }

    /**
     * Inicializa el SettingsPrefHelper.
     */
    private void initializeSettingsPrefHelper() {
        settingsPrefHelper = new SettingsPrefHelper(requireContext());
    }

    /**
     * Configura los adaptadores para los spinners.
     */
    private void setupSpinners() {
        ArrayAdapter<String> barColorAdapter = new ArrayAdapter<>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                ColorManager.BAR_COLOR_NAMES
        );
        barColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBarColors.setAdapter(barColorAdapter);

        ArrayAdapter<String> iconColorAdapter = new ArrayAdapter<>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                ColorManager.ICON_COLOR_NAMES
        );
        iconColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerIconColors.setAdapter(iconColorAdapter);
    }

    /**
     * Carga las preferencias guardadas y actualiza la interfaz.
     */
    private void loadPreferences() {
        int defaultBarColorPosition = 0; // Negro
        int defaultIconColorPosition = 0; // Blanco

        int barColorPosition = settingsPrefHelper.getInt(KEY_BAR_COLOR, defaultBarColorPosition);
        int iconColorPosition = settingsPrefHelper.getInt(KEY_ICON_COLOR, defaultIconColorPosition);
        boolean lastOpenedFolderEnabled = settingsPrefHelper.getBoolean(KEY_LAST_OPENED, false);

        binding.spinnerBarColors.setSelection(barColorPosition);
        binding.spinnerIconColors.setSelection(iconColorPosition);
        binding.switchEnableLastFolder.setChecked(lastOpenedFolderEnabled);
    }

    /**
     * Configura los listeners para los elementos de la interfaz.
     */
    private void setupListeners() {
        binding.spinnerBarColors.setOnItemSelectedListener(new BarColorItemSelectedListener());
        binding.spinnerIconColors.setOnItemSelectedListener(new IconColorItemSelectedListener());
        binding.switchEnableLastFolder.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsPrefHelper.saveBoolean(KEY_LAST_OPENED, isChecked));
    }

    /**
     * Listener para el spinner de colores de la barra.
     */
    private class BarColorItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int position, long id) {
            settingsPrefHelper.saveInt(KEY_BAR_COLOR, position);
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.changeSystemBarsColor(ColorManager.getBarColorByIndex(position));
            }
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> adapterView) {

        }
    }

    /**
     * Listener para el spinner de colores de los iconos.
     */
    private class IconColorItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int position, long id) {
            settingsPrefHelper.saveInt(KEY_ICON_COLOR, position);
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.changeBottomNavigationIconColor(ColorManager.getIconColorByIndex(position));
            }
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> adapterView) {

        }
    }
}

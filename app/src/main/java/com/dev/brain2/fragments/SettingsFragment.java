package com.dev.brain2.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
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
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inicializar SettingsPrefHelper
        settingsPrefHelper = new SettingsPrefHelper(requireContext());

        // Configurar los adaptadores para los spinners
        // Adaptador para el spinner de colores de la barra
        ArrayAdapter<String> barColorAdapter = new ArrayAdapter<>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                ColorManager.BAR_COLOR_NAMES
        );
        barColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBarColors.setAdapter(barColorAdapter);

        // Adaptador para el spinner de colores de los iconos
        ArrayAdapter<String> iconColorAdapter = new ArrayAdapter<>(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                ColorManager.ICON_COLOR_NAMES
        );
        iconColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerIconColors.setAdapter(iconColorAdapter);

        // Cargar las preferencias guardadas
        loadPreferences();

        // Configurar el listener para el spinner de colores de la barra
        binding.spinnerBarColors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                settingsPrefHelper.saveInt(KEY_BAR_COLOR, position);
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.changeSystemBarsColor(ColorManager.getBarColorByIndex(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No se seleccionó nada
            }
        });

        // Configurar el listener para el spinner de colores de los iconos
        binding.spinnerIconColors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                settingsPrefHelper.saveInt(KEY_ICON_COLOR, position);
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.changeBottomNavigationIconColor(ColorManager.getIconColorByIndex(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No se seleccionó nada
            }
        });

        // Configurar el listener para el switch de última carpeta abierta
        binding.switchEnableLastFolder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsPrefHelper.saveBoolean(KEY_LAST_OPENED, isChecked);
            }
        });
    }

    /**
     * Carga las preferencias guardadas y actualiza la interfaz.
     */
    private void loadPreferences() {
        int defaultBarColorPosition = 0; // Negro
        int defaultIconColorPosition = 0; // Blanco

        int barColorPosition = settingsPrefHelper.getInt(KEY_BAR_COLOR, defaultBarColorPosition);
        int iconColorPosition = settingsPrefHelper.getInt(KEY_ICON_COLOR, defaultIconColorPosition);
        boolean folder = settingsPrefHelper.getBoolean(KEY_LAST_OPENED, false); // Predeterminado a falso

        binding.spinnerBarColors.setSelection(barColorPosition);
        binding.spinnerIconColors.setSelection(iconColorPosition);
        binding.switchEnableLastFolder.setChecked(folder);
    }
}

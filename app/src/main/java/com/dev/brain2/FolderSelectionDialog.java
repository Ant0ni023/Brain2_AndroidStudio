package com.dev.brain2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona los diálogos de selección y creación de carpetas.
 * Esta clase sigue el patrón Builder para la construcción de diálogos
 * y el patrón Observer para notificar las selecciones del usuario.
 */
public class FolderSelectionDialog {

    // Constantes para la configuración de colores
    private static final String[] COLOR_NAMES = {"Azul", "Rojo", "Verde", "Amarillo", "Naranja", "Morado"};
    private static final String[] COLOR_VALUES = {"#1E90FF", "#FF0000", "#00FF00", "#FFFF00", "#FFA500", "#800080"};

    // Dependencias principales
    private final Context context;
    private final FolderManager folderManager;
    private final OnFolderSelectedListener listener;

    /**
     * Interface para notificar cuando se selecciona una carpeta.
     * Sigue el patrón Observer.
     */
    public interface OnFolderSelectedListener {
        /**
         * Se llama cuando el usuario selecciona o crea una carpeta.
         * @param folder La carpeta seleccionada o creada
         */
        void onFolderSelected(Folder folder);
    }

    /**
     * Constructor del diálogo de selección de carpetas.
     *
     * @param context Contexto de la aplicación
     * @param folderManager Gestor de carpetas
     * @param listener Listener para eventos de selección
     * @throws IllegalArgumentException si algún parámetro es null
     */
    public FolderSelectionDialog(Context context, FolderManager folderManager, OnFolderSelectedListener listener) {
        validateConstructorParameters(context, folderManager, listener);

        this.context = context;
        this.folderManager = folderManager;
        this.listener = listener;
    }

    /**
     * Muestra el diálogo de selección de carpetas.
     * Si no hay carpetas, muestra directamente el diálogo de creación.
     */
    public void show() {
        List<String> folderList = new ArrayList<>(folderManager.getFolderNames());

        if (folderList.isEmpty()) {
            showCreateFolderDialog();
        } else {
            showFolderSelectionDialog(folderList);
        }
    }

    /**
     * Muestra el diálogo con la lista de carpetas existentes.
     *
     * @param folderList Lista de nombres de carpetas
     */
    private void showFolderSelectionDialog(List<String> folderList) {
        // Añade la opción de crear nueva carpeta al final de la lista
        folderList.add("Crear nueva carpeta");

        ArrayAdapter<String> adapter = createFolderListAdapter(folderList);

        new AlertDialog.Builder(context)
                .setTitle("Seleccionar Carpeta")
                .setAdapter(adapter, (dialog, which) -> {
                    handleFolderSelection(folderList, which);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Crea el adaptador para la lista de carpetas.
     *
     * @param folderList Lista de nombres de carpetas
     * @return ArrayAdapter configurado
     */
    private ArrayAdapter<String> createFolderListAdapter(List<String> folderList) {
        return new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                folderList
        );
    }

    /**
     * Maneja la selección de una carpeta de la lista.
     *
     * @param folderList Lista de nombres de carpetas
     * @param position Posición seleccionada
     */
    private void handleFolderSelection(List<String> folderList, int position) {
        String selectedFolderName = folderList.get(position);

        if (selectedFolderName.equals("Crear nueva carpeta")) {
            showCreateFolderDialog();
        } else {
            folderManager.getFolderByName(selectedFolderName)
                    .ifPresent(folder -> showEditFolderDialog(folder));
        }
    }

    /**
     * Muestra el diálogo para crear una nueva carpeta.
     */
    private void showCreateFolderDialog() {
        View dialogView = createDialogView();
        EditText input = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = setupColorSpinner(dialogView);

        new AlertDialog.Builder(context)
                .setTitle("Crear Carpeta")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    createNewFolder(input, colorSpinner);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Muestra el diálogo para editar una carpeta existente.
     *
     * @param folder Carpeta a editar
     */
    private void showEditFolderDialog(Folder folder) {
        View dialogView = createDialogView();
        EditText input = dialogView.findViewById(R.id.folderNameInput);
        Spinner colorSpinner = setupColorSpinner(dialogView);

        // Establece los valores actuales
        input.setText(folder.getName());
        setInitialColor(colorSpinner, folder.getColor());

        new AlertDialog.Builder(context)
                .setTitle("Modificar Carpeta")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    updateExistingFolder(folder, input, colorSpinner);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Crea la vista del diálogo.
     *
     * @return Vista inflada del diálogo
     */
    private View createDialogView() {
        return LayoutInflater.from(context).inflate(R.layout.dialog_create_folder, null);
    }

    /**
     * Configura el Spinner de selección de colores.
     *
     * @param dialogView Vista del diálogo
     * @return Spinner configurado
     */
    private Spinner setupColorSpinner(View dialogView) {
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                COLOR_NAMES
        );
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);
        return colorSpinner;
    }

    /**
     * Establece el color inicial en el Spinner.
     *
     * @param colorSpinner Spinner de colores
     * @param currentColor Color actual de la carpeta
     */
    private void setInitialColor(Spinner colorSpinner, String currentColor) {
        int colorPosition = java.util.Arrays.asList(COLOR_VALUES).indexOf(currentColor);
        if (colorPosition >= 0) {
            colorSpinner.setSelection(colorPosition);
        }
    }

    /**
     * Crea una nueva carpeta con los valores proporcionados.
     *
     * @param nameInput Input del nombre
     * @param colorSpinner Spinner de color
     */
    private void createNewFolder(EditText nameInput, Spinner colorSpinner) {
        String folderName = nameInput.getText().toString().trim();
        if (!folderName.isEmpty()) {
            int selectedColorPosition = colorSpinner.getSelectedItemPosition();
            String selectedColor = COLOR_VALUES[selectedColorPosition];

            folderManager.addFolder(folderName, selectedColor);
            Folder newFolder = folderManager.getFolderByName(folderName).orElse(null);

            if (newFolder != null) {
                listener.onFolderSelected(newFolder);
            }
        }
    }

    /**
     * Actualiza una carpeta existente con los nuevos valores.
     *
     * @param folder Carpeta a actualizar
     * @param nameInput Input del nombre
     * @param colorSpinner Spinner de color
     */
    private void updateExistingFolder(Folder folder, EditText nameInput, Spinner colorSpinner) {
        String folderName = nameInput.getText().toString().trim();
        if (!folderName.isEmpty()) {
            int selectedColorPosition = colorSpinner.getSelectedItemPosition();
            String selectedColor = COLOR_VALUES[selectedColorPosition];

            folder.setName(folderName);
            folder.setColor(selectedColor);
            folderManager.updateFolder(folder);
            listener.onFolderSelected(folder);
        }
    }

    /**
     * Valida los parámetros del constructor.
     */
    private void validateConstructorParameters(Context context, FolderManager folderManager,
                                               OnFolderSelectedListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("El contexto no puede ser null");
        }
        if (folderManager == null) {
            throw new IllegalArgumentException("El FolderManager no puede ser null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("El OnFolderSelectedListener no puede ser null");
        }
    }
}
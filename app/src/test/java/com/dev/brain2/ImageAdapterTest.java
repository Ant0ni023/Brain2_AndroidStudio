package com.dev.brain2;

import android.net.Uri;
import com.dev.brain2.models.Folder;
import com.dev.brain2.managers.FolderManager;
import com.dev.brain2.models.Image;
import com.dev.brain2.adapters.ImageAdapter;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.List;

public class ImageAdapterTest {

    private List<Image> images;
    private FolderManager folderManager;
    private Folder currentFolder;
    private ImageAdapter adapter;


    @Test
    public void testGetItemCount() {
        assertEquals("El tamaño de la lista debería ser 2", 2, adapter.getItemCount());
    }

    @Test
    public void testAddImage() {
        // Convierte el String a un Uri
        images.add(new Image(Uri.parse("uri3"), "Image 3"));
        assertEquals("El tamaño de la lista debería ser 3 después de agregar una imagen", 3, adapter.getItemCount());
    }


    @Test
    public void testDeleteImage() {
        // Simula la eliminación de la imagen en la posición 0
        images.remove(0);  // Elimina la imagen en la posición 0 de la lista
        adapter.notifyDataSetChanged();  // Notifica al adaptador para que se actualice

        // Verifica que el tamaño de la lista es 1 después de la eliminación
        assertEquals("El tamaño de la lista debería ser 1 después de eliminar una imagen", 1, adapter.getItemCount());
    }

    @Test
    public void testRenameImage() {
        Image image = images.get(0);
        String newName = "New Image Name";
        image.setName(newName);
        assertEquals("El nombre de la imagen debería haberse actualizado", newName, image.getName());
    }

    @Test
    public void testMoveImageToAnotherFolder() {
        Folder targetFolder = new Folder("Target Folder", "SomeOtherParameter"); // Proporciona el segundo parámetro
        Image imageToMove = images.get(0);

        targetFolder.addImage(imageToMove);
        currentFolder.getImages().remove(imageToMove);

        assertEquals("La carpeta actual debería tener 1 imagen después de mover", 1, currentFolder.getImages().size());
        assertEquals("La carpeta de destino debería tener 1 imagen", 1, targetFolder.getImages().size());
    }


    @Test
    public void testImageUriParsing() {
        Image image = images.get(0);
        Uri imageUri = image.getUri(); // Asumiendo que getUri() ya devuelve un Uri
        assertEquals("La URI debería coincidir con 'uri1'", "uri1", imageUri.toString());
    }
}

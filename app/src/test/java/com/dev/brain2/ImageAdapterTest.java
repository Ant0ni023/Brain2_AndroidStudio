package com.dev.brain2;

import android.net.Uri;
import com.dev.brain2.Folder;
import com.dev.brain2.FolderManager;
import com.dev.brain2.Image;
import com.dev.brain2.ImageAdapter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
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
        images.add(new Image("uri3", "Image 3"));
        assertEquals("El tamaño de la lista debería ser 3 después de agregar una imagen", 3, adapter.getItemCount());
    }

    @Test
    public void testDeleteImage() {
        adapter.removeImage(0); // Llama al nuevo método para eliminar la imagen
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
        Uri imageUri = Uri.parse(image.getUri());
        assertEquals("La URI debería coincidir con 'uri1'", "uri1", imageUri.toString());
    }
}

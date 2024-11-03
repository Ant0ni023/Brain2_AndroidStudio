package com.dev.brain2;

import org.junit.Test;
import static org.junit.Assert.*;

public class ImageTest {

    @Test
    public void testConstructor() {
        // Verifica que el constructor inicializa los valores correctamente
        Image image = new Image("sampleUri", "sampleName");
        assertEquals("sampleUri", image.getUri());
        assertEquals("sampleName", image.getName());
    }

    @Test
    public void testSetUri() {
        // Verifica que se pueda actualizar el valor de uri
        Image image = new Image("initialUri", "name");
        image.setUri("newUri");
        assertEquals("newUri", image.getUri());
    }

    @Test
    public void testSetName() {
        // Verifica que se pueda actualizar el valor de name
        Image image = new Image("uri", "initialName");
        image.setName("newName");
        assertEquals("newName", image.getName());
    }

    @Test
    public void testGetUri() {
        // Verifica que getUri devuelve el valor correcto
        Image image = new Image("sampleUri", "name");
        assertEquals("sampleUri", image.getUri());
    }

    @Test
    public void testGetName() {
        // Verifica que getName devuelve el valor correcto
        Image image = new Image("uri", "sampleName");
        assertEquals("sampleName", image.getName());
    }
}

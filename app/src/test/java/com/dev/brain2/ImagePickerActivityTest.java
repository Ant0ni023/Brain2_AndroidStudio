package com.dev.brain2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ImagePickerActivityTest {

    @Rule
    public ActivityTestRule<ImagePickerActivity> activityRule =
            new ActivityTestRule<>(ImagePickerActivity.class);

    @Test
    public void testSelectImageFromGallery() {
        // Simula el click en el botón para seleccionar una imagen
        onView(withId(R.id.confirmButton)).perform(click());

        // Aquí deberías simular la selección de una imagen desde la galería
        // Suponiendo que tienes un URI de imagen válido para usar en la prueba
        Uri testImageUri = Uri.parse("android.resource://com.dev.brain2/drawable/test_image"); // Cambia el nombre por el de tu imagen
        Intent resultData = new Intent();
        resultData.setData(testImageUri);
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultData);

        // Simula el resultado de la selección de imagen
        activityRule.getActivity().pickImageLauncher.launch(result.getData());

        // Verifica que la imagen se muestre en el ImageView
        onView(withId(R.id.imageView)).check(matches(isDisplayed()));
    }

    @Test
    public void testTakePhoto() {
        // Simula el click en el botón para tomar una foto
        onView(withId(R.id.confirmButton)).perform(click());

        // Aquí deberías simular la toma de una foto
        Bitmap testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Intent resultData = new Intent();
        resultData.putExtra("data", testBitmap);
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultData);

        // Simula el resultado de tomar una foto
        activityRule.getActivity().takePhotoLauncher.launch(result.getData());

        // Verifica que la imagen se muestre en el ImageView
        onView(withId(R.id.imageView)).check(matches(isDisplayed()));
    }

    @Test
    public void testAskForImageNameAndSave() {
        // Simula la selección de una imagen
        Uri testImageUri = Uri.parse("android.resource://com.dev.brain2/drawable/test_image");
        activityRule.getActivity().setSelectedImageUri(testImageUri); // Usar el setter

        // Simula el click en el botón de confirmación
        onView(withId(R.id.confirmButton)).perform(click());

        // En vez de usar imageNameInput, puedes definir el nombre directamente
        String imageName = "test_image_name";

        // Simula la acción de guardar llamando directamente al método
        activityRule.getActivity().openFolderSelectionDialog(testImageUri, imageName);

        // Puedes añadir aquí tus aserciones para verificar el comportamiento esperado
    }

}


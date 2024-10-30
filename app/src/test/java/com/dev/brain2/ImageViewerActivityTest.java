package com.dev.brain2;

import android.net.Uri;
import android.os.Bundle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class ImageViewerActivityTest {

    @Test
    public void testImageDisplayed() {
        // Configura la actividad y pasa la URI de la imagen a través del Intent
        Uri imageUri = Uri.parse("android.resource://com.dev.brain2/drawable/your_image"); // Reemplaza con tu imagen
        Bundle bundle = new Bundle();
        bundle.putString("imageUri", imageUri.toString());

        // Inicia la actividad
        try (ActivityScenario<ImageViewerActivity> scenario = ActivityScenario.launch(ImageViewerActivity.class)) {
            // Simula el Intent
            scenario.onActivity(activity -> activity.getIntent().putExtras(bundle));

            // Verifica que la imagen se muestre en el ImageView
            onView(withId(R.id.fullImageView)).check(matches(isDisplayed()));
        }
    }
}

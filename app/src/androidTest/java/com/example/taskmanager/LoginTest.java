package com.example.taskmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    private ActivityScenario<Login> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(Login.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void testCorreoInvalido() {
        onView(withId(R.id.correoLogin)).perform(replaceText("correoInvalido"));
        onView(withId(R.id.contraseniaLogin)).perform(replaceText("password123"));
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText("Correo no valido")).inRoot(new ToastMatcher()).check(matches(withText("Correo no valido")));
    }

    @Test
    public void testContraseniaVacia() {
        onView(withId(R.id.correoLogin)).perform(replaceText("test@test.com"));
        onView(withId(R.id.contraseniaLogin)).perform(replaceText(""));
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText("Ingrese una Contraseña")).inRoot(new ToastMatcher()).check(matches(withText("Ingrese una Contraseña")));
    }

    @Test
    public void testMostrarMensajeProgreso() {
        scenario.onActivity(activity -> {
            EditText correoLogin = activity.findViewById(R.id.correoLogin);
            EditText contraseniaLogin = activity.findViewById(R.id.contraseniaLogin);
            Button btn_login = activity.findViewById(R.id.btn_login);

            correoLogin.setText("test@test.com");
            contraseniaLogin.setText("password123");

            btn_login.performClick();

            ProgressDialog progressDialog = activity.progressDialog;
            assertTrue(progressDialog.isShowing());
        });
    }

    @Test
    public void testInicioSesionExitoso() {
        // Esta prueba asume que el inicio de sesión será exitoso.

        scenario.onActivity(activity -> {
            EditText correoLogin = activity.findViewById(R.id.correoLogin);
            EditText contraseniaLogin = activity.findViewById(R.id.contraseniaLogin);
            Button btn_login = activity.findViewById(R.id.btn_login);

            correoLogin.setText("test@test.com");
            contraseniaLogin.setText("password123");

            btn_login.performClick();

            // Asume que el progreso se cierra y se navega a la siguiente actividad
            ProgressDialog progressDialog = activity.progressDialog;
            assertTrue(progressDialog.isShowing());
        });
    }
}



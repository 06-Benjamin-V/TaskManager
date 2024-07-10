package com.example.taskmanager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RegisterTest {

    @Test
    public void testNombreVacio() {
        try (ActivityScenario<Register> scenario = ActivityScenario.launch(Register.class)) {
            onView(withId(R.id.NombreET)).perform(replaceText(""));
            onView(withId(R.id.CorreoEt)).perform(replaceText("test@test.com"));
            onView(withId(R.id.ContrasenieET)).perform(replaceText("password123"));
            onView(withId(R.id.ConfirmarContraseniaET)).perform(replaceText("password123"));
            onView(withId(R.id.NombreET)).perform(click());

            onView(withText("Ingrese un nombre")).inRoot(new ToastMatcher())
                    .check(matches(withText("Ingrese un nombre")));
        }
    }

    @Test
    public void testCorreoInvalido() {
        try (ActivityScenario<Register> scenario = ActivityScenario.launch(Register.class)) {
            onView(withId(R.id.NombreET)).perform(replaceText("Nombre"));
            onView(withId(R.id.CorreoEt)).perform(replaceText("correoInvalido"));
            onView(withId(R.id.ContrasenieET)).perform(replaceText("password123"));
            onView(withId(R.id.ConfirmarContraseniaET)).perform(replaceText("password123"));
            onView(withId(R.id.NombreET)).perform(click());

            onView(withText("Ingrese un correo válido")).inRoot(new ToastMatcher())
                    .check(matches(withText("Ingrese un correo válido")));
        }
    }

    @Test
    public void testContraseniaVacia() {
        try (ActivityScenario<Register> scenario = ActivityScenario.launch(Register.class)) {
            onView(withId(R.id.NombreET)).perform(replaceText("Nombre"));
            onView(withId(R.id.CorreoEt)).perform(replaceText("test@test.com"));
            onView(withId(R.id.ContrasenieET)).perform(replaceText(""));
            onView(withId(R.id.ConfirmarContraseniaET)).perform(replaceText("password123"));
            onView(withId(R.id.NombreET)).perform(click());

            onView(withText("Ingrese una contraseña")).inRoot(new ToastMatcher())
                    .check(matches(withText("Ingrese una contraseña")));
        }
    }

    @Test
    public void testContraseniasNoCoinciden() {
        try (ActivityScenario<Register> scenario = ActivityScenario.launch(Register.class)) {
            onView(withId(R.id.NombreET)).perform(replaceText("Nombre"));
            onView(withId(R.id.CorreoEt)).perform(replaceText("test@test.com"));
            onView(withId(R.id.ContrasenieET)).perform(replaceText("password123"));
            onView(withId(R.id.ConfirmarContraseniaET)).perform(replaceText("password456"));
            onView(withId(R.id.NombreET)).perform(click());

            onView(withText("Las contraseñas no coinciden")).inRoot(new ToastMatcher())
                    .check(matches(withText("Las contraseñas no coinciden")));
        }
    }

    @Test
    public void testDatosValidos() {
        try (ActivityScenario<Register> scenario = ActivityScenario.launch(Register.class)) {
            onView(withId(R.id.NombreET)).perform(replaceText("Nombre"));
            onView(withId(R.id.CorreoEt)).perform(replaceText("test@test.com"));
            onView(withId(R.id.ContrasenieET)).perform(replaceText("password123"));
            onView(withId(R.id.ConfirmarContraseniaET)).perform(replaceText("password123"));
            onView(withId(R.id.NombreET)).perform(click());

            // Aquí podrías verificar si el ProgressDialog está mostrando el mensaje "Creando Cuenta...."
            onView(withText("Creando Cuenta....")).inRoot(new ToastMatcher())
                    .check(matches(withText("Creando Cuenta....")));
        }
    }
}




package com.example.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.MainActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;

public class MenuPrincipal extends AppCompatActivity {

    Button cerrarSesion;
    Button tareas;
    Button agregarTarea;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    CalendarView calendarioMenuPrincipal;

    TextView nombresPrincipal, correoPrincipal;
    ProgressBar progressBarDatos;
    FirebaseFirestore db;

    String uidUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Task Manager");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        nombresPrincipal = findViewById(R.id.nombresPrincipal);
        correoPrincipal = findViewById(R.id.correoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);
        cerrarSesion = findViewById(R.id.cerrarSesion);
        tareas = findViewById(R.id.tareas);
        agregarTarea = findViewById(R.id.agregarTarea);
        calendarioMenuPrincipal = findViewById(R.id.calendarioMenuPrincipal);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });

        agregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, AgregarTarea.class));
            }
        });

        tareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(MenuPrincipal.this, MostrarTareas.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MenuPrincipal.this, "Error al abrir la actividad de tareas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        comprobarInicioSesion();
    }

    private void comprobarInicioSesion() {
        if (firebaseAuth.getCurrentUser() != null) {
            cargarDatos();
        } else {
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }

    private void cargarDatos() {
        db.collection("Usuarios").document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            progressBarDatos.setVisibility(View.GONE);
                            nombresPrincipal.setVisibility(View.VISIBLE);
                            correoPrincipal.setVisibility(View.VISIBLE);

                            String nombre = document.getString("nombres");
                            String correo = document.getString("correo");

                            nombresPrincipal.setText(nombre);
                            correoPrincipal.setText(correo);
                        }
                    } else {
                        Toast.makeText(MenuPrincipal.this, "Error al obtener los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

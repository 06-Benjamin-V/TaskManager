package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuPrincipal extends AppCompatActivity {

    Button cerrarSesion;
    Button btnVerTareas;
    Button btnAgregarTarea;
    Button btnVerEventos;
    Button btnAgregarEvento;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    CalendarView calendarioMenuPrincipal;

    TextView nombresPrincipal, correoPrincipal;
    ProgressBar progressBarDatos;
    FirebaseFirestore db;

    GoogleApiClient googleApiClient;
    @SuppressLint("MissingInflatedId")
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
        calendarioMenuPrincipal = findViewById(R.id.calendarioMenuPrincipal);

        btnVerTareas = findViewById(R.id.btnVerTareas);
        btnVerEventos=findViewById(R.id.btnVerEventos);
        btnAgregarTarea=findViewById(R.id.btnAgregarTarea);
        btnAgregarEvento=findViewById(R.id.btnAgregarEvento);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        googleApiClient.connect();

        calendarioMenuPrincipal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                mostrarDialogoCrearEvento();
            }

            private void mostrarDialogoCrearEvento() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuPrincipal.this,R.style.AlertDialogTheme);
                builder.setTitle("Crear Evento");
                builder.setMessage("¿Desea Crear un Evento?");
                builder.setPositiveButton("Si", (dialog, which) -> {
                    Intent intent = new Intent(MenuPrincipal.this, AgregarEvento.class);
                    startActivity(intent);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });

        btnAgregarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, AgregarEvento.class));
            }
        });

        btnVerEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(MenuPrincipal.this, MostarEventos.class));
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MenuPrincipal.this, "No se puede abrir eventos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAgregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipal.this, AgregarTarea.class));
            }
        });

        btnVerTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(MenuPrincipal.this,MostarTareas.class));
                }catch (Exception e){
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
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(status -> {
            if (status.isSuccess()) {
                Toast.makeText(MenuPrincipal.this, "Cerraste sesión exitosamente", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(MenuPrincipal.this, "Error al cerrar sesión en Google", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

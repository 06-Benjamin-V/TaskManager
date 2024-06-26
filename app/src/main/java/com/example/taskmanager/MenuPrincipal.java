package com.example.taskmanager;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

public class MenuPrincipal extends AppCompatActivity {

    Button cerrarSesion;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView nombresPrincipal,correoPrincipal;
    ProgressBar progressBarDatos;
    FirebaseFirestore usuarios;


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


        nombresPrincipal=findViewById(R.id.nombresPrincipal);
        correoPrincipal=findViewById(R.id.correoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);
        cerrarSesion=findViewById(R.id.cerrarSesion);

        usuarios= FirebaseFirestore.getInstance().collection("Usuarios").getFirestore();
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalirAplicacion();
            }
        });
    }

    @Override
    protected void onStart() {
        comprobarInicioSesion();
        super.onStart();
    }

    private void comprobarInicioSesion(){
        if(firebaseAuth.getCurrentUser()!=null){
            cargarDatos();
        }else {
            startActivity(new Intent(MenuPrincipal.this,MainActivity.class));
            finish();
        }
    }

    private void cargarDatos(){
        usuarios.collection("Usuarios").document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            progressBarDatos.setVisibility(View.GONE);
                            nombresPrincipal.setVisibility(View.VISIBLE);
                            correoPrincipal.setVisibility(View.VISIBLE);

                            String nombre=documentSnapshot.getString("nombres");
                            String correo=documentSnapshot.getString("correo");

                            nombresPrincipal.setText(nombre);
                            correoPrincipal.setText(correo);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MenuPrincipal.this,"Error al obtener los datos"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this,"Cerraste sesion exitosamente",Toast.LENGTH_LONG).show();
    }
}
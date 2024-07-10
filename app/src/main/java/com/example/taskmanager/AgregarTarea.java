package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.taskmanager.objetos.Tarea;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AgregarTarea extends AppCompatActivity {

    private TextView tituloPaginaAgregarTarea;
    private EditText tituloTarea,descripcionTarea;
    private Button btnGuardarTarea;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final int RC_SIGN_IN = 9001;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tarea);

        tituloPaginaAgregarTarea=findViewById(R.id.tituloPaginaAgregarTarea);
        tituloTarea=findViewById(R.id.tituloTarea);
        descripcionTarea=findViewById(R.id.descripcionTarea);
        btnGuardarTarea=findViewById(R.id.btnGuardarTarea);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();


        btnGuardarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTarea();
            }
        });
    }
    private void guardarTarea() {
        final String titulo=tituloTarea.getText().toString();
        final String descripcion=descripcionTarea.getText().toString();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        Tarea tarea=new Tarea(titulo,descripcion);
            guardarTareaFirestore(tarea);

    }
    private void guardarTareaFirestore(Tarea tarea){
        if(user==null){
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }
        String uidUsuario=user.getUid();

        db.collection("Usuarios")
                .document(uidUsuario)
                .collection("Tareas")
                .add(tarea)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String tareaId = documentReference.getId(); // Obtener la ID generada por Firestore
                        tarea.setId(tareaId);
                        db.collection("Usuarios")
                                .document(uidUsuario)
                                .collection("Tareas")
                                .document(tareaId)
                                .set(tarea)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(AgregarTarea.this, "Tarea guardado correctamente", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(AgregarTarea.this, "Error al guardar tarea", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(AgregarTarea.this, "Error al Guardar Tarea en Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
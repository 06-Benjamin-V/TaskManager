package com.example.taskmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText NombreET, CorreoEt, ContrasenieET, ConfirmarContraseniaET;
    Button RegistrarUsuario;
    TextView TengoUnaCuetaET;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    String nombre = "";
    String correo = "";
    String contrasenia = "";
    String confirmarContrasenia = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Registrar");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        NombreET = findViewById(R.id.NombreET);
        CorreoEt = findViewById(R.id.CorreoEt);
        ContrasenieET = findViewById(R.id.ContrasenieET);
        ConfirmarContraseniaET = findViewById(R.id.ConfirmarContraseniaET);
        RegistrarUsuario = findViewById(R.id.RegistrarUsuarioET);
        TengoUnaCuetaET = findViewById(R.id.TengoUnaCuetaET);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere un Poco");
        progressDialog.setCanceledOnTouchOutside(false);

        RegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarDatos();
            }
        });

        TengoUnaCuetaET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }

    private void ValidarDatos() {
        nombre = NombreET.getText().toString().trim();
        correo = CorreoEt.getText().toString().trim();
        contrasenia = ContrasenieET.getText().toString().trim();
        confirmarContrasenia = ConfirmarContraseniaET.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(contrasenia)) {
            Toast.makeText(this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmarContrasenia)) {
            Toast.makeText(this, "Confirme la contraseña", Toast.LENGTH_SHORT).show();
        } else if (!contrasenia.equals(confirmarContrasenia)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            CrearCuenta();
        }
    }

    private void CrearCuenta() {
        progressDialog.setMessage("Creando Cuenta....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(correo, contrasenia)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        GuardarInformacion();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, "Error al crear cuenta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void GuardarInformacion() {
        progressDialog.setMessage("Guardando su Informacion");

        String uid = firebaseAuth.getCurrentUser().getUid();

        HashMap<String, Object> datos = new HashMap<>();
        datos.put("uid", uid);
        datos.put("correo", correo);
        datos.put("nombres", nombre);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Usuarios").document(uid)
                .set(datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, MenuPrincipal.class));
                        finish(); // Terminar esta actividad para evitar problemas de pila
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, "Error al guardar información: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    EditText correoLogin,contraseniaLogin;
    Button btn_login;
    TextView usuarioNuevoTXT;

    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    String correo="";
    String contrasenia="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        correoLogin=findViewById(R.id.correoLogin);
        contraseniaLogin=findViewById(R.id.contraseniaLogin);
        btn_login=findViewById(R.id.btn_login);
        usuarioNuevoTXT = findViewById(R.id.usuarioNuevoTXT);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(Login.this);
        progressDialog.setTitle("Espere un Poco");
        progressDialog.setCanceledOnTouchOutside(false);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarDatosLogin();
            }
        });
        usuarioNuevoTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    private void ValidarDatosLogin() {
        correo=correoLogin.getText().toString();
        contrasenia=contraseniaLogin.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            Toast.makeText(this,"Correo no valido",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(contrasenia)) {
            Toast.makeText(this,"Ingrese una Contraseña",Toast.LENGTH_SHORT).show();
        }else {
            LoginDeUsuario();
        }


    }

    private void LoginDeUsuario() {
        progressDialog.setMessage("Iniciando sesion...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(correo,contrasenia)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(Login.this,MenuPrincipal.class));
                            Toast.makeText(Login.this,"Bienvenido(a) "+user.getEmail(),Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this,"Verifique el Correo o la Contraseña",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
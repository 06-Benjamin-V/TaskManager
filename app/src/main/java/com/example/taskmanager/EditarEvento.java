package com.example.taskmanager;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.taskmanager.objetos.Evento;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;


public class EditarEvento extends AppCompatActivity {
    TextView tituloPaginaEditarTarea, fechayHoraInicioTXTEditar, fechaYHoraTerminoTXTEditar, tituloEditar;
    EditText descripcionEditar;
    Button btn_fecha_hora_inicioEditar, btn_fecha_hora_terminoEditar, btn_guardarEventoEditar;
    FirebaseFirestore db;
    FirebaseAuth auth;

    private static final String TAG = "EditarTarea";
    private static final int REQUEST_AUTHORIZATION = 1001;

    private GoogleSignInClient googleSignInClient;
    private GoogleAccountCredential googleAccountCredential;

    private String tituloOriginal;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_evento);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(CalendarScopes.CALENDAR))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(CalendarScopes.CALENDAR));

        tituloPaginaEditarTarea = findViewById(R.id.tituloPaginaEditarTarea);
        fechayHoraInicioTXTEditar = findViewById(R.id.fechayHoraInicioTXTEditarEvento);
        fechaYHoraTerminoTXTEditar = findViewById(R.id.fechaYHoraTerminoTXTEditarEvento);
        tituloOriginal = getIntent().getStringExtra("titulo").toString();

        tituloEditar = findViewById(R.id.tituloEditar);
        descripcionEditar = findViewById(R.id.descripcionEditar);

        btn_fecha_hora_inicioEditar = findViewById(R.id.btn_fecha_hora_inicioEditarEvento);
        btn_fecha_hora_terminoEditar = findViewById(R.id.btn_fecha_hora_terminoEditarEvento);
        btn_guardarEventoEditar = findViewById(R.id.btn_guardarEventoEditar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btn_fecha_hora_inicioEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(true);
            }
        });

        btn_fecha_hora_terminoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false);
            }
        });

        btn_guardarEventoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void showDateTimePicker(final boolean isInicio) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, yearSelected);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker(calendar, isInicio);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(final Calendar calendar, final boolean isInicio) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfDay) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minuteOfDay);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String dateTimeStr = dateFormat.format(calendar.getTime());
                    if (isInicio) {
                        fechayHoraInicioTXTEditar.setText(dateTimeStr);
                    } else {
                        fechaYHoraTerminoTXTEditar.setText(dateTimeStr);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void actualizarTareaFirestore() {
        String titulo = tituloEditar.getText().toString();
        String descripcion = descripcionEditar.getText().toString();
        String fechaInicio = fechayHoraInicioTXTEditar.getText().toString();
        String fechaTermino = fechaYHoraTerminoTXTEditar.getText().toString();
        String userId = auth.getCurrentUser().getUid();

        Evento eventoActualizada = new Evento(titulo, descripcion, fechaInicio, fechaTermino);
        CollectionReference tareasRef = db.collection("Usuarios")
                .document(userId)
                .collection("Tareas");
        Query query = tareasRef.whereEqualTo("titulo", tituloOriginal);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String uidTareaEditar = document.getId();

                        tareasRef.document(uidTareaEditar)
                                .set(eventoActualizada)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EditarEvento.this, tituloOriginal, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(EditarEvento.this, "Tarea actualizada en Firestore", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditarEvento.this, "Error al actualizar en Firestore " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

}
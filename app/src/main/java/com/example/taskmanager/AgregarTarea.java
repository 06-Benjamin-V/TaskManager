package com.example.taskmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanager.objetos.Tarea;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AgregarTarea extends AppCompatActivity {

    private TextView fechayHoraInicioTXT;
    private TextView fechaYHoraTerminoTXT;
    private Button btn_fecha_hora_inicio;
    private Button btn_fecha_hora_termino;
    private Button btn_guardarTarea;

    private EditText tituloTarea;
    private EditText descripcionTarea;

    private FirebaseFirestore db;
    private FirebaseAuth auth;


    private static final String TAG = "AgregarTarea";
    private static final int REQUEST_AUTHORIZATION = 1001;

    private GoogleSignInClient googleSignInClient;
    private GoogleAccountCredential googleAccountCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tarea);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(CalendarScopes.CALENDAR))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializar la credencial de Google Calendar
        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(CalendarScopes.CALENDAR));

        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        fechayHoraInicioTXT = findViewById(R.id.fechayHoraInicioTXT);
        fechaYHoraTerminoTXT = findViewById(R.id.fechaYHoraTerminoTXT);
        btn_fecha_hora_inicio = findViewById(R.id.btn_fecha_hora_inicio);
        btn_fecha_hora_termino = findViewById(R.id.btn_fecha_hora_termino);
        btn_guardarTarea = findViewById(R.id.btn_guardarTarea);
        tituloTarea=findViewById(R.id.titulo);
        descripcionTarea=findViewById(R.id.descripcion);

        btn_fecha_hora_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(true);
            }
        });

        btn_fecha_hora_termino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false);
            }
        });

        btn_guardarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTareasEnFirestore();
                guardarTareaEnGoogleCalendar();
            }
        });
    }

    private void showDateTimePicker(final boolean isInicio) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

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
                    // Establecer la hora seleccionada
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minuteOfDay);

                    // Formatear la fecha y hora seleccionadas
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String dateTimeStr = dateFormat.format(calendar.getTime());

                    // Actualizar el TextView correspondiente según sea inicio o término
                    if (isInicio) {
                        fechayHoraInicioTXT.setText(dateTimeStr);
                    } else {
                        fechaYHoraTerminoTXT.setText(dateTimeStr);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void guardarTareasEnFirestore(){
        String titulo = tituloTarea.getText().toString();
        String descripcion = descripcionTarea.getText().toString();
        String fechaInicio= fechayHoraInicioTXT.getText().toString();
        String fechaTermino= fechaYHoraTerminoTXT.getText().toString();

        String userId= auth.getCurrentUser().getUid();

        Tarea tarea = new Tarea(titulo,descripcion,fechaInicio,fechaTermino);

        CollectionReference tareasRef=db.collection("Usuarios")
                .document(userId)
                .collection("Tareas");

        tareasRef.add(tarea)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AgregarTarea.this,"Tarea guardada en firestore",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(AgregarTarea.this,"Error al guardar en firestore "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarTareaEnGoogleCalendar() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleAccountCredential.setSelectedAccount(account.getAccount());
            String fechaYHoraInicioSrc = fechayHoraInicioTXT.getText().toString();
            String fechaYHoraTerminoSrc = fechaYHoraTerminoTXT.getText().toString();

            String titulo = tituloTarea.getText().toString();
            String descripcion = descripcionTarea.getText().toString();

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getDefault());

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault());

            try {
                Date dateInicio = inputFormat.parse(fechaYHoraInicioSrc);
                Date dateTermino = inputFormat.parse(fechaYHoraTerminoSrc);

                String rfc3339DateTimeInicio = outputFormat.format(dateInicio);
                String rfc3339DateTimeTermino = outputFormat.format(dateTermino);

                DateTime dateTimeInicio = new DateTime(rfc3339DateTimeInicio);
                DateTime dateTimeTermino = new DateTime(rfc3339DateTimeTermino);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Event event = new Event()
                                    .setSummary(titulo)
                                    .setDescription(descripcion);

                            EventDateTime start = new EventDateTime()
                                    .setDateTime(dateTimeInicio)
                                    .setTimeZone("America/Santiago");
                            event.setStart(start);

                            EventDateTime end = new EventDateTime()
                                    .setDateTime(dateTimeTermino)
                                    .setTimeZone("America/Santiago");
                            event.setEnd(end);

                            // Inicializar el servicio de Calendar
                            com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    GsonFactory.getDefaultInstance(),
                                    googleAccountCredential)
                                    .setApplicationName(getString(R.string.app_name))
                                    .build();

                            // Insertar el evento en el calendario primario del usuario
                            Event createdEvent = service.events().insert("primary", event).execute();

                            // Verificar si el evento se creó correctamente
                            if (createdEvent != null) {
                                runOnUiThread(() -> {
                                    Toast.makeText(AgregarTarea.this, "Tarea creada en Google Calendar", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(AgregarTarea.this, "Error al crear la tarea en Google Calendar", Toast.LENGTH_SHORT).show();
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                Toast.makeText(AgregarTarea.this, "Error de red al guardar la tarea en Google Calendar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                Toast.makeText(AgregarTarea.this, "Error al guardar la tarea en Google Calendar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                        return null;
                    }
                }.execute();

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al parsear la fecha y hora: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes iniciar sesión con tu cuenta de Google", Toast.LENGTH_SHORT).show();
            signInWithGoogle();
        }
    }


    private void signInWithGoogle() {
        Intent signInIntent=googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,REQUEST_AUTHORIZATION);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==REQUEST_AUTHORIZATION && requestCode==RESULT_OK){
            guardarTareaEnGoogleCalendar();
        }
    }


}

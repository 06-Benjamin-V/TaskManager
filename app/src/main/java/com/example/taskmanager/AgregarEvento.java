package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanager.objetos.Evento;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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

public class AgregarEvento extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView tituloPaginaAgregarEvento,fechayHoraInicioTXTEvento,fechaYHoraTerminoTXTEvento;
    private EditText tituloEvento,descripcionEvento;
    private Button btn_fecha_hora_inicioEvento,btn_fecha_hora_terminoEvento,btnGuardarEvento;
    private CheckBox checkGuardarGoogleEvento;


    private static final String TAG = "AgregarEvento";
    private static final int REQUEST_AUTHORIZATION = 1001;

    private GoogleSignInClient googleSignInClient;
    private GoogleAccountCredential googleAccountCredential;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_evento);

        tituloPaginaAgregarEvento=findViewById(R.id.tituloPaginaAgregarEvento);
        fechayHoraInicioTXTEvento=findViewById(R.id.fechayHoraInicioTXTEvento);
        fechaYHoraTerminoTXTEvento=findViewById(R.id.fechaYHoraTerminoTXTEvento);

        tituloEvento=findViewById(R.id.tituloEvento);
        descripcionEvento=findViewById(R.id.descripcionEvento);

        btn_fecha_hora_inicioEvento=findViewById(R.id.btn_fecha_hora_inicioEvento);
        btn_fecha_hora_terminoEvento=findViewById(R.id.btn_fecha_hora_terminoEvento);
        btnGuardarEvento=findViewById(R.id.btnGuardarEvento);

        checkGuardarGoogleEvento=findViewById(R.id.checkGuardarGoogleEvento);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(CalendarScopes.CALENDAR))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(CalendarScopes.CALENDAR));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        btn_fecha_hora_inicioEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(true);
            }
        });

        btn_fecha_hora_terminoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false);
            }
        });


        btnGuardarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkGuardarGoogleEvento.isChecked()) {
                    if(GoogleSignIn.getLastSignedInAccount(AgregarEvento.this) != null) {
                        guardarTareasEnFirestore();
                        guardarTareaEnGoogleCalendar();
                    }else {
                        signInWithGoogle();
                    }
                }else {
                    guardarTareasEnFirestore();
                }
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
                        fechayHoraInicioTXTEvento.setText(dateTimeStr);
                    } else {
                        fechaYHoraTerminoTXTEvento.setText(dateTimeStr);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void guardarTareasEnFirestore() {
        String titulo = tituloEvento.getText().toString();
        String descripcion = descripcionEvento.getText().toString();
        String fechaInicio = fechayHoraInicioTXTEvento.getText().toString();
        String fechaTermino = fechaYHoraTerminoTXTEvento.getText().toString();
        String userId = auth.getCurrentUser().getUid();
        Evento evento = new Evento(titulo, descripcion, fechaInicio, fechaTermino);
        CollectionReference tareasRef = db.collection("Usuarios")
                .document(userId)
                .collection("Eventos");
        tareasRef.add(evento)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AgregarEvento.this, "Tarea guardada en firestore", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(AgregarEvento.this, "Error al guardar en firestore " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarTareaEnGoogleCalendar() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleAccountCredential.setSelectedAccount(account.getAccount());
            String fechaYHoraInicioSrc = fechayHoraInicioTXTEvento.getText().toString();
            String fechaYHoraTerminoSrc = fechaYHoraTerminoTXTEvento.getText().toString();

            String titulo = tituloEvento.getText().toString();
            String descripcion = descripcionEvento.getText().toString();

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getDefault());

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault());

            try {
                Date dateInicio = inputFormat.parse(fechaYHoraInicioSrc);
                Date dateTermino = inputFormat.parse(fechaYHoraTerminoSrc);

                String rfc3339DateTimeInicio = outputFormat.format(dateInicio);
                String rfc3339DateTimeTermino = outputFormat.format(dateTermino);

                EventDateTime start = new EventDateTime()
                        .setDateTime(new DateTime(rfc3339DateTimeInicio))
                        .setTimeZone(TimeZone.getDefault().getID());

                EventDateTime end = new EventDateTime()
                        .setDateTime(new DateTime(rfc3339DateTimeTermino))
                        .setTimeZone(TimeZone.getDefault().getID());

                Event event = new Event()
                        .setSummary(titulo)
                        .setDescription(descripcion)
                        .setStart(start)
                        .setEnd(end);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    GsonFactory.getDefaultInstance(),
                                    googleAccountCredential)
                                    .setApplicationName(getString(R.string.app_name))
                                    .build();
                            Event createdEvent = service.events().insert("primary", event).execute();

                            if (createdEvent != null) {
                                runOnUiThread(() -> {
                                    Toast.makeText(AgregarEvento.this, "Tarea creada en Google Calendar", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(AgregarEvento.this, "Error al crear la tarea en Google Calendar", Toast.LENGTH_SHORT).show();
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                Toast.makeText(AgregarEvento.this, "Error de red al guardar la tarea en Google Calendar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                Toast.makeText(AgregarEvento.this, "Error al guardar la tarea en Google Calendar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_AUTHORIZATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_AUTHORIZATION && requestCode == RESULT_OK) {
            guardarTareaEnGoogleCalendar();
        }
    }


}

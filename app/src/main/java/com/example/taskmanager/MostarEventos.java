package com.example.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanager.objetos.Evento;
import com.example.taskmanager.objetos.EventoAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
public class MostarEventos extends AppCompatActivity {

    private RecyclerView recyclerViewEventos;
    private EventoAdapter eventoAdapter;
    private List<Evento> listaEventos;

    private TextView tituloPaginaEventos;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_eventos);

        recyclerViewEventos = findViewById(R.id.recyclerViewEventos);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(this));

        tituloPaginaEventos=findViewById(R.id.tituloPaginaEventos);

        auth = FirebaseAuth.getInstance();

        listaEventos = new ArrayList<>();
        eventoAdapter = new EventoAdapter(listaEventos,this);
        recyclerViewEventos.setAdapter(eventoAdapter);
        obtenerEventosUsuarioDesdeFirestore();
    }

    private void obtenerEventosUsuarioDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = auth.getUid();

        db.collection("Usuarios").document(userId).collection("Eventos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaEventos.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Evento evento = document.toObject(Evento.class);
                        listaEventos.add(evento);
                    }
                    eventoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al Obtener los Eventos del Usuario", e);
                });
    }

}

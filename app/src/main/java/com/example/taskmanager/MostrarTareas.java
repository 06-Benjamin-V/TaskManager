package com.example.taskmanager;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanager.objetos.Tarea;
import com.example.taskmanager.objetos.TareaAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
public class MostrarTareas extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TareaAdapter tareaAdapter;
    private List<Tarea> listaTareas;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_tareas);

        recyclerView = findViewById(R.id.recyclerViewTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();

        listaTareas = new ArrayList<>();
        tareaAdapter = new TareaAdapter(listaTareas);
        recyclerView.setAdapter(tareaAdapter);
        obtenerTareasUsuarioDesdeFirestore();
    }

    private void obtenerTareasUsuarioDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = auth.getUid();

        db.collection("Usuarios").document(userId).collection("Tareas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaTareas.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Tarea tarea = document.toObject(Tarea.class);
                        listaTareas.add(tarea);
                    }
                    tareaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener tareas del usuario", e);
                });
    }
}

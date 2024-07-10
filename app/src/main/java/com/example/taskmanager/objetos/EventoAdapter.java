package com.example.taskmanager.objetos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanager.EditarEvento;
import com.example.taskmanager.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private static List<Evento> listaEventos;
    private Context context;
    private GoogleSignInAccount googleSignInAccount;

    public EventoAdapter(List<Evento> listaEventos, Context context) {
        this.listaEventos = listaEventos;
        this.context=context;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Evento evento = listaEventos.get(position);
        holder.textViewTitulo.setText(evento.getTitulo());
        holder.textViewDescripcion.setText(evento.getDescripcion());
        holder.textViewFechaInicio.setText(evento.getFechaHoraInicio());
        holder.textViewFechaFin.setText(evento.getFechaHoraFin());

        holder.eliminarEvento.setOnClickListener(v -> {
            eliminarEventoFirestore(position);
        });

        holder.editarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditarEvento.class);
                intent.putExtra("eventoId", evento.getEventoId());
                intent.putExtra("titulo", evento.getTitulo());
                context.startActivity(intent);
            }
        });

    }
    private void eliminarEventoFirestore(int position) {
        Evento evento = listaEventos.get(position);
        String userId = FirebaseAuth.getInstance().getUid();
        String eventoId = evento.getEventoId();
        if (userId != null && eventoId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Usuarios")
                    .document(userId)
                    .collection("Eventos")
                    .document(eventoId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        listaEventos.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Evento removido de Firestore", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al eliminar en Firestore", e);
                        Toast.makeText(context, "Error al eliminar en Firestore", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo, textViewDescripcion, textViewFechaInicio, textViewFechaFin;
        Button eliminarEvento,editarEvento;
        Context context;

        private static final String TAG = "Eventos";
        private static final int REQUEST_AUTHORIZATION = 1001;


        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewFechaInicio = itemView.findViewById(R.id.textViewFecha);
            textViewFechaFin = itemView.findViewById(R.id.textViewFechaFin);
            eliminarEvento=itemView.findViewById(R.id.eliminarEvento);
            editarEvento=itemView.findViewById(R.id.editarEvento);
            context = itemView.getContext();

        }

    }

}


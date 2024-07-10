package com.example.taskmanager.objetos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanager.R;
import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private static List<Evento> listaEventos;
    private Context context;

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

    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo, textViewDescripcion, textViewFechaInicio, textViewFechaFin;
        Context context;

        private static final String TAG = "Eventos";
        private static final int REQUEST_AUTHORIZATION = 1001;


        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewFechaInicio = itemView.findViewById(R.id.textViewFecha);
            textViewFechaFin = itemView.findViewById(R.id.textViewFechaFin);
            context = itemView.getContext();
        }

    }

}


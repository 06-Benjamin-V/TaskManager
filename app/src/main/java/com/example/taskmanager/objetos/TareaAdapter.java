package com.example.taskmanager.objetos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private static List<Tarea> listaTareas;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser usuario;
    DocumentReference userRef;

    public TareaAdapter(List<Tarea> listaTareas, Context context) {
        this.listaTareas = listaTareas;
        this.context=context;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Tarea tarea = listaTareas.get(position);
        holder.textViewTituloTarea.setText(tarea.getTituloTarea());
        holder.textViewDescripcionTarea.setText(tarea.getDescripcionTarea());
        holder.checkboxCompletarTareaTarea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mostrarVentanaCompletar(position);
                }else {
                    
                }
            }

            private void mostrarVentanaCompletar(int position) {
                AlertDialog.Builder dialogo=new AlertDialog.Builder(context);
                dialogo.setTitle("¿Completar Tarea?");
                dialogo.setMessage("¿Estas Seguro que Desea Completar la Tarea?");

                dialogo.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth = FirebaseAuth.getInstance();
                        usuario = auth.getCurrentUser();
                        String userId = usuario.getUid();
                        db = FirebaseFirestore.getInstance();
                        userRef = db.collection("Usuarios").document(userId);

                        userRef.collection("Tareas").document(listaTareas.get(position).getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        listaTareas.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(TareaAdapter.this.context, "Felicidades terminaste tu tarea", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(context, "Error al completar la Tarea", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                dialogo.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyItemChanged(position);
                    }
                });
                dialogo.setCancelable(false);
                dialogo.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTituloTarea, textViewDescripcionTarea;
        Context context;
        CheckBox checkboxCompletarTareaTarea;

        private static final String TAG = "Tareas";
        private static final int REQUEST_AUTHORIZATION = 1001;


        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTituloTarea = itemView.findViewById(R.id.textViewTituloTarea);
            textViewDescripcionTarea = itemView.findViewById(R.id.textViewDescripcionTarea);
            context = itemView.getContext();
            checkboxCompletarTareaTarea=itemView.findViewById(R.id.checkboxCompletarTareaTarea);

        }

    }

}

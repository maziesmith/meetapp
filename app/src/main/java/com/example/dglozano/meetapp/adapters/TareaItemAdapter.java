package com.example.dglozano.meetapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dglozano.meetapp.R;
import com.example.dglozano.meetapp.modelo.Tarea;

import java.util.List;

/**
 * Created by dglozano on 01/02/18.
 */

public class TareaItemAdapter extends RecyclerView.Adapter<TareaItemAdapter.TareaViewHolder> {

    private List<Tarea> tareasList;

    public class TareaViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, personasAsignadaTextView;
        private ImageView estadoImageView;

        public TareaViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.tv_tarea_row_title);
            personasAsignadaTextView = (TextView) view.findViewById(R.id.tv_tarea_row_persona_asignada);
            estadoImageView = (ImageView) view.findViewById(R.id.img_tarea_row_estado_icon);
        }
    }

    public TareaItemAdapter(List<Tarea> tareasList) {
        this.tareasList = tareasList;
    }

    @Override
    public TareaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarea_list_row, parent, false);

        return new TareaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TareaViewHolder holder, int position) {
        Tarea tarea = tareasList.get(position);
        holder.titleTextView.setText(tarea.getTitulo());
        holder.personasAsignadaTextView.setText(tarea.getPersonaAsignada().toString());
        switch(tarea.getEstadoTarea()){
            case SIN_ASIGNAR:
                holder.estadoImageView.setImageResource(R.drawable.ic_person_add_black_24dp);
                break;
            case EN_PROGRESO:
                holder.estadoImageView.setImageResource(R.drawable.ic_access_time_black_24dp);
                break;
            case FINALIZADA:
                holder.estadoImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tareasList.size();
    }
}
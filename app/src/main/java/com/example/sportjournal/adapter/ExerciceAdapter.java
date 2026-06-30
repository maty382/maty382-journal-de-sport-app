package com.example.sportjournal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportjournal.R;
import com.example.sportjournal.model.Exercice;

import java.util.List;

/**
 * ExerciceAdapter affiche la liste des exercices dans le détail d'une séance.
 * C'est le RecyclerView "imbriqué" demandé par le sujet.
 */
public class ExerciceAdapter extends
        RecyclerView.Adapter<ExerciceAdapter.ExerciceViewHolder> {

    private final List<Exercice> exercices;

    public ExerciceAdapter(List<Exercice> exercices) {
        this.exercices = exercices;
    }

    @NonNull
    @Override
    public ExerciceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercice, parent, false);
        return new ExerciceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciceViewHolder holder, int position) {
        Exercice exercice = exercices.get(position);

        holder.tvNom.setText(exercice.getNom());
        // Affiche "4 × 12 @ 80.5 kg"
        holder.tvResume.setText(exercice.getResume());
        // Affiche le volume total calculé
        holder.tvVolume.setText("Volume: " +
                String.format("%.0f", exercice.getVolumeTotal()) + " kg");
    }

    @Override
    public int getItemCount() {
        return exercices.size();
    }

    public static class ExerciceViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvResume, tvVolume;

        public ExerciceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tv_exercice_nom);
            tvResume = itemView.findViewById(R.id.tv_exercice_resume);
            tvVolume = itemView.findViewById(R.id.tv_exercice_volume);
        }
    }
}
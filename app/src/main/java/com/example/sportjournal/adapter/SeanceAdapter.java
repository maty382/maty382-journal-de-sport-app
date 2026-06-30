package com.example.sportjournal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportjournal.R;
import com.example.sportjournal.model.Seance;

import java.util.List;

public class SeanceAdapter extends RecyclerView.Adapter<SeanceAdapter.SeanceViewHolder> {

    private final List<Seance> seances;
    private final Context context;
    private final OnSeanceClickListener listener;

    public interface OnSeanceClickListener {
        void onSeanceClick(Seance seance);
        void onSeanceSupprimer(Seance seance, int position);
        void onSeanceModifier(Seance seance, int position, String nouvelleNote);
    }

    public SeanceAdapter(Context context, List<Seance> seances,
                         OnSeanceClickListener listener) {
        this.context = context;
        this.seances = seances;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seance, parent, false);
        return new SeanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeanceViewHolder holder, int position) {
        Seance seance = seances.get(position);

        holder.tvDate.setText(formatDate(seance.getDate()));
        holder.tvDuree.setText("⏱ " + seance.getDureeFormatee());

        int nbExercices = seance.getExercices() != null ?
                seance.getExercices().size() : 0;
        holder.tvExercices.setText("💪 " + nbExercices + " exercice(s)");

        if (seance.getNote() != null && !seance.getNote().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText("📝 " + seance.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        // Clic simple → ouvrir le détail
        holder.itemView.setOnClickListener(v ->
                listener.onSeanceClick(seance));

        // Bouton SUPPRIMER avec confirmation
        holder.btnSupprimer.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer la séance")
                    .setMessage("Voulez-vous vraiment supprimer la séance du "
                            + formatDate(seance.getDate())
                            + " ? Cette action est irréversible.")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        listener.onSeanceSupprimer(seance, position);
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        // Bouton MODIFIER avec confirmation
        holder.btnModifier.setOnClickListener(v -> {
            // Champ de saisie pour la nouvelle note
            EditText etNote = new EditText(context);
            etNote.setHint("Saisissez une note...");
            etNote.setText(seance.getNote());
            etNote.setSingleLine(false);
            etNote.setMinLines(2);

            LinearLayout container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(50, 20, 50, 10);
            container.addView(etNote);

            new AlertDialog.Builder(context)
                    .setTitle("Modifier la séance")
                    .setMessage("Séance du " + formatDate(seance.getDate()))
                    .setView(container)
                    .setPositiveButton("Enregistrer", (dialog, which) -> {
                        String nouvelleNote = etNote.getText().toString().trim();
                        // Confirmation avant d'enregistrer
                        new AlertDialog.Builder(context)
                                .setTitle("Confirmer la modification")
                                .setMessage("Voulez-vous enregistrer cette modification ?")
                                .setPositiveButton("Confirmer", (d, w) -> {
                                    seance.setNote(nouvelleNote);
                                    notifyItemChanged(position);
                                    listener.onSeanceModifier(seance, position, nouvelleNote);
                                })
                                .setNegativeButton("Annuler", null)
                                .show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return seances.size();
    }

    public void supprimerItem(int position) {
        seances.remove(position);
        notifyItemRemoved(position);
    }

    private String formatDate(String date) {
        if (date == null || date.length() < 10) return date;
        String[] mois = {"", "Janvier", "Février", "Mars", "Avril", "Mai",
                "Juin", "Juillet", "Août", "Septembre", "Octobre",
                "Novembre", "Décembre"};
        try {
            String[] parts = date.split("-");
            int m = Integer.parseInt(parts[1]);
            return parts[2] + " " + mois[m] + " " + parts[0];
        } catch (Exception e) {
            return date;
        }
    }

    public static class SeanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDuree, tvExercices, tvNote;
        TextView btnSupprimer, btnModifier;

        public SeanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate      = itemView.findViewById(R.id.tv_seance_date);
            tvDuree     = itemView.findViewById(R.id.tv_seance_duree);
            tvExercices = itemView.findViewById(R.id.tv_seance_exercices);
            tvNote      = itemView.findViewById(R.id.tv_seance_note);
            btnSupprimer = itemView.findViewById(R.id.btn_supprimer);
            btnModifier  = itemView.findViewById(R.id.btn_modifier);
        }
    }
}
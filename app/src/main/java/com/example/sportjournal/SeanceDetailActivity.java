package com.example.sportjournal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportjournal.adapter.ExerciceAdapter;
import com.example.sportjournal.database.ExerciceDAO;
import com.example.sportjournal.model.Exercice;
import com.example.sportjournal.model.Seance;

import java.util.List;

/**
 * SeanceDetailActivity : affiche le détail complet d'une séance.
 * Reçoit l'objet Seance via Intent (Serializable).
 * Affiche les exercices dans un RecyclerView imbriqué.
 */
public class SeanceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seance_detail);

        // Toolbar avec bouton retour
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Récupérer la séance passée via Intent (Serializable)
        Seance seance = (Seance) getIntent().getSerializableExtra("SEANCE");

        if (seance == null) {
            finish();
            return;
        }

        // Remplir les infos de la séance
        TextView tvDate = findViewById(R.id.tv_detail_date);
        TextView tvDuree = findViewById(R.id.tv_detail_duree);
        TextView tvNbExercices = findViewById(R.id.tv_detail_nb_exercices);
        TextView tvNote = findViewById(R.id.tv_detail_note);

        tvDate.setText(seance.getDate());
        tvDuree.setText("⏱ " + seance.getDureeFormatee());

        // Charger les exercices depuis la base de données
        ExerciceDAO exerciceDAO = new ExerciceDAO(this);
        List<Exercice> exercices = exerciceDAO.getExercicesParSeance(seance.getId());

        tvNbExercices.setText("💪 " + exercices.size() + " exercice(s)");

        // Afficher la note si elle existe
        if (seance.getNote() != null && !seance.getNote().isEmpty()) {
            tvNote.setVisibility(View.VISIBLE);
            tvNote.setText("📝 " + seance.getNote());
        }

        // RecyclerView imbriqué pour les exercices
        RecyclerView rvExercices = findViewById(R.id.rv_exercices);
        ExerciceAdapter exerciceAdapter = new ExerciceAdapter(exercices);
        rvExercices.setLayoutManager(new LinearLayoutManager(this));
        rvExercices.setAdapter(exerciceAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
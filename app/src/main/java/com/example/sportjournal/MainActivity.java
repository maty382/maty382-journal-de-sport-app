package com.example.sportjournal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportjournal.adapter.SeanceAdapter;
import com.example.sportjournal.database.ExerciceDAO;
import com.example.sportjournal.database.SeanceDAO;
import com.example.sportjournal.model.Exercice;
import com.example.sportjournal.model.Seance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SeanceAdapter.OnSeanceClickListener {

    private SeanceAdapter adapter;
    private List<Seance> seanceList;
    private SeanceDAO seanceDAO;
    private ExerciceDAO exerciceDAO;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private TextView tvNbSeances, tvTempsTotal, tvNbExercices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        seanceDAO   = new SeanceDAO(this);
        exerciceDAO = new ExerciceDAO(this);

        recyclerView  = findViewById(R.id.recyclerview_seances);
        tvEmpty       = findViewById(R.id.tv_empty);
        tvNbSeances   = findViewById(R.id.tv_nb_seances);
        tvTempsTotal  = findViewById(R.id.tv_temps_total);
        tvNbExercices = findViewById(R.id.tv_nb_exercices);

        // LayoutManager initialisé une seule fois
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, NouvelleSeanceActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerSeances();
    }

    private void chargerSeances() {
        seanceList = seanceDAO.getToutesLesSeances();

        for (Seance seance : seanceList) {
            List<Exercice> exercices =
                    exerciceDAO.getExercicesParSeance(seance.getId());
            seance.setExercices(exercices);
        }

        if (seanceList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new SeanceAdapter(this, seanceList, this);
        recyclerView.setAdapter(adapter);

        mettreAJourStatistiques();
    }

    private void mettreAJourStatistiques() {
        int nbSeances   = seanceDAO.getNombreSeances();
        int tempsTotal  = seanceDAO.getTempsTotal();
        int nbExercices = exerciceDAO.getNombreExercicesTotal();

        tvNbSeances.setText(String.valueOf(nbSeances));
        tvNbExercices.setText(String.valueOf(nbExercices));

        int heures  = tempsTotal / 3600;
        int minutes = (tempsTotal % 3600) / 60;
        if (heures > 0) {
            tvTempsTotal.setText(heures + "h" + minutes + "m");
        } else {
            tvTempsTotal.setText(minutes + "min");
        }
    }

    /**
     * Clic simple → ouvrir le détail de la séance
     */
    @Override
    public void onSeanceClick(Seance seance) {
        Intent intent = new Intent(this, SeanceDetailActivity.class);
        intent.putExtra("SEANCE", seance);
        startActivity(intent);
    }

    /**
     * Bouton Supprimer → dialogue de confirmation → suppression en base
     */
    @Override
    public void onSeanceSupprimer(Seance seance, int position) {
        seanceDAO.supprimerSeance(seance.getId());
        // Recharger toute la liste proprement
        chargerSeances();
        Toast.makeText(this, "✓ Séance supprimée", Toast.LENGTH_SHORT).show();
    }

    /**
     * Bouton Modifier → sauvegarde la nouvelle note en base
     */
    @Override
    public void onSeanceModifier(Seance seance, int position, String nouvelleNote) {
        seanceDAO.mettreAJourNote(seance.getId(), nouvelleNote);
        Toast.makeText(this, "✓ Séance modifiée", Toast.LENGTH_SHORT).show();
    }
}
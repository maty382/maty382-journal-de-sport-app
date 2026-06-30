package com.example.sportjournal;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportjournal.adapter.ExerciceAdapter;
import com.example.sportjournal.database.ExerciceDAO;
import com.example.sportjournal.database.SeanceDAO;
import com.example.sportjournal.model.Exercice;
import com.example.sportjournal.model.Seance;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * NouvelleSeanceActivity : écran pour créer une nouvelle séance.
 * Fonctionnalités bonus :
 * - Chronomètre avec démarrer/pause/reset
 * - Formulaire d'ajout d'exercice
 * - Liste en temps réel des exercices ajoutés
 * - Sauvegarde en base de données au moment de terminer
 */
public class NouvelleSeanceActivity extends AppCompatActivity {

    // === Chronomètre ===
    private TextView tvChrono;
    private Button btnStartPause, btnReset;
    private Timer timer;
    private int secondesEcoulees = 0;
    private boolean chronoEnMarche = false;

    // === Exercices ===
    private TextInputEditText etNom, etSeries, etReps, etPoids, etNote;
    private RecyclerView rvExercicesTemp;
    private ExerciceAdapter exerciceAdapter;
    private final List<Exercice> exercicesTemp = new ArrayList<>();

    // === Base de données ===
    private SeanceDAO seanceDAO;
    private ExerciceDAO exerciceDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nouvelle_seance);

        // Toolbar avec bouton retour
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // DAOs
        seanceDAO = new SeanceDAO(this);
        exerciceDAO = new ExerciceDAO(this);

        // Vues
        tvChrono = findViewById(R.id.tv_chrono);
        btnStartPause = findViewById(R.id.btn_start_pause);
        btnReset = findViewById(R.id.btn_reset);
        etNom = findViewById(R.id.et_nom_exercice);
        etSeries = findViewById(R.id.et_series);
        etReps = findViewById(R.id.et_reps);
        etPoids = findViewById(R.id.et_poids);
        etNote = findViewById(R.id.et_note_seance);
        rvExercicesTemp = findViewById(R.id.rv_exercices_temp);

        // RecyclerView des exercices en cours
        exerciceAdapter = new ExerciceAdapter(exercicesTemp);
        rvExercicesTemp.setLayoutManager(new LinearLayoutManager(this));
        rvExercicesTemp.setAdapter(exerciceAdapter);

        // Chronomètre
        btnStartPause.setOnClickListener(v -> toggleChrono());
        btnReset.setOnClickListener(v -> resetChrono());

        // Ajouter exercice
        Button btnAjouterExercice = findViewById(R.id.btn_ajouter_exercice);
        btnAjouterExercice.setOnClickListener(v -> ajouterExercice());

        // Terminer séance
        Button btnTerminer = findViewById(R.id.btn_terminer);
        btnTerminer.setOnClickListener(v -> terminerSeance());
    }

    /**
     * Démarre ou met en pause le chronomètre
     */
    private void toggleChrono() {
        if (!chronoEnMarche) {
            // Démarrer
            chronoEnMarche = true;
            btnStartPause.setText("⏸ Pause");
            btnStartPause.setBackgroundTintList(
                    getColorStateList(R.color.orange_warning));

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    secondesEcoulees++;
                    // Met à jour l'UI sur le thread principal
                    runOnUiThread(() -> tvChrono.setText(formaterTemps(secondesEcoulees)));
                }
            }, 1000, 1000);
        } else {
            // Pause
            chronoEnMarche = false;
            btnStartPause.setText("▶ Reprendre");
            btnStartPause.setBackgroundTintList(
                    getColorStateList(R.color.green_success));
            if (timer != null) timer.cancel();
        }
    }

    /**
     * Remet le chronomètre à zéro
     */
    private void resetChrono() {
        if (timer != null) timer.cancel();
        chronoEnMarche = false;
        secondesEcoulees = 0;
        tvChrono.setText("00:00:00");
        btnStartPause.setText("▶ Démarrer");
        btnStartPause.setBackgroundTintList(
                getColorStateList(R.color.green_success));
    }

    /**
     * Convertit les secondes en format "HH:MM:SS"
     */
    private String formaterTemps(int secondes) {
        int h = secondes / 3600;
        int m = (secondes % 3600) / 60;
        int s = secondes % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
    }

    /**
     * Valide et ajoute un exercice à la liste temporaire
     */
    private void ajouterExercice() {
        String nom = etNom.getText().toString().trim();
        String seriesStr = etSeries.getText().toString().trim();
        String repsStr = etReps.getText().toString().trim();
        String poidsStr = etPoids.getText().toString().trim();

        // Validation
        if (nom.isEmpty() || seriesStr.isEmpty() ||
                repsStr.isEmpty() || poidsStr.isEmpty()) {
            Toast.makeText(this, "Remplissez tous les champs",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int series = Integer.parseInt(seriesStr);
        int reps = Integer.parseInt(repsStr);
        double poids = Double.parseDouble(poidsStr);

        // Créer l'exercice temporaire (seanceId = 0 pour l'instant)
        Exercice exercice = new Exercice(nom, series, reps, poids, 0);
        exercicesTemp.add(exercice);
        exerciceAdapter.notifyItemInserted(exercicesTemp.size() - 1);

        // Vider le formulaire
        etNom.setText("");
        etSeries.setText("");
        etReps.setText("");
        etPoids.setText("");

        Toast.makeText(this, "✓ " + nom + " ajouté", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sauvegarde la séance et tous ses exercices en base de données
     */
    private void terminerSeance() {
        if (exercicesTemp.isEmpty()) {
            Toast.makeText(this,
                    "Ajoutez au moins un exercice", Toast.LENGTH_SHORT).show();
            return;
        }

        // Arrêter le chrono
        if (chronoEnMarche && timer != null) timer.cancel();

        // Date du jour
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        String note = etNote.getText().toString().trim();

        // Créer et sauvegarder la séance
        Seance seance = new Seance(date, secondesEcoulees, note);
        long seanceId = seanceDAO.insererSeance(seance);

        // Sauvegarder chaque exercice avec l'id de la séance
        for (Exercice exercice : exercicesTemp) {
            exercice.setSeanceId((int) seanceId);
            exerciceDAO.insererExercice(exercice);
        }

        Toast.makeText(this,
                "✓ Séance sauvegardée avec " + exercicesTemp.size() + " exercices",
                Toast.LENGTH_LONG).show();

        finish(); // Retour à MainActivity
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
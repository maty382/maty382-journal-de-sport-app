package com.example.sportjournal.model;

import java.io.Serializable;

/**
 * Modèle représentant un exercice dans une séance.
 * Lié à une séance via seanceId (clé étrangère SQLite).
 */
public class Exercice implements Serializable {

    private int id;
    private String nom;        // Ex: "Squat", "Développé couché"
    private int series;        // Nombre de séries : ex 4
    private int reps;          // Répétitions par série : ex 12
    private double poids;      // Poids en kg : ex 80.5
    private int seanceId;      // Référence vers la séance parente (clé étrangère)

    // Constructeur sans id (avant insertion en base)
    public Exercice(String nom, int series, int reps, double poids, int seanceId) {
        this.nom = nom;
        this.series = series;
        this.reps = reps;
        this.poids = poids;
        this.seanceId = seanceId;
    }

    // Constructeur complet (depuis la base de données)
    public Exercice(int id, String nom, int series, int reps, double poids, int seanceId) {
        this.id = id;
        this.nom = nom;
        this.series = series;
        this.reps = reps;
        this.poids = poids;
        this.seanceId = seanceId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getSeries() { return series; }
    public void setSeries(int series) { this.series = series; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public double getPoids() { return poids; }
    public void setPoids(double poids) { this.poids = poids; }

    public int getSeanceId() { return seanceId; }
    public void setSeanceId(int seanceId) { this.seanceId = seanceId; }

    /**
     * Calcule le volume total de cet exercice (séries × reps × poids)
     * Utile pour les statistiques de progression
     */
    public double getVolumeTotal() {
        return series * reps * poids;
    }

    /**
     * Retourne un résumé lisible : "4 × 12 @ 80.5 kg"
     */
    public String getResume() {
        return series + " × " + reps + " @ " + poids + " kg";
    }
}
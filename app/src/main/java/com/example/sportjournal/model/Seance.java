package com.example.sportjournal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle représentant une séance d'entraînement.
 * Implémente Serializable pour pouvoir être passé entre Activities via Intent.
 * C'est comme mettre un objet dans une enveloppe et l'envoyer à une autre Activity.
 */
public class Seance implements Serializable {

    private int id;
    private String date;       // Format : "2024-01-15"
    private int duree;         // En secondes (pour le chronomètre)
    private String note;       // Ressenti de la séance
    private List<Exercice> exercices; // Liste des exercices de cette séance

    // Constructeur pour créer une nouvelle séance
    public Seance(String date, int duree, String note) {
        this.date = date;
        this.duree = duree;
        this.note = note;
        this.exercices = new ArrayList<>();
    }

    // Constructeur complet (depuis la base de données)
    public Seance(int id, String date, int duree, String note) {
        this.id = id;
        this.date = date;
        this.duree = duree;
        this.note = note;
        this.exercices = new ArrayList<>();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<Exercice> getExercices() { return exercices; }
    public void setExercices(List<Exercice> exercices) { this.exercices = exercices; }

    /**
     * Convertit la durée en secondes en format lisible "1h 23min 45s"
     */
    public String getDureeFormatee() {
        int heures = duree / 3600;
        int minutes = (duree % 3600) / 60;
        int secondes = duree % 60;

        if (heures > 0) {
            return heures + "h " + minutes + "min " + secondes + "s";
        } else if (minutes > 0) {
            return minutes + "min " + secondes + "s";
        } else {
            return secondes + "s";
        }
    }
}
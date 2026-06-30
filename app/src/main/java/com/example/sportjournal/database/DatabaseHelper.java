package com.example.sportjournal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper gère la création et la mise à jour de la base de données SQLite.
 * C'est le "chef de chantier" qui construit les tables au premier lancement.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Nom du fichier de base de données sur l'appareil
    private static final String DATABASE_NAME = "sportjournal.db";
    // Version : à incrémenter si on modifie la structure
    private static final int DATABASE_VERSION = 1;

    // ============ TABLE SEANCES ============
    public static final String TABLE_SEANCES = "seances";
    public static final String COL_SEANCE_ID = "id";
    public static final String COL_SEANCE_DATE = "date";
    public static final String COL_SEANCE_DUREE = "duree";
    public static final String COL_SEANCE_NOTE = "note";

    // ============ TABLE EXERCICES ============
    public static final String TABLE_EXERCICES = "exercices";
    public static final String COL_EXERCICE_ID = "id";
    public static final String COL_EXERCICE_NOM = "nom";
    public static final String COL_EXERCICE_SERIES = "series";
    public static final String COL_EXERCICE_REPS = "reps";
    public static final String COL_EXERCICE_POIDS = "poids";
    public static final String COL_EXERCICE_SEANCE_ID = "seance_id"; // Clé étrangère

    // Requête SQL pour créer la table séances
    private static final String CREATE_TABLE_SEANCES =
            "CREATE TABLE " + TABLE_SEANCES + " (" +
                    COL_SEANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SEANCE_DATE + " TEXT NOT NULL, " +
                    COL_SEANCE_DUREE + " INTEGER DEFAULT 0, " +
                    COL_SEANCE_NOTE + " TEXT" +
                    ");";

    // Requête SQL pour créer la table exercices
    // FOREIGN KEY lie seance_id à la table séances
    // ON DELETE CASCADE supprime les exercices si la séance est supprimée
    private static final String CREATE_TABLE_EXERCICES =
            "CREATE TABLE " + TABLE_EXERCICES + " (" +
                    COL_EXERCICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_EXERCICE_NOM + " TEXT NOT NULL, " +
                    COL_EXERCICE_SERIES + " INTEGER DEFAULT 1, " +
                    COL_EXERCICE_REPS + " INTEGER DEFAULT 1, " +
                    COL_EXERCICE_POIDS + " REAL DEFAULT 0, " +
                    COL_EXERCICE_SEANCE_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + COL_EXERCICE_SEANCE_ID + ") " +
                    "REFERENCES " + TABLE_SEANCES + "(" + COL_SEANCE_ID + ") " +
                    "ON DELETE CASCADE" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Active les clés étrangères dans SQLite (désactivées par défaut)
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL(CREATE_TABLE_SEANCES);
        db.execSQL(CREATE_TABLE_EXERCICES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si on change la structure, on recrée tout
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEANCES);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Active les clés étrangères à chaque ouverture
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}
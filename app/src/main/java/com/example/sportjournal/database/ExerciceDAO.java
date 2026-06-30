package com.example.sportjournal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sportjournal.model.Exercice;

import java.util.ArrayList;
import java.util.List;

/**
 * ExerciceDAO gère toutes les opérations sur la table exercices.
 */
public class ExerciceDAO {

    private final DatabaseHelper dbHelper;

    public ExerciceDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Insère un exercice lié à une séance
     */
    public long insererExercice(Exercice exercice) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_EXERCICE_NOM, exercice.getNom());
        values.put(DatabaseHelper.COL_EXERCICE_SERIES, exercice.getSeries());
        values.put(DatabaseHelper.COL_EXERCICE_REPS, exercice.getReps());
        values.put(DatabaseHelper.COL_EXERCICE_POIDS, exercice.getPoids());
        values.put(DatabaseHelper.COL_EXERCICE_SEANCE_ID, exercice.getSeanceId());

        long id = db.insert(DatabaseHelper.TABLE_EXERCICES, null, values);
        db.close();
        return id;
    }

    /**
     * Retourne tous les exercices d'une séance donnée
     */
    public List<Exercice> getExercicesParSeance(int seanceId) {
        List<Exercice> exercices = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_EXERCICES,
                null,
                DatabaseHelper.COL_EXERCICE_SEANCE_ID + "=?",
                new String[]{String.valueOf(seanceId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCICE_ID));
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCICE_NOM));
                int series = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCICE_SERIES));
                int reps = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCICE_REPS));
                double poids = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCICE_POIDS));
                exercices.add(new Exercice(id, nom, series, reps, poids, seanceId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return exercices;
    }

    /**
     * Retourne le nombre total d'exercices (pour les statistiques)
     */
    public int getNombreExercicesTotal() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_EXERCICES, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
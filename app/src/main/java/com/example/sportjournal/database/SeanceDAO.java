package com.example.sportjournal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sportjournal.model.Seance;

import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    private final DatabaseHelper dbHelper;

    public SeanceDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Insère une nouvelle séance et retourne son id
     */
    public long insererSeance(Seance seance) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SEANCE_DATE, seance.getDate());
        values.put(DatabaseHelper.COL_SEANCE_DUREE, seance.getDuree());
        values.put(DatabaseHelper.COL_SEANCE_NOTE, seance.getNote());
        long id = db.insert(DatabaseHelper.TABLE_SEANCES, null, values);
        db.close();
        return id;
    }

    /**
     * Met à jour la durée d'une séance
     */
    public void mettreAJourDuree(int seanceId, int duree) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SEANCE_DUREE, duree);
        db.update(DatabaseHelper.TABLE_SEANCES, values,
                DatabaseHelper.COL_SEANCE_ID + "=?",
                new String[]{String.valueOf(seanceId)});
        db.close();
    }

    /**
     * Met à jour la note d'une séance
     */
    public void mettreAJourNote(int seanceId, String nouvelleNote) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SEANCE_NOTE, nouvelleNote);
        db.update(DatabaseHelper.TABLE_SEANCES, values,
                DatabaseHelper.COL_SEANCE_ID + "=?",
                new String[]{String.valueOf(seanceId)});
        db.close();
    }

    /**
     * Retourne toutes les séances triées par date décroissante
     */
    public List<Seance> getToutesLesSeances() {
        List<Seance> seances = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SEANCES,
                null, null, null, null, null,
                DatabaseHelper.COL_SEANCE_DATE + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SEANCE_ID));
                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SEANCE_DATE));
                int duree = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SEANCE_DUREE));
                String note = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SEANCE_NOTE));
                seances.add(new Seance(id, date, duree, note));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return seances;
    }

    /**
     * Supprime une séance — les exercices sont supprimés
     * automatiquement grâce à ON DELETE CASCADE
     */
    public void supprimerSeance(int seanceId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_SEANCES,
                DatabaseHelper.COL_SEANCE_ID + "=?",
                new String[]{String.valueOf(seanceId)});
        db.close();
    }

    /**
     * Nombre total de séances (pour les statistiques)
     */
    public int getNombreSeances() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_SEANCES, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Temps total d'entraînement en secondes (pour les statistiques)
     */
    public int getTempsTotal() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COL_SEANCE_DUREE +
                        ") FROM " + DatabaseHelper.TABLE_SEANCES, null);
        int total = 0;
        if (cursor.moveToFirst()) total = cursor.getInt(0);
        cursor.close();
        db.close();
        return total;
    }
}
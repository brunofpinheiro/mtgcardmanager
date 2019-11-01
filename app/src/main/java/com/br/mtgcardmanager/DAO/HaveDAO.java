package com.br.mtgcardmanager.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.br.mtgcardmanager.Model.Card;

import java.util.ArrayList;

import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_FOIL;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_ID;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_ID_EDITION;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_NAME_EN;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_NAME_PT;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_QUANTITY;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.LOG;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.TABLE_HAVE;

public class HaveDAO {

    /**
     * Inserts a card into table have
     * @param db
     * @param card
     * @return
     */
    public Long insertHaveCard(SQLiteDatabase db, Card card){
        long cardId;

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_EN, card.getName_en());
        values.put(KEY_NAME_PT, card.getName_pt());
        values.put(KEY_ID_EDITION, card.getId_edition());
        values.put(KEY_QUANTITY, card.getQuantity());
        values.put(KEY_FOIL, card.getFoil());

        // insert row
        cardId = db.insert(TABLE_HAVE, null, values);

        return cardId;
    }

    /**
     * Returns a list of all have cards
     * @param db
     * @return
     */
    public ArrayList<Card> getAllHaveCards(SQLiteDatabase db){
        ArrayList<Card> cards       = new ArrayList<>();
        String          selectQuery = "";
        Cursor          cursor      = null;

        Log.e(LOG, selectQuery);
        selectQuery = "SELECT * FROM " + TABLE_HAVE + " ORDER BY " + KEY_NAME_PT;
        cursor      = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                Card card = new Card();
                card.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                card.setName_en(cursor.getString(cursor.getColumnIndex(KEY_NAME_EN)));
                card.setName_pt(cursor.getString(cursor.getColumnIndex(KEY_NAME_PT)));
                card.setId_edition(cursor.getInt(cursor.getColumnIndex(KEY_ID_EDITION)));
                card.setQuantity(cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)));
                card.setFoil(cursor.getString(cursor.getColumnIndex(KEY_FOIL)));

                cards.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cards;
    }

    /**
     * Deletes a have card
     * @param db
     * @param id_have_card
     * @return
     */
    public int deleteHaveCard(SQLiteDatabase db, long id_have_card){
        int rowsAffected;

        rowsAffected = db.delete(TABLE_HAVE, KEY_ID + " = ?", new String[]{String.valueOf(id_have_card)});
        db.close();

        return rowsAffected;
    }

    /**
     * Deletes all have cards
     * @param db
     */
    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_HAVE, null, null);
        db.close();
    }

    /**
     * Checks if a specific card already exists in table have
     * @param db
     * @param name_en
     * @param id_edition
     * @param foil
     * @return
     */
    public Card checkIfHaveCardExists(SQLiteDatabase db, String name_en, int id_edition, String foil){
        String selectQuery;
        Cursor cursor;
        Card   existingCard;

        selectQuery = "SELECT *" +
                " FROM " + TABLE_HAVE +
                " WHERE " + KEY_NAME_EN + " = '" + name_en + "'" +
                " AND " + KEY_ID_EDITION + " = " + id_edition +
                " AND " + KEY_FOIL + " = '" + foil + "'";

        Log.e(LOG, selectQuery);

        cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        existingCard = new Card();

        if (cursor.getCount() > 0) {
            existingCard.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            existingCard.setName_en(cursor.getString(cursor.getColumnIndex(KEY_NAME_EN)));
            existingCard.setName_pt(cursor.getString(cursor.getColumnIndex(KEY_NAME_PT)));
            existingCard.setId_edition(cursor.getInt(cursor.getColumnIndex(KEY_ID_EDITION)));
            existingCard.setQuantity(cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)));
            existingCard.setFoil(cursor.getString(cursor.getColumnIndex(KEY_FOIL)));
        } else {
            existingCard.setId(0);
            existingCard.setName_en("");
            existingCard.setName_pt("");
            existingCard.setId_edition(0);
            existingCard.setQuantity(0);
            existingCard.setFoil("N");
        }

        cursor.close();
        return existingCard;
    }
}

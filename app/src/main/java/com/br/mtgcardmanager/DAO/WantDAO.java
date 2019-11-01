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
import static com.br.mtgcardmanager.Helper.DatabaseHelper.TABLE_WANT;

public class WantDAO {

    /**
     * Inserts a card into table want
     * @param db
     * @param card
     * @return
     */
    public Long insertWantCard(SQLiteDatabase db, Card card){
        ContentValues values;
        long          cardId;

        values = new ContentValues();

        values.put(KEY_NAME_EN, card.getName_en());
        values.put(KEY_NAME_PT, card.getName_pt());
        values.put(KEY_ID_EDITION, card.getId_edition());
        values.put(KEY_QUANTITY, card.getQuantity());
        values.put(KEY_FOIL, card.getFoil());

        // insert row
        cardId = db.insert(TABLE_WANT, null, values);

        return cardId;
    }

    /**
     * Returns a list of all want cards
     * @param db
     * @return
     */
    public ArrayList<Card> getAllWantCards(SQLiteDatabase db){
        ArrayList<Card> cards       = new ArrayList<>();
        String          selectQuery = "";
        Cursor          cursor      = null;

        Log.e(LOG, selectQuery);
        selectQuery = "SELECT * FROM " + TABLE_WANT + " ORDER BY " + KEY_NAME_PT;
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
     * Deletes a want card
     * @param db
     * @param id_want_card
     * @return
     */
    public int deleteWantCard(SQLiteDatabase db, long id_want_card){
        int rowsAffected;

        rowsAffected = db.delete(TABLE_WANT, KEY_ID + " = ?", new String[] { String.valueOf(id_want_card) });
        db.close();

        return rowsAffected;
    }

    /**
     * Deletes all want cards
     * @param db
     */
    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_WANT, null, null);
        db.close();
    }

    /**
     * Checks if a specific card already exists in table want
     * @param db
     * @param name_en
     * @param id_edition
     * @param foil
     * @return
     */
    public Card checkIfWantCardExists(SQLiteDatabase db, String name_en, int id_edition, String foil){
        String selectQuery;
        Cursor cursor;
        Card   existingCard;

        selectQuery = "SELECT *" +
                " FROM " + TABLE_WANT +
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
package com.br.mtgcardmanager.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.br.mtgcardmanager.Model.Card;

import java.util.ArrayList;
import java.util.List;

import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_ID;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_NAME_EN;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_NAME_PT;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.LOG;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.TABLE_ALL_CARDS;

public class AllCardsDAO {
    private long cardId;

    /**
     * Inserts a list of <Card> into table all_cards
     * @param db
     * @param allCards
     * @return
     */
    public Long insertAll(SQLiteDatabase db, List<Card> allCards){
        ContentValues values;

        db.beginTransaction();
        try {
            for (Card card : allCards) {
                values = new ContentValues();
                values.put(KEY_NAME_EN, card.getName_en());
                values.put(KEY_NAME_PT, card.getName_pt());

                // insert row
                cardId = db.insert(TABLE_ALL_CARDS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return cardId;
    }

    /**
     * Searches and returns all cards from table all_cards
     * @param db
     * @return
     */
    public ArrayList<Card> getAll(SQLiteDatabase db){
        ArrayList<Card> cards       = new ArrayList<>();
        String          selectQuery = "";
        Cursor          cursor      = null;

        Log.e(LOG, selectQuery);
        selectQuery = "SELECT * FROM " + TABLE_ALL_CARDS + " ORDER BY " + KEY_NAME_EN;
        cursor      = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                Card card = new Card();
                card.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                card.setName_en(cursor.getString(cursor.getColumnIndex(KEY_NAME_EN)));
                card.setName_pt(cursor.getString(cursor.getColumnIndex(KEY_NAME_PT)));

                cards.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cards;
    }

    /**
     * Deletes everything from table all_cards
     * @param db
     */
    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_ALL_CARDS, null, null);
        db.close();
    }

    /**
     * Searches a card by name
     * @param db
     * @param name
     * @return
     */
    public List<Card> getByName(SQLiteDatabase db, String name) {
        List<Card> cards;
        String     selectQuery;
        Cursor     cursor;

        selectQuery = "SELECT * " +
                        " FROM " + TABLE_ALL_CARDS +
                        " WHERE " + KEY_NAME_EN + " LIKE '%" + name.toLowerCase() + "%'" +
                        " OR " + KEY_NAME_PT + " LIKE '%" + name.toLowerCase() + "%'" +
                        " LIMIT 10";

        cursor = db.rawQuery(selectQuery, null);
        cards  = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                card.setName_en(cursor.getString(cursor.getColumnIndex(KEY_NAME_EN)));
                card.setName_pt(cursor.getString(cursor.getColumnIndex(KEY_NAME_PT)));

                cards.add(card);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cards;
    }

    /**
     * Returns the total number os cards in table all_cards
     * @param db
     * @return
     */
    public int getAllCardsCount(SQLiteDatabase db) {
        String selectQuery;
        Cursor cursor;
        int    count;

        selectQuery = "SELECT count(id) as total" +
                " FROM " + TABLE_ALL_CARDS;

        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("total"));
            cursor.close();
            return count;
        } else {
            cursor.close();
            return 0;
        }


    }
}

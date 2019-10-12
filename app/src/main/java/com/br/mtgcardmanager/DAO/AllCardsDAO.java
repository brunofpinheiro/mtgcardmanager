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
    public long card_id;

    public Long insertAll(SQLiteDatabase db, List<Card> allCards){
        ContentValues values;

        db.beginTransaction();
        try {
            for (Card card : allCards) {
                values = new ContentValues();
                values.put(KEY_NAME_EN, card.getName_en());
                values.put(KEY_NAME_PT, card.getName_pt());

                // insert row
                card_id = db.insert(TABLE_ALL_CARDS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return card_id;
    }

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

//    public int deleteHaveCard(SQLiteDatabase db, long id_have_card){
//        int rowsAffected;
//
//        rowsAffected = db.delete(TABLE_HAVE, KEY_ID + " = ?", new String[]{String.valueOf(id_have_card)});
//        db.close();
//
//        return rowsAffected;
//    }
//
    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_ALL_CARDS, null, null);
        db.close();
    }



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

    public int getAllCardsCount(SQLiteDatabase db) {
        String selectQuery;
        Cursor cursor;
        int count;

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
//
//    public Card checkIfHaveCardExists(SQLiteDatabase db, String name_en, int id_edition, String foil){
//        String selectQuery = "SELECT *" +
//                " FROM " + TABLE_HAVE +
//                " WHERE " + KEY_NAME_EN + " = '" + name_en + "'" +
//                " AND " + KEY_ID_EDITION + " = " + id_edition +
//                " AND " + KEY_FOIL + " = '" + foil + "'";
//
//        Log.e(LOG, selectQuery);
//
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        if (cursor != null){
//            cursor.moveToFirst();
//        }
//
//        Card existingCard = new Card();
//
//        if (cursor.getAllCardsCount() > 0) {
//            existingCard.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
//            existingCard.setName_en(cursor.getString(cursor.getColumnIndex(KEY_NAME_EN)));
//            existingCard.setName_pt(cursor.getString(cursor.getColumnIndex(KEY_NAME_PT)));
//            existingCard.setId_edition(cursor.getInt(cursor.getColumnIndex(KEY_ID_EDITION)));
//            existingCard.setQuantity(cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)));
//            existingCard.setFoil(cursor.getString(cursor.getColumnIndex(KEY_FOIL)));
//        } else {
//            existingCard.setId(0);
//            existingCard.setName_en("");
//            existingCard.setName_pt("");
//            existingCard.setId_edition(0);
//            existingCard.setQuantity(0);
//            existingCard.setFoil("N");
//        }
//
//        cursor.close();
//        return existingCard;
//    }
}

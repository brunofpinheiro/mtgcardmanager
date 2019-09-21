package com.br.mtgcardmanager.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Model.HaveCard;

import org.json.JSONArray;
import org.json.JSONObject;

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
    public long card_id;

    public Long insertHaveCard(SQLiteDatabase db, HaveCard haveCard){
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_EN, haveCard.getName_en());
        values.put(KEY_NAME_PT, haveCard.getName_pt());
        values.put(KEY_ID_EDITION, haveCard.getId_edition());
        values.put(KEY_QUANTITY, haveCard.getQuantity());
        values.put(KEY_FOIL, haveCard.getFoil());

        // insert row
        card_id = db.insert(TABLE_HAVE, null, values);

        return card_id;
    }

    public ArrayList<HaveCard> getAllHaveCards(SQLiteDatabase db){
        ArrayList<HaveCard> cards       = new ArrayList<>();
        String              selectQuery = "";
        Cursor              cursor      = null;

        Log.e(LOG, selectQuery);
        selectQuery = "SELECT * FROM " + TABLE_HAVE + " ORDER BY " + KEY_NAME_PT;
        cursor      = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                HaveCard card = new HaveCard();
                card.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                card.setName_en(cursor.getString(cursor.getColumnIndex(KEY_NAME_EN)));
                card.setName_pt(cursor.getString(cursor.getColumnIndex(KEY_NAME_PT)));
                card.setId_edition(cursor.getInt(cursor.getColumnIndex(KEY_ID_EDITION)));
                card.setQuantity(cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)));
                card.setFoil(cursor.getString(cursor.getColumnIndex(KEY_FOIL)));

                // adding to have cards list
                cards.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cards;
    }

    public void deleteHaveCard(SQLiteDatabase db, long id_have_card){
        db.delete(TABLE_HAVE, KEY_ID + " = ?", new String[] { String.valueOf(id_have_card) });
        db.close();
    }

    public HaveCard checkIfHaveCardExists(SQLiteDatabase db, String name_en, int id_edition, String foil){
        String selectQuery = "SELECT *" +
                " FROM " + TABLE_HAVE +
                " WHERE " + KEY_NAME_EN + " = '" + name_en + "'" +
                " AND " + KEY_ID_EDITION + " = " + id_edition +
                " AND " + KEY_FOIL + " = '" + foil + "'";

        Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        HaveCard existingCard = new HaveCard();

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

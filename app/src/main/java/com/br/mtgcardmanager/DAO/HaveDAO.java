package com.br.mtgcardmanager.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Model.HaveCards;

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

    public Long insertHaveCard(SQLiteDatabase db, HaveCards haveCard){
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

    public ArrayList<HaveCards> getAllHaveCards(SQLiteDatabase db){
        ArrayList<HaveCards> cards       = new ArrayList<>();
        String               selectQuery = "";
        Cursor               cursor      = null;

        Log.e(LOG, selectQuery);
        selectQuery = "SELECT * FROM " + TABLE_HAVE + " ORDER BY " + KEY_NAME_PT;
        cursor      = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                HaveCards card = new HaveCards();
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

    public HaveCards checkIfHaveCardExists(SQLiteDatabase db, String name_en, int id_edition, String foil){
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

        HaveCards existingCard = new HaveCards();

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

    public JSONArray exportHaveToJSON(Context context) {

//        String myPath = DB_PATH + DB_NAME;// Set path to your database
//        String myTable = TABLE_NAME;//Set name of your table
//or you can use `context.getDatabasePath("my_db_test.db")`

        String myPath = context.getDatabasePath(DatabaseHelper.DATABASE_NAME).getAbsolutePath();
        String myTable = TABLE_HAVE;

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor      = myDataBase.rawQuery(searchQuery, null );

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for(int i = 0 ;  i < totalColumn ; i++) {
                if(cursor.getColumnName(i) != null) {
                    try {
                        if(cursor.getString(i) != null) {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        }
                        else {
                            rowObject.put(cursor.getColumnName(i) , "");
                        }
                    } catch( Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }
}

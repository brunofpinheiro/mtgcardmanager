package com.br.mtgcardmanager.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.br.mtgcardmanager.DAO.EditionDAO;
import com.br.mtgcardmanager.DAO.HaveDAO;
import com.br.mtgcardmanager.DAO.WantDAO;
import com.br.mtgcardmanager.Model.Editions;
import com.br.mtgcardmanager.Model.HaveCards;
import com.br.mtgcardmanager.Model.WantCards;
import com.br.mtgcardmanager.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Bruno on 23/07/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public               ArrayList<Editions> currentEditions;
    public               long                editionsCount;
    public               long                card_id;
    public static        SQLiteDatabase      db;
    private static       DatabaseHelper      sInstance;
    public static  final String              LOG = "DatabaseHelper";
    private static final int                 DATABASE_VERSION = 1;
    public static  final String              DATABASE_NAME     = "mtgCardManager";

    //Table names
    public static  final String              TABLE_HAVE        = "have";
    public static  final String              TABLE_WANT        = "want";
    public static  final String              TABLE_EDITIONS    = "editions";

    //Commom column names
    public static  final String              KEY_ID            = "id";
    public static  final String              KEY_NAME_EN       = "name_en";
    public static  final String              KEY_NAME_PT       = "name_pt";
    public static  final String              KEY_ID_EDITION    = "id_edition";
    public static  final String              KEY_QUANTITY      = "quantity";
    public static  final String              KEY_FOIL          = "foil";

    //EDITIONS Table - specific column names
    public static  final String              KEY_EDITION_SHORT = "edition_short";
    public static  final String              KEY_EDITION       = "edition";
    public static  final String              KEY_EDITION_PT    = "edition_pt";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // Create statements
    // HAVE Table Create Statement
    private final String CREATE_TABLE_HAVE = "CREATE TABLE "
            + TABLE_HAVE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME_EN +    " TEXT, "
            + KEY_NAME_PT +    " TEXT, "
            + KEY_ID_EDITION + " INTEGER, "
            + KEY_QUANTITY +   " INTEGER, "
            + KEY_FOIL +       " TEXT, "
            + "FOREIGN KEY(id_edition) REFERENCES editions(id))";

    // WANT Table Create Statement
    private final String CREATE_TABLE_WANT = "CREATE TABLE "
            + TABLE_WANT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME_EN +    " TEXT, "
            + KEY_NAME_PT +    " TEXT, "
            + KEY_ID_EDITION + " INTEGER, "
            + KEY_QUANTITY +   " INTEGER, "
            + KEY_FOIL +       " TEXT, "
            + "FOREIGN KEY(id_edition) REFERENCES editions(id))";

    // EDITIONS Table Create Statement
    public static final String CREATE_TABLE_EDITIONS = "CREATE TABLE "
            + TABLE_EDITIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_EDITION_SHORT + " TEXT, "
            + KEY_EDITION + " TEXT, "
            + KEY_EDITION_PT + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(CREATE_TABLE_HAVE);
        db.execSQL(CREATE_TABLE_WANT);
        db.execSQL(CREATE_TABLE_EDITIONS);
        insertAllEditions();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HAVE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WANT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDITIONS);

        // create new tables
        onCreate(db);
    }

    public SQLiteDatabase openDB(){
        db = this.getWritableDatabase();
        return db;
    }

    // closing database
    public void closeDB(SQLiteDatabase db) {
        if (db != null && db.isOpen())
            db.close();
    }


    //********************************
    //***** HAVE Table functions *****
    //********************************
    public Long insertHaveCard(HaveCards haveCard){
        db              = this.getWritableDatabase();
        HaveDAO haveDAO = new HaveDAO();

        card_id = haveDAO.insertHaveCard(db, haveCard);

        return card_id;
    }

    public ArrayList<HaveCards> getAllHaveCards(){
        db              = getReadableDatabase();
        HaveDAO haveDAO = new HaveDAO();

        return haveDAO.getAllHaveCards(db);
    }

    public void deleteHaveCard(long id_have_card){
        db              = this.getWritableDatabase();
        HaveDAO haveDAO = new HaveDAO();

        haveDAO.deleteHaveCard(db, id_have_card);
    }

    public HaveCards checkIfHaveCardExists(String name_en, int id_edition, String foil){
        db              = this.getReadableDatabase();
        HaveDAO haveDAO = new HaveDAO();

        return haveDAO.checkIfHaveCardExists(db, name_en, id_edition, foil);
    }


    //********************************
    //***** WANT Table functions *****
    //********************************
    public Long insertWantCard(WantCards wantCard){
        db              = this.getWritableDatabase();
        WantDAO wantDAO = new WantDAO();

        card_id = wantDAO.insertWantCard(db, wantCard);

        return card_id;
    }

    public ArrayList<WantCards> getAllWantCards(){
        db              = getReadableDatabase();
        WantDAO wantDAO = new WantDAO();

        return wantDAO.getAllWantCards(db);
    }

    public void deleteWantCard(long id_want_card){
        db              = this.getWritableDatabase();
        WantDAO wantDAO = new WantDAO();

        wantDAO.deleteWantCard(db, id_want_card);
    }

    public WantCards checkIfWantCardExists(String name_en, int id_edition, String foil){
        db              = this.getReadableDatabase();
        WantDAO wantDAO = new WantDAO();

        return wantDAO.checkIfWantCardExists(db, name_en, id_edition, foil);
    }


    //********************************
    //****** Commom  Functions *******
    //********************************
    public Long updateCardQuantity(String table_name, int id, int quantity){
        db = this.getWritableDatabase();

        String where = KEY_ID + " = " + id;

        ContentValues values = new ContentValues();
        values.put(KEY_QUANTITY, quantity);

        // update card's quantity
        card_id = db.update(table_name, values, where, null);

        return card_id;
    }


    //********************************
    //*** EDITIONS Table functions ***
    //********************************
    public Long getEditionsQty(){
        db = this.getReadableDatabase();
        EditionDAO editionDAO = new EditionDAO();

        return editionDAO.getEditionsQty(db);
    }

    public Editions getSingleEdition(Context context, String selectedEdition) {
        db                    = this.getReadableDatabase();
        EditionDAO editionDAO = new EditionDAO();

        return editionDAO.getSingleEdition(db, context, selectedEdition);
    }

    public Long insertAllEditions(){
        db                    = this.getReadableDatabase();
        EditionDAO editionDAO = new EditionDAO();

        return editionDAO.insertAllEditions(db);
    }

    public String getEditionById(Context context, long edition_id) {
        db                    = this.getReadableDatabase();
        EditionDAO editionDAO = new EditionDAO();

        return editionDAO.getEditionById(db, context, edition_id);
    }

    public ArrayList<Editions> populateEditionsList() {
        EditionDAO editionDAO = new EditionDAO();

        currentEditions = editionDAO.populateEditionsList();

        return currentEditions;
    }
}
package com.br.mtgcardmanager.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

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
    public ArrayList<Editions>    currentEditions;
    public long                   editionsCount;
    public long                   card_id;
    private SQLiteDatabase        db;
    private static DatabaseHelper sInstance;
    private Editions              edition;
    private Cursor                cursor;

    private static final String LOG = "DatabaseHelper";

    private static final int    DATABASE_VERSION = 1;
    private static final String DATABASE_NAME    = "mtgCardManager";

    //Table names
    private static final String TABLE_HAVE     = "have";
    private static final String TABLE_WANT     = "want";
    private static final String TABLE_EDITIONS = "editions";

    //Commom column names
    private static final String KEY_ID         = "id";
    private static final String KEY_NAME_EN    = "name_en";
    private static final String KEY_NAME_PT    = "name_pt";
    private static final String KEY_ID_EDITION = "id_edition";
    private static final String KEY_QUANTITY   = "quantity";
    private static final String KEY_FOIL       = "foil";

    //HAVE Table - specific column names

    //WANT Table - specific column names


    //EDITIONS Table - specific column names
    private static final String KEY_EDITION_SHORT = "edition_short";
    private static final String KEY_EDITION       = "edition";

    // Create statements
    // HAVE Table Create Statement
    private static final String CREATE_TABLE_HAVE = "CREATE TABLE "
            + TABLE_HAVE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME_EN +    " TEXT, "
            + KEY_NAME_PT +    " TEXT, "
            + KEY_ID_EDITION + " INTEGER, "
            + KEY_QUANTITY +   " INTEGER, "
            + KEY_FOIL +       " TEXT, "
            + "FOREIGN KEY(id_edition) REFERENCES editions(id))";

    // WANT Table Create Statement
    private static final String CREATE_TABLE_WANT = "CREATE TABLE "
            + TABLE_WANT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME_EN +    " TEXT, "
            + KEY_NAME_PT +    " TEXT, "
            + KEY_ID_EDITION + " INTEGER, "
            + KEY_QUANTITY +   " INTEGER, "
            + KEY_FOIL +       " TEXT, "
            + "FOREIGN KEY(id_edition) REFERENCES editions(id))";

    // EDITIONS Table Create Statement
    private static final String CREATE_TABLE_EDITIONS = "CREATE TABLE "
            + TABLE_EDITIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_EDITION_SHORT + " TEXT, "
            + KEY_EDITION + " TEXT" + ")";

    // constructor
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
//        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


    //********************************
    //***** HAVE Table functions *****
    //********************************
    public Long insertHaveCard(HaveCards haveCard){
        db = this.getWritableDatabase();

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

//    public HaveCards getSingleHaveCard(long have_card_id){
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String selectQuery = "SELECT  * FROM " + TABLE_HAVE
//                + " WHERE " + KEY_ID + " = " + have_card_id;
//
//        Log.e(LOG, selectQuery);
//
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        if (cursor != null){
//            cursor.moveToFirst();
//        }
//
//        HaveCards card = new HaveCards();
//        card.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
//        card.setName_en(cursor.getString(cursor.getColumnIndex(KEY_NAME_EN)));
//        card.setName_pt(cursor.getString(cursor.getColumnIndex(KEY_NAME_PT)));
//        card.setId_edition(cursor.getInt(cursor.getColumnIndex(KEY_ID_EDITION)));
//        card.setQuantity(cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)));
//
//        cursor.close();
//        return card;
//    }

    public ArrayList<HaveCards> getAllHaveCards(){
        db = getReadableDatabase();

        ArrayList<HaveCards> cards = new ArrayList<>();
        String selectQuery         = "SELECT * FROM " + TABLE_HAVE + " ORDER BY " + KEY_NAME_PT;
        Log.e(LOG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

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

    public void deleteHaveCard(long id_have_card){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HAVE, KEY_ID + " = ?",
                new String[] { String.valueOf(id_have_card) });
        db.close();
    }

    public HaveCards checkIfHaveCardExists(String name_en, int id_edition, String foil){
        db = this.getReadableDatabase();

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



    //********************************
    //***** WANT Table functions *****
    //********************************
    public Long insertWantCard(WantCards wantCard){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_EN, wantCard.getName_en());
        values.put(KEY_NAME_PT, wantCard.getName_pt());
        values.put(KEY_ID_EDITION, wantCard.getId_edition());
        values.put(KEY_QUANTITY, wantCard.getQuantity());
        values.put(KEY_FOIL, wantCard.getFoil());

        // insert row
        card_id = db.insert(TABLE_WANT, null, values);

        return card_id;
    }

    public ArrayList<WantCards> getAllWantCards(){
        db = getReadableDatabase();

        ArrayList<WantCards> cards = new ArrayList<>();
        String selectQuery         = "SELECT * FROM " + TABLE_WANT + " ORDER BY " + KEY_NAME_PT;
        Log.e(LOG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                WantCards card = new WantCards();
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

    public void deleteWantCard(long id_want_card){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WANT, KEY_ID + " = ?",
                new String[] { String.valueOf(id_want_card) });
        db.close();
    }

    public WantCards checkIfWantCardExists(String name_en, int id_edition, String foil){
        db = this.getReadableDatabase();

        String selectQuery = "SELECT *" +
                " FROM " + TABLE_WANT +
                " WHERE " + KEY_NAME_EN + " = '" + name_en + "'" +
                " AND " + KEY_ID_EDITION + " = " + id_edition +
                " AND " + KEY_FOIL + " = '" + foil + "'";

        Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        WantCards existingCard = new WantCards();

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
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteStatement stmt = db.compileStatement("SELECT COUNT(*) FROM editions");

        editionsCount = stmt.simpleQueryForLong();

        return editionsCount;
    }

    public Editions getSingleEdition(Context context, String selectedEdition){
        db = this.getReadableDatabase();
        try {
            String selectQuery = "SELECT " + KEY_ID  + ", " + KEY_EDITION + " FROM " + TABLE_EDITIONS
                    + " WHERE " + KEY_EDITION + " = '" + selectedEdition + "'";

            Log.e(LOG, selectQuery);

            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null){
                cursor.moveToFirst();
            }
            edition = new Editions();
            edition.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            edition.setEdition(cursor.getString(cursor.getColumnIndex(KEY_EDITION)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.edition_not_found, Toast.LENGTH_LONG).show();
        }
        cursor.close();
        return edition;
    }

    public Long insertAllEditions(){

        long edition_id = 0;

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDITIONS);
        db.execSQL(CREATE_TABLE_EDITIONS);
        currentEditions = new ArrayList<>();
        if (currentEditions.size() == 0) {
            populateEditionsList();
        }

        ContentValues values = new ContentValues();
        for (int i=0; i < currentEditions.size(); i++){
            values.put(KEY_EDITION_SHORT, currentEditions.get(i).getEdition_short());
            values.put(KEY_EDITION, currentEditions.get(i).getEdition());
            // insert row
            edition_id = db.insert(TABLE_EDITIONS, null, values);
        }

        return edition_id;
    }

    public String getEditionById(Context context, long edition_id) {
        String edition_name = "";
        try {
            SQLiteDatabase db = this.getReadableDatabase();


            String selectQuery = "SELECT " + KEY_EDITION  + " FROM " + TABLE_EDITIONS
                    + " WHERE " + TABLE_EDITIONS + "." + KEY_ID + " = " + edition_id;

            Log.e(LOG, selectQuery);

            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null){
                cursor.moveToFirst();
            }

            edition_name = cursor.getString(cursor.getColumnIndex(KEY_EDITION));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.edition_not_found, Toast.LENGTH_LONG).show();
        }
        cursor.close();
        return edition_name;
    }

    public JSONArray exportHaveToJSON(Context context)
    {

//        String myPath = DB_PATH + DB_NAME;// Set path to your database
//        String myTable = TABLE_NAME;//Set name of your table
//or you can use `context.getDatabasePath("my_db_test.db")`

        String myPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
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

    public void populateEditionsList() {
        currentEditions = new ArrayList<>();

        // Promo
        currentEditions.add(new Editions("FNM", "Friday Night Magic"));
        currentEditions.add(new Editions("PTC", "Prerelease Events"));
        currentEditions.add(new Editions("GDC", "Magic Game Day Cards"));
        // Decks
        currentEditions.add(new Editions("MD1", "Modern Event Deck"));
        currentEditions.add(new Editions("PD3", "Premium Deck Series: Graveborn"));
        currentEditions.add(new Editions("PD2", "Premium Deck Series: Fire and Lightning"));
        currentEditions.add(new Editions("H09", "Premium Deck Series: Slivers"));
        // Casual Suplements
        currentEditions.add(new Editions("HOP", "Planechase"));
        currentEditions.add(new Editions("CMD", "Commander"));
        currentEditions.add(new Editions("PC2", "Planechase 2012 Edition"));
        currentEditions.add(new Editions("CMA", "Commander’s Arsenal"));
        currentEditions.add(new Editions("C13", "Commander 2013"));
        currentEditions.add(new Editions("CNS", "Conspiracy"));
        currentEditions.add(new Editions("C14", "Commander 2014"));
        currentEditions.add(new Editions("C15", "Commander 2015"));
        // Portal / Starter Sets
        currentEditions.add(new Editions("POR", "Portal"));
        currentEditions.add(new Editions("PO2", "Portal Second Age"));
        // Reprint Sets
        currentEditions.add(new Editions("CHR", "Chronicles"));
        currentEditions.add(new Editions("MMA", "Modern Masters"));
        currentEditions.add(new Editions("MM2", "Modern Masters 2015"));
        currentEditions.add(new Editions("EMA", "Eternal Masters"));
        // Un-Sets
        currentEditions.add(new Editions("UNG", "Unglued"));
        currentEditions.add(new Editions("UNH", "Unhinged"));
        // Duel Decks
        currentEditions.add(new Editions("EVG", "Duel Decks: Elves vs. Goblins"));
        currentEditions.add(new Editions("DD2", "Duel Decks: Jace vs. Chandra"));
        currentEditions.add(new Editions("DDC", "Duel Decks: Divine vs. Demonic"));
        currentEditions.add(new Editions("DDD", "Duel Decks: Garruk vs. Liliana"));
        currentEditions.add(new Editions("DDE", "Duel Decks: Phyrexia vs. The Coalition"));
        currentEditions.add(new Editions("DDF", "Duel Decks: Elspeth vs. Tezzeret"));
        currentEditions.add(new Editions("DDG", "Duel Decks: Knights vs. Dragons"));
        currentEditions.add(new Editions("DDH", "Duel Decks: Ajani vs. Nicol Bolas"));
        currentEditions.add(new Editions("DDI", "Duel Decks: Venser vs. Koth"));
        currentEditions.add(new Editions("DDJ", "Duel Decks: Izzet vs. Golgari"));
        currentEditions.add(new Editions("DDK", "Duel Decks: Sorin vs. Tibalt"));
        currentEditions.add(new Editions("HVM", "Duel Decks: Heroes vs. Monsters"));
        currentEditions.add(new Editions("DDM", "Duel Decks: Jace vs. Vraska"));
        currentEditions.add(new Editions("DDN", "Duel Decks: Speed vs. Cunning"));
        currentEditions.add(new Editions("DD3", "Duel Decks: Anthology"));
        currentEditions.add(new Editions("EVK", "Duel Decks: Elspeth vs. Kiora"));
        currentEditions.add(new Editions("DDP", "Duel Decks: Zendikar vs. Eldrazi"));
        currentEditions.add(new Editions("DDQ", "Duel Decks: Blessed vs. Cursed"));
        // Older Editions
        currentEditions.add(new Editions("LEA", "Limited Edition Alpha"));
        currentEditions.add(new Editions("LEB", "Limited Edition Beta"));
        currentEditions.add(new Editions("ARN", "Arabian Nights"));
        currentEditions.add(new Editions("ATQ", "Antiquities"));
        currentEditions.add(new Editions("3ED", "Revised Edition"));
        currentEditions.add(new Editions("LEG", "Legends"));
        currentEditions.add(new Editions("DRK", "The Dark"));
        currentEditions.add(new Editions("FEM", "Fallen Empires"));
        currentEditions.add(new Editions("4ED", "Fourth Edition"));
        // Ice Age Block
        currentEditions.add(new Editions("ICE", "Ice Age"));
        currentEditions.add(new Editions("ALL", "Alliances"));
        currentEditions.add(new Editions("CSP", "Coldsnap"));
        // Homelands
        currentEditions.add(new Editions("HML", "Homelands"));
        // Mirage Block
        currentEditions.add(new Editions("MIR", "Mirage"));
        currentEditions.add(new Editions("VIS", "Visions"));
        currentEditions.add(new Editions("WTH", "Weatherlight"));
        // Core Set
        currentEditions.add(new Editions("5ED", "Fifth Edition"));
        currentEditions.add(new Editions("6ED", "Classic Sixth Edition"));
        currentEditions.add(new Editions("7ED", "Seventh Edition"));
        currentEditions.add(new Editions("8ED", "Eighth Edition"));
        currentEditions.add(new Editions("9ED", "Ninth Edition"));
        currentEditions.add(new Editions("10E", "Tenth Edition"));
        currentEditions.add(new Editions("M10", "Magic 2010"));
        currentEditions.add(new Editions("M11", "Magic 2011"));
        currentEditions.add(new Editions("M12", "Magic 2012"));
        currentEditions.add(new Editions("M13", "Magic 2013"));
        currentEditions.add(new Editions("M14", "Magic 2014"));
        currentEditions.add(new Editions("M15", "Magic 2015"));
        // Tempest Block
        currentEditions.add(new Editions("TMP", "Tempest"));
        currentEditions.add(new Editions("STH", "Stronghold"));
        currentEditions.add(new Editions("EXO", "Exodus"));
        // Urza Block
        currentEditions.add(new Editions("USG", "Urza’s Saga"));
        currentEditions.add(new Editions("ULG", "Urza’s Legacy"));
        currentEditions.add(new Editions("UDS", "Urza's Destiny"));
        // Masques Block
        currentEditions.add(new Editions("MMQ", "Mercadian Masques"));
        currentEditions.add(new Editions("NEM", "Nemesis"));
        currentEditions.add(new Editions("PCY", "Prophecy"));
        // Invasion Block
        currentEditions.add(new Editions("INV", "Invasion"));
        currentEditions.add(new Editions("PLS", "Planeshift"));
        currentEditions.add(new Editions("APC", "Apocalypse"));
        // Odyssey Block
        currentEditions.add(new Editions("ODY", "Odyssey"));
        currentEditions.add(new Editions("TOR", "Torment"));
        currentEditions.add(new Editions("JUD", "Judgment"));
        // Onslaught Block
        currentEditions.add(new Editions("ONS", "Onslaught"));
        currentEditions.add(new Editions("LGN", "Legions"));
        currentEditions.add(new Editions("SCG", "Scourge"));
        // Mirrodin Block
        currentEditions.add(new Editions("MRD", "Mirrodin"));
        currentEditions.add(new Editions("DST", "Darksteel"));
        currentEditions.add(new Editions("5DN", "Fifth Dawn"));
        // Kamigawa Block
        currentEditions.add(new Editions("CHK", "Champions of Kamigawa"));
        currentEditions.add(new Editions("BOK", "Betrayers of Kamigawa"));
        currentEditions.add(new Editions("SOK", "Saviors of Kamigawa"));
        // Ravnica
        currentEditions.add(new Editions("RAV", "Ravnica: City of Guilds"));
        currentEditions.add(new Editions("GPT", "Guildpact"));
        currentEditions.add(new Editions("DIS", "Dissension"));
        // Time Spiral Block
        currentEditions.add(new Editions("TSP", "Time Spiral"));
        currentEditions.add(new Editions("PLC", "Planar Chaos"));
        currentEditions.add(new Editions("FUT", "Future Sight"));
        // Lorwyn-Shadowmoor Block
        currentEditions.add(new Editions("LRW", "Lorwyn"));
        currentEditions.add(new Editions("MOR", "Morningtide"));
        currentEditions.add(new Editions("SHM", "Shadowmoor"));
        currentEditions.add(new Editions("EVE", "Eventide"));
        // Shards of Alara
        currentEditions.add(new Editions("ALA", "Shards of Alara"));
        currentEditions.add(new Editions("CON", "Conflux"));
        currentEditions.add(new Editions("ARB", "Alara Reborn"));
        // Zendikar Block
        currentEditions.add(new Editions("ZEN", "Zendikar"));
        currentEditions.add(new Editions("WWK", "Worldwake"));
        currentEditions.add(new Editions("ROE", "Rise of the Eldrazi"));
        // Scars of Mirrodin Block
        currentEditions.add(new Editions("SOM", "Scars of Mirrodin"));
        currentEditions.add(new Editions("MBS", "Mirrodin Besieged"));
        currentEditions.add(new Editions("NPH", "New Phyrexia"));
        // Innistrad Block
        currentEditions.add(new Editions("ISD", "Innistrad"));
        currentEditions.add(new Editions("DKA", "Dark Ascension"));
        currentEditions.add(new Editions("AVR", "Avacyn Restored"));
        // Return to Ravnica Block
        currentEditions.add(new Editions("RTR", "Return to Ravnica"));
        currentEditions.add(new Editions("GTC", "Gatecrash"));
        currentEditions.add(new Editions("DGM", "Dragon's Maze"));
        // Theros Block
        currentEditions.add(new Editions("THR", "Theros"));
        currentEditions.add(new Editions("BNG", "Born of the Gods"));
        currentEditions.add(new Editions("JOU", "Journey into Nyx"));
        // Khans of Tarkir Block
        currentEditions.add(new Editions("KTK", "Khans of Tarkir"));
        currentEditions.add(new Editions("FRF", "Fate Reforged"));
        currentEditions.add(new Editions("DTK", "Dragons of Tarkir"));
        // Magic Origins
        currentEditions.add(new Editions("ORI", "Magic Origins"));
        // Battle For Zendikar Block
        currentEditions.add(new Editions("BFZ", "Battle for Zendikar"));
        currentEditions.add(new Editions("OGW", "Oath of the Gatewatch"));
        // Shadows over Innistrad Block
        currentEditions.add(new Editions("SOI", "Shadows over Innistrad"));
        currentEditions.add(new Editions("EMN", "Eldritch Moon"));
        // Next Editions will be ordered by release date
    }
}
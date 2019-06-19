package com.br.mtgcardmanager.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Model.Editions;
import com.br.mtgcardmanager.R;

import java.util.ArrayList;

import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_EDITION;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_EDITION_PT;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_EDITION_SHORT;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_ID;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.LOG;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.TABLE_EDITIONS;

public class EditionDAO {
    public ArrayList<Editions> currentEditions;

    public Long getEditionsQty(SQLiteDatabase db){
        SQLiteStatement stmt          = db.compileStatement("SELECT COUNT(*) FROM editions");
        long            editionsCount = stmt.simpleQueryForLong();

        return editionsCount;
    }

    public Editions getSingleEdition(SQLiteDatabase db, Context context, String selectedEdition) {
        Cursor   cursor  = null;
        Editions edition = new Editions();

        try {
            String selectQuery = " SELECT " + KEY_ID  + ", " + KEY_EDITION + ", " + KEY_EDITION_PT
                    + " FROM " + TABLE_EDITIONS
                    + " WHERE " + KEY_EDITION + " LIKE '" + selectedEdition + "' "
                    + " OR " + KEY_EDITION_PT + " LIKE '" + selectedEdition + "' ";

            cursor = db.rawQuery(selectQuery, null);
            edition = new Editions();

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                edition.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                edition.setEdition(cursor.getString(cursor.getColumnIndex(KEY_EDITION)));
            } else {
                Toast.makeText(context, R.string.edition_not_found, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return edition;
    }

    public Long insertAllEditions(SQLiteDatabase db){
        long edition_id = 0;

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDITIONS);
        db.execSQL(DatabaseHelper.CREATE_TABLE_EDITIONS);
        currentEditions = new ArrayList<>();
        if (currentEditions.size() == 0) {
            populateEditionsList();
        }

        ContentValues values = new ContentValues();
        for (int i=0; i < currentEditions.size(); i++){
            values.put(KEY_EDITION_SHORT, currentEditions.get(i).getEdition_short());
            values.put(KEY_EDITION, currentEditions.get(i).getEdition());
            values.put(KEY_EDITION_PT, currentEditions.get(i).getEdition_pt());
            // insert row
            edition_id = db.insert(TABLE_EDITIONS, null, values);
        }

        return edition_id;
    }

    public ArrayList<Editions> populateEditionsList() {
        currentEditions = new ArrayList<>();

        // Promo
//        currentEditions.add(new Editions("FNM", "Friday Night Magic"));
//        currentEditions.add(new Editions("PTC", "Prerelease Events"));
//        currentEditions.add(new Editions("GDC", "Magic Game Day Cards"));
//        // Decks
//        currentEditions.add(new Editions("MD1", "Modern Event Deck"));
//        currentEditions.add(new Editions("PD3", "Premium Deck Series: Graveborn"));
//        currentEditions.add(new Editions("PD2", "Premium Deck Series: Fire and Lightning"));
//        currentEditions.add(new Editions("H09", "Premium Deck Series: Slivers"));
//        // Casual Suplements
//        currentEditions.add(new Editions("HOP", "Planechase"));
//        currentEditions.add(new Editions("CMD", "Commander"));
//        currentEditions.add(new Editions("PC2", "Planechase 2012 Edition"));
//        currentEditions.add(new Editions("CMA", "Commander’s Arsenal"));
//        currentEditions.add(new Editions("C13", "Commander 2013"));
//        currentEditions.add(new Editions("CNS", "Conspiracy"));
//        currentEditions.add(new Editions("C14", "Commander 2014"));
//        currentEditions.add(new Editions("C15", "Commander 2015"));
//        // Portal / Starter Sets
//        currentEditions.add(new Editions("POR", "Portal"));
//        currentEditions.add(new Editions("PO2", "Portal Second Age"));
//        // Reprint Sets
//        currentEditions.add(new Editions("CHR", "Chronicles"));
//        currentEditions.add(new Editions("MMA", "Modern Masters"));
//        currentEditions.add(new Editions("MM2", "Modern Masters 2015"));
//        currentEditions.add(new Editions("EMA", "Eternal Masters"));
//        // Un-Sets
//        currentEditions.add(new Editions("UNG", "Unglued"));
//        currentEditions.add(new Editions("UNH", "Unhinged"));
//        // Duel Decks
//        currentEditions.add(new Editions("EVG", "Duel Decks: Elves vs. Goblins"));
//        currentEditions.add(new Editions("DD2", "Duel Decks: Jace vs. Chandra"));
//        currentEditions.add(new Editions("DDC", "Duel Decks: Divine vs. Demonic"));
//        currentEditions.add(new Editions("DDD", "Duel Decks: Garruk vs. Liliana"));
//        currentEditions.add(new Editions("DDE", "Duel Decks: Phyrexia vs. The Coalition"));
//        currentEditions.add(new Editions("DDF", "Duel Decks: Elspeth vs. Tezzeret"));
//        currentEditions.add(new Editions("DDG", "Duel Decks: Knights vs. Dragons"));
//        currentEditions.add(new Editions("DDH", "Duel Decks: Ajani vs. Nicol Bolas"));
//        currentEditions.add(new Editions("DDI", "Duel Decks: Venser vs. Koth"));
//        currentEditions.add(new Editions("DDJ", "Duel Decks: Izzet vs. Golgari"));
//        currentEditions.add(new Editions("DDK", "Duel Decks: Sorin vs. Tibalt"));
//        currentEditions.add(new Editions("HVM", "Duel Decks: Heroes vs. Monsters"));
//        currentEditions.add(new Editions("DDM", "Duel Decks: Jace vs. Vraska"));
//        currentEditions.add(new Editions("DDN", "Duel Decks: Speed vs. Cunning"));
//        currentEditions.add(new Editions("DD3", "Duel Decks: Anthology"));
//        currentEditions.add(new Editions("EVK", "Duel Decks: Elspeth vs. Kiora"));
//        currentEditions.add(new Editions("DDP", "Duel Decks: Zendikar vs. Eldrazi"));
//        currentEditions.add(new Editions("DDQ", "Duel Decks: Blessed vs. Cursed"));
//        // Older Editions
//        currentEditions.add(new Editions("LEA", "Limited Edition Alpha"));
//        currentEditions.add(new Editions("LEB", "Limited Edition Beta"));
//        currentEditions.add(new Editions("ARN", "Arabian Nights"));
//        currentEditions.add(new Editions("ATQ", "Antiquities"));
//        currentEditions.add(new Editions("3ED", "Revised Edition"));
//        currentEditions.add(new Editions("LEG", "Legends"));
//        currentEditions.add(new Editions("DRK", "The Dark"));
//        currentEditions.add(new Editions("FEM", "Fallen Empires"));
//        currentEditions.add(new Editions("4ED", "Fourth Edition"));
//        // Ice Age Block
//        currentEditions.add(new Editions("ICE", "Ice Age"));
//        currentEditions.add(new Editions("ALL", "Alliances"));
//        currentEditions.add(new Editions("CSP", "Coldsnap"));
//        // Homelands
//        currentEditions.add(new Editions("HML", "Homelands"));
//        // Mirage Block
//        currentEditions.add(new Editions("MIR", "Mirage"));
//        currentEditions.add(new Editions("VIS", "Visions"));
//        currentEditions.add(new Editions("WTH", "Weatherlight"));
//        // Core Set
//        currentEditions.add(new Editions("5ED", "Fifth Edition"));
//        currentEditions.add(new Editions("6ED", "Classic Sixth Edition"));
//        currentEditions.add(new Editions("7ED", "Seventh Edition"));
//        currentEditions.add(new Editions("8ED", "Eighth Edition"));
//        currentEditions.add(new Editions("9ED", "Ninth Edition"));
//        currentEditions.add(new Editions("10E", "Tenth Edition"));
//        currentEditions.add(new Editions("M10", "Magic 2010"));
//        currentEditions.add(new Editions("M11", "Magic 2011"));
//        currentEditions.add(new Editions("M12", "Magic 2012"));
//        currentEditions.add(new Editions("M13", "Magic 2013"));
//        currentEditions.add(new Editions("M14", "Magic 2014"));
//        currentEditions.add(new Editions("M15", "Magic 2015"));
//        // Tempest Block
//        currentEditions.add(new Editions("TMP", "Tempest"));
//        currentEditions.add(new Editions("STH", "Stronghold"));
//        currentEditions.add(new Editions("EXO", "Exodus"));
//        // Urza Block
//        currentEditions.add(new Editions("USG", "Urza’s Saga"));
//        currentEditions.add(new Editions("ULG", "Urza’s Legacy"));
//        currentEditions.add(new Editions("UDS", "Urza's Destiny"));
//        // Masques Block
//        currentEditions.add(new Editions("MMQ", "Mercadian Masques"));
//        currentEditions.add(new Editions("NEM", "Nemesis"));
//        currentEditions.add(new Editions("PCY", "Prophecy"));
//        // Invasion Block
//        currentEditions.add(new Editions("INV", "Invasion"));
//        currentEditions.add(new Editions("PLS", "Planeshift"));
//        currentEditions.add(new Editions("APC", "Apocalypse"));
//        // Odyssey Block
//        currentEditions.add(new Editions("ODY", "Odyssey"));
//        currentEditions.add(new Editions("TOR", "Torment"));
//        currentEditions.add(new Editions("JUD", "Judgment"));
//        // Onslaught Block
//        currentEditions.add(new Editions("ONS", "Onslaught"));
//        currentEditions.add(new Editions("LGN", "Legions"));
//        currentEditions.add(new Editions("SCG", "Scourge"));
//        // Mirrodin Block
//        currentEditions.add(new Editions("MRD", "Mirrodin"));
//        currentEditions.add(new Editions("DST", "Darksteel"));
//        currentEditions.add(new Editions("5DN", "Fifth Dawn"));
//        // Kamigawa Block
//        currentEditions.add(new Editions("CHK", "Champions of Kamigawa"));
//        currentEditions.add(new Editions("BOK", "Betrayers of Kamigawa"));
//        currentEditions.add(new Editions("SOK", "Saviors of Kamigawa"));
//        // Ravnica
//        currentEditions.add(new Editions("RAV", "Ravnica: City of Guilds"));
//        currentEditions.add(new Editions("GPT", "Guildpact"));
//        currentEditions.add(new Editions("DIS", "Dissension"));
//        // Time Spiral Block
//        currentEditions.add(new Editions("TSP", "Time Spiral"));
//        currentEditions.add(new Editions("PLC", "Planar Chaos"));
//        currentEditions.add(new Editions("FUT", "Future Sight"));
//        // Lorwyn-Shadowmoor Block
//        currentEditions.add(new Editions("LRW", "Lorwyn"));
//        currentEditions.add(new Editions("MOR", "Morningtide"));
//        currentEditions.add(new Editions("SHM", "Shadowmoor"));
//        currentEditions.add(new Editions("EVE", "Eventide"));
//        // Shards of Alara
//        currentEditions.add(new Editions("ALA", "Shards of Alara"));
//        currentEditions.add(new Editions("CON", "Conflux"));
//        currentEditions.add(new Editions("ARB", "Alara Reborn"));
//        // Zendikar Block
//        currentEditions.add(new Editions("ZEN", "Zendikar"));
//        currentEditions.add(new Editions("WWK", "Worldwake"));
//        currentEditions.add(new Editions("ROE", "Rise of the Eldrazi"));
//        // Scars of Mirrodin Block
//        currentEditions.add(new Editions("SOM", "Scars of Mirrodin"));
//        currentEditions.add(new Editions("MBS", "Mirrodin Besieged"));
//        currentEditions.add(new Editions("NPH", "New Phyrexia"));
//        // Innistrad Block
//        currentEditions.add(new Editions("ISD", "Innistrad"));
//        currentEditions.add(new Editions("DKA", "Dark Ascension"));
//        currentEditions.add(new Editions("AVR", "Avacyn Restored"));
//        // Return to Ravnica Block
//        currentEditions.add(new Editions("RTR", "Return to Ravnica"));
//        currentEditions.add(new Editions("GTC", "Gatecrash"));
//        currentEditions.add(new Editions("DGM", "Dragon's Maze"));
//        // Theros Block
//        currentEditions.add(new Editions("THR", "Theros"));
//        currentEditions.add(new Editions("BNG", "Born of the Gods"));
//        currentEditions.add(new Editions("JOU", "Journey into Nyx"));
        // Khans of Tarkir Block
        currentEditions.add(new Editions("KTK", "Khans of Tarkir", "Khans de Tarkir"));
        currentEditions.add(new Editions("FRF", "Fate Reforged", "Destino Reescrito"));
        currentEditions.add(new Editions("DTK", "Dragons of Tarkir", "Dragões de Tarkir"));
        // Magic Origins
//        currentEditions.add(new Editions("ORI", "Magic Origins"));
//        // Battle For Zendikar Block
//        currentEditions.add(new Editions("BFZ", "Battle for Zendikar"));
//        currentEditions.add(new Editions("OGW", "Oath of the Gatewatch"));
//        // Shadows over Innistrad Block
//        currentEditions.add(new Editions("SOI", "Shadows over Innistrad"));
//        currentEditions.add(new Editions("EMN", "Eldritch Moon"));
//
//        // From here forward editions will be ordered by release date
//        currentEditions.add(new Editions("CN2", "Conspiracy: Take the Crown"));
//        currentEditions.add(new Editions("KLD", "Kaladesh"));
//        currentEditions.add(new Editions("C16", "Commander (2016 Edition)"));
//        currentEditions.add(new Editions("PCA", "Planechase Anthology"));
//        currentEditions.add(new Editions("AER", "Aether Revolt"));

        return currentEditions;
    }

    public String getEditionById(SQLiteDatabase db, Context context, long edition_id) {
        String edition_name = "";
        Cursor cursor       = null;

        try {
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
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return edition_name;
    }
}

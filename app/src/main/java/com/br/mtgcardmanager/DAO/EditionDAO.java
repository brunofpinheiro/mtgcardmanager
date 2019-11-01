package com.br.mtgcardmanager.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Model.Edition;
import com.br.mtgcardmanager.R;

import java.util.ArrayList;

import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_EDITION;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_EDITION_PT;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_EDITION_SHORT;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.KEY_ID;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.LOG;
import static com.br.mtgcardmanager.Helper.DatabaseHelper.TABLE_EDITIONS;

public class EditionDAO {
    public ArrayList<Edition> currentEditions;

    /**
     * Returns the total number os editions
     * @param db
     * @return
     */
    public Long getEditionsQty(SQLiteDatabase db){
        SQLiteStatement stmt          = db.compileStatement("SELECT COUNT(*) FROM editions");
        long            editionsCount = stmt.simpleQueryForLong();

        return editionsCount;
    }

    /**
     * Returns a single edition based on its name
     * @param db
     * @param context
     * @param selectedEdition
     * @return
     */
    public Edition getSingleEdition(SQLiteDatabase db, Context context, String selectedEdition) {
        Cursor  cursor  = null;
        Edition edition = new Edition();

        try {
            String selectQuery = " SELECT " + KEY_ID  + ", " + KEY_EDITION + ", " + KEY_EDITION_PT
                    + " FROM " + TABLE_EDITIONS
                    + " WHERE " + KEY_EDITION + " LIKE '" + selectedEdition + "' "
                    + " OR " + KEY_EDITION_PT + " LIKE '" + selectedEdition + "' ";

            cursor  = db.rawQuery(selectQuery, null);
            edition = new Edition();

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

    /**
     * Inserts a list of <Edition>
     * @param db
     * @return
     */
    public Long insertAllEditions(SQLiteDatabase db){
        long          editionId = 0;
        ContentValues values;

        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDITIONS);
            db.execSQL(DatabaseHelper.CREATE_TABLE_EDITIONS);
            currentEditions = new ArrayList<>();
            if (currentEditions.size() == 0) {
                populateEditionsList();
            }

            values = new ContentValues();
            db.beginTransaction();
            for (int i=0; i < currentEditions.size(); i++){
                values.put(KEY_EDITION_SHORT, currentEditions.get(i).getEdition_short());
                values.put(KEY_EDITION, currentEditions.get(i).getEdition());
                values.put(KEY_EDITION_PT, currentEditions.get(i).getEdition_pt());
                // insert row
                editionId = db.insert(TABLE_EDITIONS, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return editionId;
    }

    public ArrayList<Edition> populateEditionsList() {
        currentEditions = new ArrayList<>();

        // Promo
        currentEditions.add(new Edition("FNM", "Friday Night Magic", "Friday Night Magic"));
        currentEditions.add(new Edition("PTC", "Prerelease Events", "Prerelease Events"));
        currentEditions.add(new Edition("GDC", "Magic Game Day Cards", "Magic Game Day Cards"));
        // Casual Suplements
        currentEditions.add(new Edition("HOP", "Planechase", "Planechase (2009 Edition)"));
        currentEditions.add(new Edition("ARC", "Archenemy", "Archenemy"));
        currentEditions.add(new Edition("PC2", "Planechase (2012 Edition)", "Planechase (2012 Edition)"));
        currentEditions.add(new Edition("CNS", "Conspiracy", "Conspiracy"));
        currentEditions.add(new Edition("CN2", "Conspiracy: Take the Crown", "Conspiracy: Take the Crown"));
        currentEditions.add(new Edition("PCA", "Planechase Anthology", "Planechase Anthology"));
        currentEditions.add(new Edition("ANN", "Archenemy: Nicol Bolas", "Archenemy: Nicol Bolas"));
        // Commander
        currentEditions.add(new Edition("CMD", "Commander", "Commander (2011 Edition)"));
        currentEditions.add(new Edition("CMA", "Commander’s Arsenal", "Commander's Arsenal"));
        currentEditions.add(new Edition("C13", "Commander 2013", "Commander 2013"));
        currentEditions.add(new Edition("C14", "Commander 2014", "Commander 2014"));
        currentEditions.add(new Edition("C15", "Commander 2015", "Commander 2015"));
        currentEditions.add(new Edition("C16", "Commander (2016 Edition)", "Commander (2016 Edition)"));
        currentEditions.add(new Edition("CMA", "Commander Anthology", "Commander Anthology"));
        currentEditions.add(new Edition("C17", "Commander (2017 Edition)", "Commander (2017 Edition)"));
        currentEditions.add(new Edition("CM2", "Commander Anthology Volume II", "Commander Anthology Volume II"));
        currentEditions.add(new Edition("C18", "Commander (2018 Edition)", "Commander (2018 Edition)"));
        // Portal / Starter Sets
        currentEditions.add(new Edition("POR", "Portal", "Portal"));
        currentEditions.add(new Edition("PO2", "Portal Second Age", "Portal Second Age"));
        // Reprint Sets
        currentEditions.add(new Edition("CHR", "Chronicles", "Chronicles"));
        currentEditions.add(new Edition("MMA", "Modern Masters", "Modern Masters"));
        currentEditions.add(new Edition("MM2", "Modern Masters 2015", "Modern Masters 2015 Edition"));
        currentEditions.add(new Edition("EMA", "Eternal Masters", "Eternal Masters"));
        currentEditions.add(new Edition("MM3", "Modern Masters 2017 Edition", "Modern Masters 2017 Edition"));
        currentEditions.add(new Edition("IMA", "Iconic Masters", "Iconic Masters"));
        currentEditions.add(new Edition("M25", "Masters 25", "Masters 25"));
        currentEditions.add(new Edition("UMA", "Ultimate Masters", "Ultimate Masters"));
        // Un-Sets
        currentEditions.add(new Edition("UNG", "Unglued", "Unglued"));
        currentEditions.add(new Edition("UNH", "Unhinged", "Unhinged"));
        currentEditions.add(new Edition("UN3", "Unstable", "Unstable"));
        // Duel Decks
        currentEditions.add(new Edition("EVG", "Duel Decks: Elves vs. Goblins", "Duel Decks: Elves vs. Goblins"));
        currentEditions.add(new Edition("DD2", "Duel Decks: Jace vs. Chandra", "Duel Decks: Jace vs. Chandra"));
        currentEditions.add(new Edition("DDC", "Duel Decks: Divine vs. Demonic", "Duel Decks: Divine vs. Demonic"));
        currentEditions.add(new Edition("DDD", "Duel Decks: Garruk vs. Liliana", "Duel Decks: Garruk vs. Liliana"));
        currentEditions.add(new Edition("DDE", "Duel Decks: Phyrexia vs. The Coalition", "Duel Decks: Phyrexia vs. the Coalition"));
        currentEditions.add(new Edition("DDF", "Duel Decks: Elspeth vs. Tezzeret", "Duel Decks: Elspeth vs. Tezzeret"));
        currentEditions.add(new Edition("DDG", "Duel Decks: Knights vs. Dragons", "Duel Decks: Knights vs. Dragons"));
        currentEditions.add(new Edition("DDH", "Duel Decks: Ajani vs. Nicol Bolas", "Duel Decks: Ajani vs. Nicol Bolas"));
        currentEditions.add(new Edition("DDI", "Duel Decks: Venser vs. Koth", "Duel Decks: Venser vs. Koth"));
        currentEditions.add(new Edition("DDJ", "Duel Decks: Izzet vs. Golgari", "Duel Decks: Izzet vs. Golgari"));
        currentEditions.add(new Edition("DDK", "Duel Decks: Sorin vs. Tibalt", "Duel Decks: Sorin vs. Tibalt"));
        currentEditions.add(new Edition("HVM", "Duel Decks: Heroes vs. Monsters", "Duel Decks: Heroes vs. Monsters"));
        currentEditions.add(new Edition("DDM", "Duel Decks: Jace vs. Vraska", "Duel Decks: Jace vs. Vraska"));
        currentEditions.add(new Edition("DDN", "Duel Decks: Speed vs. Cunning", "Duel Decks: Speed vs. Cunning"));
        currentEditions.add(new Edition("DD3", "Duel Decks: Anthology", "Duel Decks: Anthology"));
        currentEditions.add(new Edition("EVK", "Duel Decks: Elspeth vs. Kiora", "Duel Decks: Elspeth vs. Kiora"));
        currentEditions.add(new Edition("DDP", "Duel Decks: Zendikar vs. Eldrazi", "Duel Decks: Zendikar vs. Eldrazi"));
        currentEditions.add(new Edition("DDQ", "Duel Decks: Blessed vs. Cursed", "Duel Decks: Blessed vs. Cursed"));
        currentEditions.add(new Edition("NVO", "Nissa vs. Ob Nixilis", "Nissa vs. Ob Nixilis"));
        currentEditions.add(new Edition("DDS", "Duel Decks: Mind vs. Might", "Duel Decks: Mind vs. Might"));
        currentEditions.add(new Edition("DDT", "Duel Decks: Merfolk vs. Goblins", "Duel Decks: Merfolk vs. Goblins"));
        currentEditions.add(new Edition("DDU", "Duel Deck: Elves vs. Inventors", "Duel Deck: Elves vs. Inventors"));
        // Older Edition
        currentEditions.add(new Edition("LEA", "Limited Edition Alpha", "Alpha"));
        currentEditions.add(new Edition("LEB", "Limited Edition Beta", "Beta"));
        currentEditions.add(new Edition("ARN", "Arabian Nights", "Arabian Nights"));
        currentEditions.add(new Edition("ATQ", "Antiquities", "Antiquities"));
        currentEditions.add(new Edition("3ED", "Revised Edition", "Revised Edition"));
        currentEditions.add(new Edition("LEG", "Legends", "Legends"));
        currentEditions.add(new Edition("DRK", "The Dark", "The Dark"));
        currentEditions.add(new Edition("FEM", "Fallen Empires", "Fallen Empires"));
        currentEditions.add(new Edition("4ED", "Fourth Edition", "Quarta Edição"));
        // Ice Age Block
        currentEditions.add(new Edition("ICE", "Ice Age", "Era Glacial"));
        currentEditions.add(new Edition("ALL", "Alliances", "Alianças"));
        currentEditions.add(new Edition("CSP", "Coldsnap", "Frente Fria"));
        // Homelands
        currentEditions.add(new Edition("HML", "Homelands", "Terras Natais"));
        // Mirage Block
        currentEditions.add(new Edition("MIR", "Mirage", "Miragem"));
        currentEditions.add(new Edition("VIS", "Visions", "Visões"));
        currentEditions.add(new Edition("WTH", "Weatherlight", "Alísios"));
        // Core Set
        currentEditions.add(new Edition("5ED", "Fifth Edition", "Quinta Edição"));
        currentEditions.add(new Edition("6ED", "Classic Sixth Edition", "Sexta Edição"));
        currentEditions.add(new Edition("7ED", "Seventh Edition", "Coleção Básica Sétima Edição"));
        currentEditions.add(new Edition("8ED", "Eighth Edition", "Oitava Edição"));
        currentEditions.add(new Edition("9ED", "Ninth Edition", "Nona Edição"));
        currentEditions.add(new Edition("10E", "Tenth Edition", "Coleção Básica Décima Edição"));
        currentEditions.add(new Edition("M10", "Magic 2010", "Coleção Básica 2010"));
        currentEditions.add(new Edition("M11", "Magic 2011", "Coleção Básica 2011"));
        currentEditions.add(new Edition("M12", "Magic 2012", "Coleção Básica 2012"));
        currentEditions.add(new Edition("M13", "Magic 2013", "Coleção Básica 2013"));
        currentEditions.add(new Edition("M14", "Magic 2014", "Coleção Básica de Magic 2014"));
        currentEditions.add(new Edition("M15", "Magic 2015", "Coleção Básica de Magic 2015"));
        currentEditions.add(new Edition("M19", "Core Set 2019", "COLEÇÃO BÁSICA 2019"));
        currentEditions.add(new Edition("M20", "Core Set 2020", "Coleção Básica 2020"));
        // Tempest Block
        currentEditions.add(new Edition("TMP", "Tempest", "Tempestade"));
        currentEditions.add(new Edition("STH", "Stronghold", "Fortaleza"));
        currentEditions.add(new Edition("EXO", "Exodus", "Êxodo"));
        // Urza Block
        currentEditions.add(new Edition("USG", "Urza’s Saga", "A Saga de Urza"));
        currentEditions.add(new Edition("ULG", "Urza’s Legacy", "O Legado de Urza"));
        currentEditions.add(new Edition("UDS", "Urza's Destiny", "O Destino de Urza"));
        // Masques Block
        currentEditions.add(new Edition("MMQ", "Mercadian Masques", "Máscaras de Mercádia"));
        currentEditions.add(new Edition("NEM", "Nemesis", "Nêmesis"));
        currentEditions.add(new Edition("PCY", "Prophecy", "Profecia"));
        // Invasion Block
        currentEditions.add(new Edition("INV", "Invasion", "Invasão"));
        currentEditions.add(new Edition("PLS", "Planeshift", "Conjunção"));
        currentEditions.add(new Edition("APC", "Apocalypse", "Apocalipse"));
        // Odyssey Block
        currentEditions.add(new Edition("ODY", "Odyssey", "Odisseia"));
        currentEditions.add(new Edition("TOR", "Torment", "Tormento"));
        currentEditions.add(new Edition("JUD", "Judgment", "Julgamento"));
        // Onslaught Block
        currentEditions.add(new Edition("ONS", "Onslaught", "Investida"));
        currentEditions.add(new Edition("LGN", "Legions", "Legiões"));
        currentEditions.add(new Edition("SCG", "Scourge", "Flagelo"));
        // Mirrodin Block
        currentEditions.add(new Edition("MRD", "Mirrodin", "Mirrodin"));
        currentEditions.add(new Edition("DST", "Darksteel", "Darksteel"));
        currentEditions.add(new Edition("5DN", "Fifth Dawn", "Aquinta Aurora"));
        // Kamigawa Block
        currentEditions.add(new Edition("CHK", "Champions of Kamigawa", "Campeões de Kamigawa"));
        currentEditions.add(new Edition("BOK", "Betrayers of Kamigawa", "Traidores de Kamigawa"));
        currentEditions.add(new Edition("SOK", "Saviors of Kamigawa", "Salvadores de Kamigawa"));
        // Ravnica
        currentEditions.add(new Edition("RAV", "Ravnica: City of Guilds", "Ravnica: A Cidade das Guildas"));
        currentEditions.add(new Edition("GPT", "Guildpact", "Pacto das Guildas"));
        currentEditions.add(new Edition("DIS", "Dissension", "Insurreição"));
        // Time Spiral Block
        currentEditions.add(new Edition("TSP", "Time Spiral", "Espiral Temporal"));
        currentEditions.add(new Edition("PLC", "Planar Chaos", "Caos Planar"));
        currentEditions.add(new Edition("FUT", "Future Sight", "Visão do Futuro"));
        // Lorwyn-Shadowmoor Block
        currentEditions.add(new Edition("LRW", "Lorwyn", "Lorwyn"));
        currentEditions.add(new Edition("MOR", "Morningtide", "Alvorecer"));
        currentEditions.add(new Edition("SHM", "Shadowmoor", "Pântano Sombrio"));
        currentEditions.add(new Edition("EVE", "Eventide", "Entardecer"));
        // Shards of Alara
        currentEditions.add(new Edition("ALA", "Shards of Alara", "Fragmentos de Alara"));
        currentEditions.add(new Edition("CON", "Conflux", "Conflux"));
        currentEditions.add(new Edition("ARB", "Alara Reborn", "Alara Reunida"));
        // Zendikar Block
        currentEditions.add(new Edition("ZEN", "Zendikar", "Zendikar"));
        currentEditions.add(new Edition("WWK", "Worldwake", "Despertar do Mundo"));
        currentEditions.add(new Edition("ROE", "Rise of the Eldrazi", "Ascensão dos Eldrazi"));
        // Scars of Mirrodin Block
        currentEditions.add(new Edition("SOM", "Scars of Mirrodin", "Cicatrizes de Mirrodin"));
        currentEditions.add(new Edition("MBS", "Mirrodin Besieged", "Mirrodin Sitiada"));
        currentEditions.add(new Edition("NPH", "New Phyrexia", "Nova Phyrexia"));
        // Innistrad Block
        currentEditions.add(new Edition("ISD", "Innistrad", "Innistrad"));
        currentEditions.add(new Edition("DKA", "Dark Ascension", "Ascensão das Trevas"));
        currentEditions.add(new Edition("AVR", "Avacyn Restored", "Retorno de Avacyn"));
        // Return to Ravnica Block
        currentEditions.add(new Edition("RTR", "Return to Ravnica", "Retorno a Ravnica"));
        currentEditions.add(new Edition("GTC", "Gatecrash", "Portões Violados"));
        currentEditions.add(new Edition("DGM", "Dragon's Maze", "Labirinto do Dragão"));
        // Theros Block
        currentEditions.add(new Edition("THR", "Theros", "Theros"));
        currentEditions.add(new Edition("BNG", "Born of the Gods", "Nascidos dos Deuses"));
        currentEditions.add(new Edition("JOU", "Journey into Nyx", "Viagem para Nyx"));
        // Khans of Tarkir Block
        currentEditions.add(new Edition("KTK", "Khans of Tarkir", "Khans de Tarkir"));
        currentEditions.add(new Edition("FRF", "Fate Reforged", "Destino Reescrito"));
        currentEditions.add(new Edition("DTK", "Dragons of Tarkir", "Dragões de Tarkir"));
        // Magic Origins Block
        currentEditions.add(new Edition("ORI", "Magic Origins", "Magic – Origens"));
        // Battle For Zendikar Block
        currentEditions.add(new Edition("BFZ", "Battle for Zendikar", "Batalha por Zendikar"));
        currentEditions.add(new Edition("OGW", "Oath of the Gatewatch", "Juramento das Sentinelas"));
        // Shadows over Innistrad Block
        currentEditions.add(new Edition("SOI", "Shadows over Innistrad", "Sombras Em Innistrad"));
        currentEditions.add(new Edition("EMN", "Eldritch Moon", "Enfrente as Trevas"));
        // Kaladesh Block
        currentEditions.add(new Edition("KLD", "Kaladesh", "Kaladesh"));
        currentEditions.add(new Edition("AER", "Aether Revolt", "Revolta do Éter"));
        // Amonkhet Block
        currentEditions.add(new Edition("AKH", "Amonkhet", "Amonkhet"));
        currentEditions.add(new Edition("HOU", "Hour of Devastation", "Hora da Devastação"));
        // Ixalan Block
        currentEditions.add(new Edition("XLN", "Ixalan", "Ixalan"));
        currentEditions.add(new Edition("RIX", "Rivals of Ixalan", "RIVAIS DE IXALAN"));
        // Dominaria Block
        currentEditions.add(new Edition("DOM", "Dominaria", "DOMINÁRIA"));
        // Ravnica Block
        currentEditions.add(new Edition("GRN", "Guilds of Ravnica", "Guildas de Ravnica"));
        currentEditions.add(new Edition("RNA", "Ravnica Allegiance", "Lealdade em Ravnica"));
        // War of the Spark
        currentEditions.add(new Edition("WAR", "War of the Spark", "Guerra da Centelha"));
        // Modern Horizons
        currentEditions.add(new Edition("MH1", "Modern Horizons", "Modern Horizons"));

        // From here forward editions will be ordered by release date
        currentEditions.add(new Edition("ELD", "Throne of Eldraine", "Trono de Eldraine"));

        return currentEditions;
    }

    /**
     * Searches a edition by id
     * @param db
     * @param context
     * @param edition_id
     * @return
     */
    public String getEditionById(SQLiteDatabase db, Context context, long edition_id) {
        String editionName = "";
        Cursor cursor      = null;

        try {
            String selectQuery = "SELECT " + KEY_EDITION_PT  + " FROM " + TABLE_EDITIONS
                    + " WHERE " + TABLE_EDITIONS + "." + KEY_ID + " = " + edition_id;

            Log.e(LOG, selectQuery);

            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null){
                cursor.moveToFirst();
            }

            editionName = cursor.getString(cursor.getColumnIndex(KEY_EDITION_PT));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.edition_not_found, Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return editionName;
    }

    /**
     * Returns a list of all editions
     * @param db
     * @return
     */
    public ArrayList<Edition> getAllEditions(SQLiteDatabase db){
        ArrayList<Edition> editions    = new ArrayList<>();
        String             selectQuery = "";
        Cursor             cursor      = null;

        selectQuery = "SELECT * FROM " + TABLE_EDITIONS + " ORDER BY " + KEY_ID;
        cursor      = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                Edition edition = new Edition();
                edition.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                edition.setEdition(cursor.getString(cursor.getColumnIndex(KEY_EDITION)));
                edition.setEdition_pt(cursor.getString(cursor.getColumnIndex(KEY_EDITION_PT)));
                edition.setEdition_short(cursor.getString(cursor.getColumnIndex(KEY_EDITION_SHORT)));

                editions.add(edition);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return editions;
    }
}

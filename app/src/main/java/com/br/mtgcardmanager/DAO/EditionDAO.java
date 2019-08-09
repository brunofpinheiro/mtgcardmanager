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
        currentEditions.add(new Editions("FNM", "Friday Night Magic", "Friday Night Magic"));
        currentEditions.add(new Editions("PTC", "Prerelease Events", "Prerelease Events"));
        currentEditions.add(new Editions("GDC", "Magic Game Day Cards", "Magic Game Day Cards"));
        // Casual Suplements
        currentEditions.add(new Editions("HOP", "Planechase", "Planechase (2009 Edition)"));
        currentEditions.add(new Editions("ARC", "Archenemy", "Archenemy"));
        currentEditions.add(new Editions("PC2", "Planechase (2012 Edition)", "Planechase (2012 Edition)"));
        currentEditions.add(new Editions("CNS", "Conspiracy", "Conspiracy"));
        currentEditions.add(new Editions("CN2", "Conspiracy: Take the Crown", "Conspiracy: Take the Crown"));
        currentEditions.add(new Editions("PCA", "Planechase Anthology", "Planechase Anthology"));
        currentEditions.add(new Editions("ANN", "Archenemy: Nicol Bolas", "Archenemy: Nicol Bolas"));
        // Commander
        currentEditions.add(new Editions("CMD", "Commander", "Commander (2011 Edition)"));
        currentEditions.add(new Editions("CMA", "Commander’s Arsenal", "Commander's Arsenal"));
        currentEditions.add(new Editions("C13", "Commander 2013", "Commander 2013"));
        currentEditions.add(new Editions("C14", "Commander 2014", "Commander 2014"));
        currentEditions.add(new Editions("C15", "Commander 2015", "Commander 2015"));
        currentEditions.add(new Editions("C16", "Commander (2016 Edition)", "Commander (2016 Edition)"));
        currentEditions.add(new Editions("CMA", "Commander Anthology", "Commander Anthology"));
        currentEditions.add(new Editions("C17", "Commander (2017 Edition)", "Commander (2017 Edition)"));
        currentEditions.add(new Editions("CM2", "Commander Anthology Volume II", "Commander Anthology Volume II"));
        currentEditions.add(new Editions("C18", "Commander (2018 Edition)", "Commander (2018 Edition)"));
        // Portal / Starter Sets
        currentEditions.add(new Editions("POR", "Portal", "Portal"));
        currentEditions.add(new Editions("PO2", "Portal Second Age", "Portal Second Age"));
        // Reprint Sets
        currentEditions.add(new Editions("CHR", "Chronicles", "Chronicles"));
        currentEditions.add(new Editions("MMA", "Modern Masters", "Modern Masters"));
        currentEditions.add(new Editions("MM2", "Modern Masters 2015", "Modern Masters 2015 Edition"));
        currentEditions.add(new Editions("EMA", "Eternal Masters", "Eternal Masters"));
        currentEditions.add(new Editions("MM3", "Modern Masters 2017 Edition", "Modern Masters 2017 Edition"));
        currentEditions.add(new Editions("IMA", "Iconic Masters", "Iconic Masters"));
        currentEditions.add(new Editions("M25", "Masters 25", "Masters 25"));
        currentEditions.add(new Editions("UMA", "Ultimate Masters", "Ultimate Masters"));
        // Un-Sets
        currentEditions.add(new Editions("UNG", "Unglued", "Unglued"));
        currentEditions.add(new Editions("UNH", "Unhinged", "Unhinged"));
        currentEditions.add(new Editions("UN3", "Unstable", "Unstable"));
        // Duel Decks
        currentEditions.add(new Editions("EVG", "Duel Decks: Elves vs. Goblins", "Duel Decks: Elves vs. Goblins"));
        currentEditions.add(new Editions("DD2", "Duel Decks: Jace vs. Chandra", "Duel Decks: Jace vs. Chandra"));
        currentEditions.add(new Editions("DDC", "Duel Decks: Divine vs. Demonic", "Duel Decks: Divine vs. Demonic"));
        currentEditions.add(new Editions("DDD", "Duel Decks: Garruk vs. Liliana", "Duel Decks: Garruk vs. Liliana"));
        currentEditions.add(new Editions("DDE", "Duel Decks: Phyrexia vs. The Coalition", "Duel Decks: Phyrexia vs. the Coalition"));
        currentEditions.add(new Editions("DDF", "Duel Decks: Elspeth vs. Tezzeret", "Duel Decks: Elspeth vs. Tezzeret"));
        currentEditions.add(new Editions("DDG", "Duel Decks: Knights vs. Dragons", "Duel Decks: Knights vs. Dragons"));
        currentEditions.add(new Editions("DDH", "Duel Decks: Ajani vs. Nicol Bolas", "Duel Decks: Ajani vs. Nicol Bolas"));
        currentEditions.add(new Editions("DDI", "Duel Decks: Venser vs. Koth", "Duel Decks: Venser vs. Koth"));
        currentEditions.add(new Editions("DDJ", "Duel Decks: Izzet vs. Golgari", "Duel Decks: Izzet vs. Golgari"));
        currentEditions.add(new Editions("DDK", "Duel Decks: Sorin vs. Tibalt", "Duel Decks: Sorin vs. Tibalt"));
        currentEditions.add(new Editions("HVM", "Duel Decks: Heroes vs. Monsters", "Duel Decks: Heroes vs. Monsters"));
        currentEditions.add(new Editions("DDM", "Duel Decks: Jace vs. Vraska", "Duel Decks: Jace vs. Vraska"));
        currentEditions.add(new Editions("DDN", "Duel Decks: Speed vs. Cunning", "Duel Decks: Speed vs. Cunning"));
        currentEditions.add(new Editions("DD3", "Duel Decks: Anthology", "Duel Decks: Anthology"));
        currentEditions.add(new Editions("EVK", "Duel Decks: Elspeth vs. Kiora", "Duel Decks: Elspeth vs. Kiora"));
        currentEditions.add(new Editions("DDP", "Duel Decks: Zendikar vs. Eldrazi", "Duel Decks: Zendikar vs. Eldrazi"));
        currentEditions.add(new Editions("DDQ", "Duel Decks: Blessed vs. Cursed", "Duel Decks: Blessed vs. Cursed"));
        currentEditions.add(new Editions("NVO", "Nissa vs. Ob Nixilis", "Nissa vs. Ob Nixilis"));
        currentEditions.add(new Editions("DDS", "Duel Decks: Mind vs. Might", "Duel Decks: Mind vs. Might"));
        currentEditions.add(new Editions("DDT", "Duel Decks: Merfolk vs. Goblins", "Duel Decks: Merfolk vs. Goblins"));
        currentEditions.add(new Editions("DDU", "Duel Deck: Elves vs. Inventors", "Duel Deck: Elves vs. Inventors"));
        // Older Editions
        currentEditions.add(new Editions("LEA", "Limited Edition Alpha", "Alpha"));
        currentEditions.add(new Editions("LEB", "Limited Edition Beta", "Beta"));
        currentEditions.add(new Editions("ARN", "Arabian Nights", "Arabian Nights"));
        currentEditions.add(new Editions("ATQ", "Antiquities", "Antiquities"));
        currentEditions.add(new Editions("3ED", "Revised Edition", "Revised Edition"));
        currentEditions.add(new Editions("LEG", "Legends", "Legends"));
        currentEditions.add(new Editions("DRK", "The Dark", "The Dark"));
        currentEditions.add(new Editions("FEM", "Fallen Empires", "Fallen Empires"));
        currentEditions.add(new Editions("4ED", "Fourth Edition", "Quarta Edição"));
        // Ice Age Block
        currentEditions.add(new Editions("ICE", "Ice Age", "Era Glacial"));
        currentEditions.add(new Editions("ALL", "Alliances", "Alianças"));
        currentEditions.add(new Editions("CSP", "Coldsnap", "Frente Fria"));
        // Homelands
        currentEditions.add(new Editions("HML", "Homelands", "Terras Natais"));
        // Mirage Block
        currentEditions.add(new Editions("MIR", "Mirage", "Miragem"));
        currentEditions.add(new Editions("VIS", "Visions", "Visões"));
        currentEditions.add(new Editions("WTH", "Weatherlight", "Alísios"));
        // Core Set
        currentEditions.add(new Editions("5ED", "Fifth Edition", "Quinta Edição"));
        currentEditions.add(new Editions("6ED", "Classic Sixth Edition", "Sexta Edição"));
        currentEditions.add(new Editions("7ED", "Seventh Edition", "Coleção Básica Sétima Edição"));
        currentEditions.add(new Editions("8ED", "Eighth Edition", "Oitava Edição"));
        currentEditions.add(new Editions("9ED", "Ninth Edition", "Nona Edição"));
        currentEditions.add(new Editions("10E", "Tenth Edition", "Coleção Básica Décima Edição"));
        currentEditions.add(new Editions("M10", "Magic 2010", "Coleção Básica 2010"));
        currentEditions.add(new Editions("M11", "Magic 2011", "Coleção Básica 2011"));
        currentEditions.add(new Editions("M12", "Magic 2012", "Coleção Básica 2012"));
        currentEditions.add(new Editions("M13", "Magic 2013", "Coleção Básica 2013"));
        currentEditions.add(new Editions("M14", "Magic 2014", "Coleção Básica de Magic 2014"));
        currentEditions.add(new Editions("M15", "Magic 2015", "Coleção Básica de Magic 2015"));
        currentEditions.add(new Editions("M19", "Core Set 2019", "COLEÇÃO BÁSICA 2019"));
        currentEditions.add(new Editions("M20", "Core Set 2020", "Coleção Básica 2020"));
        // Tempest Block
        currentEditions.add(new Editions("TMP", "Tempest", "Tempestade"));
        currentEditions.add(new Editions("STH", "Stronghold", "Fortaleza"));
        currentEditions.add(new Editions("EXO", "Exodus", "Êxodo"));
        // Urza Block
        currentEditions.add(new Editions("USG", "Urza’s Saga", "A Saga de Urza"));
        currentEditions.add(new Editions("ULG", "Urza’s Legacy", "O Legado de Urza"));
        currentEditions.add(new Editions("UDS", "Urza's Destiny", "O Destino de Urza"));
        // Masques Block
        currentEditions.add(new Editions("MMQ", "Mercadian Masques", "Máscaras de Mercádia"));
        currentEditions.add(new Editions("NEM", "Nemesis", "Nêmesis"));
        currentEditions.add(new Editions("PCY", "Prophecy", "Profecia"));
        // Invasion Block
        currentEditions.add(new Editions("INV", "Invasion", "Invasão"));
        currentEditions.add(new Editions("PLS", "Planeshift", "Conjunção"));
        currentEditions.add(new Editions("APC", "Apocalypse", "Apocalipse"));
        // Odyssey Block
        currentEditions.add(new Editions("ODY", "Odyssey", "Odisseia"));
        currentEditions.add(new Editions("TOR", "Torment", "Tormento"));
        currentEditions.add(new Editions("JUD", "Judgment", "Julgamento"));
        // Onslaught Block
        currentEditions.add(new Editions("ONS", "Onslaught", "Investida"));
        currentEditions.add(new Editions("LGN", "Legions", "Legiões"));
        currentEditions.add(new Editions("SCG", "Scourge", "Flagelo"));
        // Mirrodin Block
        currentEditions.add(new Editions("MRD", "Mirrodin", "Mirrodin"));
        currentEditions.add(new Editions("DST", "Darksteel", "Darksteel"));
        currentEditions.add(new Editions("5DN", "Fifth Dawn", "Aquinta Aurora"));
        // Kamigawa Block
        currentEditions.add(new Editions("CHK", "Champions of Kamigawa", "Campeões de Kamigawa"));
        currentEditions.add(new Editions("BOK", "Betrayers of Kamigawa", "Traidores de Kamigawa"));
        currentEditions.add(new Editions("SOK", "Saviors of Kamigawa", "Salvadores de Kamigawa"));
        // Ravnica
        currentEditions.add(new Editions("RAV", "Ravnica: City of Guilds", "Ravnica: A Cidade das Guildas"));
        currentEditions.add(new Editions("GPT", "Guildpact", "Pacto das Guildas"));
        currentEditions.add(new Editions("DIS", "Dissension", "Insurreição"));
        // Time Spiral Block
        currentEditions.add(new Editions("TSP", "Time Spiral", "Espiral Temporal"));
        currentEditions.add(new Editions("PLC", "Planar Chaos", "Caos Planar"));
        currentEditions.add(new Editions("FUT", "Future Sight", "Visão do Futuro"));
        // Lorwyn-Shadowmoor Block
        currentEditions.add(new Editions("LRW", "Lorwyn", "Lorwyn"));
        currentEditions.add(new Editions("MOR", "Morningtide", "Alvorecer"));
        currentEditions.add(new Editions("SHM", "Shadowmoor", "Pântano Sombrio"));
        currentEditions.add(new Editions("EVE", "Eventide", "Entardecer"));
        // Shards of Alara
        currentEditions.add(new Editions("ALA", "Shards of Alara", "Fragmentos de Alara"));
        currentEditions.add(new Editions("CON", "Conflux", "Conflux"));
        currentEditions.add(new Editions("ARB", "Alara Reborn", "Alara Reunida"));
        // Zendikar Block
        currentEditions.add(new Editions("ZEN", "Zendikar", "Zendikar"));
        currentEditions.add(new Editions("WWK", "Worldwake", "Despertar do Mundo"));
        currentEditions.add(new Editions("ROE", "Rise of the Eldrazi", "Ascensão dos Eldrazi"));
        // Scars of Mirrodin Block
        currentEditions.add(new Editions("SOM", "Scars of Mirrodin", "Cicatrizes de Mirrodin"));
        currentEditions.add(new Editions("MBS", "Mirrodin Besieged", "Mirrodin Sitiada"));
        currentEditions.add(new Editions("NPH", "New Phyrexia", "Nova Phyrexia"));
        // Innistrad Block
        currentEditions.add(new Editions("ISD", "Innistrad", "Innistrad"));
        currentEditions.add(new Editions("DKA", "Dark Ascension", "Ascensão das Trevas"));
        currentEditions.add(new Editions("AVR", "Avacyn Restored", "Retorno de Avacyn"));
        // Return to Ravnica Block
        currentEditions.add(new Editions("RTR", "Return to Ravnica", "Retorno a Ravnica"));
        currentEditions.add(new Editions("GTC", "Gatecrash", "Portões Violados"));
        currentEditions.add(new Editions("DGM", "Dragon's Maze", "Labirinto do Dragão"));
        // Theros Block
        currentEditions.add(new Editions("THR", "Theros", "Theros"));
        currentEditions.add(new Editions("BNG", "Born of the Gods", "Nascidos dos Deuses"));
        currentEditions.add(new Editions("JOU", "Journey into Nyx", "Viagem para Nyx"));
        // Khans of Tarkir Block
        currentEditions.add(new Editions("KTK", "Khans of Tarkir", "Khans de Tarkir"));
        currentEditions.add(new Editions("FRF", "Fate Reforged", "Destino Reescrito"));
        currentEditions.add(new Editions("DTK", "Dragons of Tarkir", "Dragões de Tarkir"));
        // Magic Origins Block
        currentEditions.add(new Editions("ORI", "Magic Origins", "Magic – Origens"));
        // Battle For Zendikar Block
        currentEditions.add(new Editions("BFZ", "Battle for Zendikar", "Batalha por Zendikar"));
        currentEditions.add(new Editions("OGW", "Oath of the Gatewatch", "Juramento das Sentinelas"));
        // Shadows over Innistrad Block
        currentEditions.add(new Editions("SOI", "Shadows over Innistrad", "Sombras Em Innistrad"));
        currentEditions.add(new Editions("EMN", "Eldritch Moon", "Enfrente as Trevas"));
        // Kaladesh Block
        currentEditions.add(new Editions("KLD", "Kaladesh", "Kaladesh"));
        currentEditions.add(new Editions("AER", "Aether Revolt", "Revolta do Éter"));
        // Amonkhet Block
        currentEditions.add(new Editions("AKH", "Amonkhet", "Amonkhet"));
        currentEditions.add(new Editions("HOU", "Hour of Devastation", "Hora da Devastação"));
        // Ixalan Block
        currentEditions.add(new Editions("XLN", "Ixalan", "Ixalan"));
        currentEditions.add(new Editions("RIX", "Rivals of Ixalan", "RIVAIS DE IXALAN"));
        // Dominaria Block
        currentEditions.add(new Editions("DOM", "Dominaria", "DOMINÁRIA"));
        // Ravnica Block
        currentEditions.add(new Editions("GRN", "Guilds of Ravnica", "Guildas de Ravnica"));
        currentEditions.add(new Editions("RNA", "Ravnica Allegiance", "Lealdade em Ravnica"));
        // War of the Spark
        currentEditions.add(new Editions("WAR", "War of the Spark", "Guerra da Centelha"));
        // Modern Horizons
        currentEditions.add(new Editions("MH1", "Modern Horizons", "Modern Horizons"));

        // From here forward editions will be ordered by release date

        return currentEditions;
    }

    public String getEditionById(SQLiteDatabase db, Context context, long edition_id) {
        String edition_name = "";
        Cursor cursor       = null;

        try {
            String selectQuery = "SELECT " + KEY_EDITION_PT  + " FROM " + TABLE_EDITIONS
                    + " WHERE " + TABLE_EDITIONS + "." + KEY_ID + " = " + edition_id;

            Log.e(LOG, selectQuery);

            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null){
                cursor.moveToFirst();
            }

            edition_name = cursor.getString(cursor.getColumnIndex(KEY_EDITION_PT));
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

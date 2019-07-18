package com.br.mtgcardmanager.Helper;

import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;

import com.br.mtgcardmanager.R;

/**
 * Created by Bruno on 20/08/2016.
 */
public class UtilsHelper {
    String padronized_name;
    String padronized_edition;

    public String padronizeForSQL(String card_name){
        padronized_name = card_name;

        if (card_name.contains("'")){
            padronized_name = card_name.replace("'", "''");
        }

        return padronized_name;
    }

    public String padronizeEdition(String edition){
        padronized_edition = edition;

        if (edition.contains("/")) {
            int name_separator  = edition.lastIndexOf("/");
            padronized_edition = edition.substring(name_separator +1, edition.length()).trim();
        }

        return padronized_edition;
    }

    public String padronizeCardName(String card_name) {
        padronized_name = card_name;

        if (card_name.contains("(")) {
            int first_separator = card_name.lastIndexOf("(") ;
            int second_separator = card_name.lastIndexOf(")") ;
            padronized_name   = card_name.substring(first_separator + 1, second_separator).trim();
        }

        return padronized_name;
    }
}

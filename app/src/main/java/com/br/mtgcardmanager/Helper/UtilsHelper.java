package com.br.mtgcardmanager.Helper;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UtilsHelper {

    public static String padronizeForSQL(String card_name){
        String padronized_name;

        padronized_name = card_name;

        if (card_name.contains("'")){
            padronized_name = card_name.replace("'", "''");
        }

        return padronized_name;
    }

    public static String padronizeEdition(String edition){
        String padronized_edition;

        padronized_edition = edition;

        if (edition.contains("/")) {
            int name_separator = edition.lastIndexOf("/");
            padronized_edition = edition.substring(name_separator +1, edition.length()).trim();
        }

        return padronized_edition;
    }

    public static String padronizeCardName(String card_name) {
        String padronized_name = card_name;

        if (card_name.contains("(")) {
            int first_separator  = card_name.lastIndexOf("(") ;
            int second_separator = card_name.lastIndexOf(")") ;
            padronized_name      = card_name.substring(first_separator + 1, second_separator).trim();
        }

        return padronized_name;
    }

    public static void closeKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

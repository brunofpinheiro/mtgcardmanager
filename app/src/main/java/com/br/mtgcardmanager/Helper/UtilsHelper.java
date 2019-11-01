package com.br.mtgcardmanager.Helper;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UtilsHelper {

    /**
     * Replaces single quotes with double single quotes
     * @param cardName
     * @return
     */
    public static String padronizeForSQL(String cardName){
        String padronizedName;

        padronizedName = cardName;

        if (cardName.contains("'")){
            padronizedName = cardName.replace("'", "''");
        }

        return padronizedName;
    }

    /**
     * If the edition's name contains a slash, returns only what comes after the slash
     * @param edition
     * @return
     */
    public static String padronizeEdition(String edition){
        String padronizedEdition;

        padronizedEdition = edition;

        if (edition.contains("/")) {
            int nameSeparator = edition.lastIndexOf("/");
            padronizedEdition = edition.substring(nameSeparator +1, edition.length()).trim();
        }

        return padronizedEdition;
    }

    /**
     * Removes any parenthesis found on the cards name
     * @param cardName
     * @return
     */
    public static String padronizeCardName(String cardName) {
        String padronizedName = cardName;

        if (cardName.contains("(")) {
            int firstSeparator  = cardName.lastIndexOf("(") ;
            int secondSeparator = cardName.lastIndexOf(")") ;
            padronizedName      = cardName.substring(firstSeparator + 1, secondSeparator).trim();
        }

        return padronizedName;
    }

    /**
     * Closes the virtual keyboard
     * @param context
     * @param view
     */
    public static void closeKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

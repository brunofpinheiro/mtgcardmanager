package com.br.mtgcardmanager.Helper;


public class UtilsHelper {
    private String padronized_name;
    private String padronized_edition;

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
            int name_separator = edition.lastIndexOf("/");
            padronized_edition = edition.substring(name_separator +1, edition.length()).trim();
        }

        return padronized_edition;
    }

    public String padronizeCardName(String card_name) {
        padronized_name = card_name;

        if (card_name.contains("(")) {
            int first_separator  = card_name.lastIndexOf("(") ;
            int second_separator = card_name.lastIndexOf(")") ;
            padronized_name      = card_name.substring(first_separator + 1, second_separator).trim();
        }

        return padronized_name;
    }
}

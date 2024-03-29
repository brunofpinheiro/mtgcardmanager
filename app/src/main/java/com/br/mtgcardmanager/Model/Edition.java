package com.br.mtgcardmanager.Model;


public class Edition {
    int    id;
    String edition_short;
    String edition;
    String edition_pt;

    // constructors
    public Edition(){

    }

    public Edition(String edition_short, String edition, String edition_pt){
        this.edition_short = edition_short;
        this.edition       = edition;
        this.edition_pt    = edition_pt;
    }

    public Edition(int id, String edition_short, String edition, String edition_pt){
        this.id         = id;
        this.edition_short = edition_short;
        this.edition       = edition;
        this.edition_pt    = edition_pt;
    }


    /**
     * GETTERS AND SETTERS
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEdition_short() {
        return edition_short;
    }

    public void setEdition_short(String edition_short) {
        this.edition_short = edition_short;
    }

    public String getEdition() {return edition;}

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getEdition_pt() {return edition_pt;}

    public void setEdition_pt(String edition_pt) {
        this.edition_pt = edition_pt;
    }
}
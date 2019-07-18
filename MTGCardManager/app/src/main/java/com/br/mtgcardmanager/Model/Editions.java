package com.br.mtgcardmanager.Model;

/**
 * Created by Bruno on 21/07/2016.
 */
public class Editions {
    int    id;
    String edition_short;
    String edition;

    // constructors
    public Editions(){

    }

    public Editions(String edition_short, String edition){
        this.edition_short = edition_short;
        this.edition       = edition;
    }

    public Editions(int id, String edition_short, String edition){
        this.id         = id;
        this.edition_short = edition_short;
        this.edition       = edition;
    }

    // getters and setters

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

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }
}
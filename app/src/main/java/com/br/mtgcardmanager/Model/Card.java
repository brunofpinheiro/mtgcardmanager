package com.br.mtgcardmanager.Model;


public class Card {
    private int    id;
    private String name_en;
    private String name_pt;
    private int    id_edition;
    private int    quantity;
    private String foil;

    // constructors
    public Card() {

    }

    public Card(String name_en, String name_pt, int id_edition, int quantity, String foil) {
        this.name_en    = name_en;
        this.name_pt    = name_pt;
        this.id_edition = id_edition;
        this.quantity   = quantity;
        this.foil       = foil;
    }

    public Card(int id, String name_en, String name_pt, int id_edition, int quantity, String foil) {
        this.id         = id;
        this.name_en    = name_en;
        this.name_pt    = name_pt;
        this.id_edition = id_edition;
        this.quantity   = quantity;
        this.foil       = foil;
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

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_pt() {
        return name_pt;
    }

    public void setName_pt(String name_pt) {
        this.name_pt = name_pt;
    }

    public int getId_edition() {
        return id_edition;
    }

    public void setId_edition(int id_edition) {
        this.id_edition = id_edition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getFoil() {
        return foil;
    }

    public void setFoil(String foil) {
        this.foil = foil;
    }
}
package com.br.mtgcardmanager.Model;

import com.google.gson.annotations.SerializedName;

public class APICard {
    @SerializedName("name")
    private String name;

    @SerializedName("names")
    private String[] names;

    @SerializedName("manaCost")
    private String manaCost;

    @SerializedName("cmc")
    private int cmc;

    @SerializedName("colors")
    private String[] colors;

    @SerializedName("colorIdentity")
    private String[] colorIdentity;

    @SerializedName("type")
    private String type;

    @SerializedName("supertypes")
    private String[] supertypes;

    @SerializedName("types")
    private String[] types;

    @SerializedName("subtypes")
    private String[] subtypes;

    @SerializedName("rarity")
    private String rarity;

    @SerializedName("set")
    private String set;

    @SerializedName("text")
    private String text;

    @SerializedName("artist")
    private String artist;

    @SerializedName("number")
    private String number;

    @SerializedName("power")
    private String power;

    @SerializedName("toughness")
    private String toughness;

    @SerializedName("layout")
    private String layout;

    @SerializedName("multiverseid")
    private int multiverseid;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("rulings")
    private Rullings[] rulings;

    @SerializedName("foreignNames")
    private ForeignName[] foreignNames;

    @SerializedName("printings")
    private String[] printings;

    @SerializedName("originalText")
    private String originalText;

    @SerializedName("originalType")
    private String originalType;

    @SerializedName("id")
    private String id;




    /**
     * GETTERS AND SETTERS
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public int getCmc() {
        return cmc;
    }

    public void setCmc(int cmc) {
        this.cmc = cmc;
    }

    public String[] getColors() {
        return colors;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
    }

    public String[] getColorIdentity() {
        return colorIdentity;
    }

    public void setColorIdentity(String[] colorIdentity) {
        this.colorIdentity = colorIdentity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getSupertypes() {
        return supertypes;
    }

    public void setSupertypes(String[] supertypes) {
        this.supertypes = supertypes;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String[] getSubtypes() {
        return subtypes;
    }

    public void setSubtypes(String[] subtypes) {
        this.subtypes = subtypes;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getMultiverseid() {
        return multiverseid;
    }

    public void setMultiverseid(int multiverseid) {
        this.multiverseid = multiverseid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Rullings[] getRulings() {
        return rulings;
    }

    public void setRulings(Rullings[] rulings) {
        this.rulings = rulings;
    }

    public ForeignName[] getForeignNames() {
        return foreignNames;
    }

    public void setForeignNames(ForeignName[] foreignNames) {
        this.foreignNames = foreignNames;
    }

    public String[] getPrintings() {
        return printings;
    }

    public void setPrintings(String[] printings) {
        this.printings = printings;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getOriginalType() {
        return originalType;
    }

    public void setOriginalType(String originalType) {
        this.originalType = originalType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

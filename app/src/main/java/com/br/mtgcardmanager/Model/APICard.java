package com.br.mtgcardmanager.Model;

import com.google.gson.annotations.SerializedName;

public class APICard {
    @SerializedName("cards")
    private Card[] cards;

    public Card[] getCards() {
        return cards;
    }

    public void setCards(Card[] cards) {
        this.cards = cards;
    }
}

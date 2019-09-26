package com.br.mtgcardmanager.Model;

import com.google.gson.annotations.SerializedName;

public class APICards {
    @SerializedName("cards")
    private APICard[] cards;

    public APICard[] getCards() {
        return cards;
    }

    public void setCards(APICard[] cards) {
        this.cards = cards;
    }
}

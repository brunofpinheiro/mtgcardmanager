package com.br.mtgcardmanager.Helper;

import android.support.test.rule.ActivityTestRule;

import com.br.mtgcardmanager.Model.Card;
import com.br.mtgcardmanager.Model.Edition;
import com.br.mtgcardmanager.View.MainActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseHelperTest {
    @ClassRule
    public static ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private static MainActivity   mainActivity;
    private static DatabaseHelper dbHelper;

    public DatabaseHelperTest() {}

    @BeforeClass
    public static void setUp() {
        Card card;

        if (mainActivity == null)
            mainActivity = mainActivityTestRule.getActivity();

        if (dbHelper == null)
            dbHelper = DatabaseHelper.getInstance(mainActivity);

        card = new Card();
        card.setFoil("S");
        card.setId_edition(1);
        card.setName_en("deleteHaveCard");
        card.setName_pt("deleteHaveCard");
        card.setQuantity(4);
        dbHelper.insertHaveCard(card);

        card = new Card();
        card.setFoil("S");
        card.setId_edition(1);
        card.setName_en("checkIfHaveCardExists");
        card.setName_pt("checkIfHaveCardExists");
        card.setQuantity(4);
        dbHelper.insertHaveCard(card);

        card = new Card();
        card.setFoil("S");
        card.setId_edition(1);
        card.setName_en("updateCardQuantity");
        card.setName_pt("updateCardQuantity");
        card.setQuantity(4);
        dbHelper.insertHaveCard(card);

        card = new Card();
        card.setFoil("S");
        card.setId_edition(1);
        card.setName_en("deleteWantCard");
        card.setName_pt("deleteWantCard");
        card.setQuantity(4);
        dbHelper.insertHaveCard(card);

        card = new Card();
        card.setFoil("S");
        card.setId_edition(1);
        card.setName_en("checkIfWantCardExists");
        card.setName_pt("checkIfWantCardExists");
        card.setQuantity(4);
        dbHelper.insertHaveCard(card);

        System.out.println("@BeforeClass setUp");
    }

    @AfterClass
    public static void tearDown() {
        dbHelper.deleteAllHave();
        dbHelper.deleteAllWant();

        if (dbHelper != null)
            dbHelper = null;

        System.out.println("@AfterClass tearDown");
    }

    @Test
    public void insertHaveCard() {
        Card card;
        Long cardId;

        card = new Card();
        card.setFoil("S");
        card.setId_edition(1);
        card.setName_en("insertHaveCard");
        card.setName_pt("insertHaveCard");
        card.setQuantity(4);

        cardId = dbHelper.insertHaveCard(card);
        assertThat(cardId).isNotNull();
    }

    @Test
    public void getAllHaveCards() {
        int quantity;

        quantity = dbHelper.getAllHaveCards().size();

        assertThat(quantity).isNotNull();
    }

    @Test
    public void deleteHaveCard() {
        int rowsAffected;

        rowsAffected = dbHelper.deleteHaveCard(1);

        assertThat(rowsAffected).isNotNull();
    }

    @Test
    public void checkIfHaveCardExists() {
        Card checkedCard;

        checkedCard = dbHelper.checkIfHaveCardExists("checkIfHaveCardExists", 1, "S");

        assertThat(checkedCard).isNotNull();
    }

    @Test
    public void insertWantCard() {
        Card card;
        Long cardId;

        card = new Card();
        card.setFoil("S");
        card.setId_edition(1);
        card.setName_en("insertWantCard");
        card.setName_pt("insertWantCard");
        card.setQuantity(4);

        cardId = dbHelper.insertHaveCard(card);
        assertThat(cardId).isNotNull();
    }

    @Test
    public void getAllWantCards() {
        int quantity;

        quantity = dbHelper.getAllWantCards().size();

        assertThat(quantity).isNotNull();
    }

    @Test
    public void deleteWantCard() {
        int rowsAffected;

        rowsAffected = dbHelper.deleteWantCard(1);

        assertThat(rowsAffected).isNotNull();
    }

    @Test
    public void checkIfWantCardExists() {
        Card checkedCard;

        checkedCard = dbHelper.checkIfWantCardExists("checkIfWantCardExists", 1, "S");

        assertThat(checkedCard).isNotNull();
    }

    @Test
    public void updateCardQuantity() {
        Long cardId;

        cardId = dbHelper.updateCardQuantity("have", 1, 1);

        assertThat(cardId).isNotNull();
    }

    @Test
    public void getAllEditions() {
        ArrayList<Edition> editions;

        editions = dbHelper.getAllEditions();

        assertThat(editions).isNotNull();
    }

    @Test
    public void getEditionsQty() {
        Long editionsQty;

        editionsQty = dbHelper.getEditionsQty();

        assertThat(editionsQty).isGreaterThan(0);
    }

    @Test
    public void getSingleEdition() {
        Edition edition;

        edition = dbHelper.getSingleEdition(mainActivity, "Friday Night Magic");

        assertThat(edition.getEdition()).isEqualToIgnoringCase("Friday Night Magic");
    }

    @Test
    public void getEditionById() {
        String editionName;

        editionName = dbHelper.getEditionById(mainActivity, 1);

        assertThat(editionName).isEqualToIgnoringCase("Friday Night Magic");
    }

    @Test
    public void populateEditionsList() {
        ArrayList<Edition> editions;

        editions = dbHelper.populateEditionsList();

        assertThat(editions).isNotNull();
    }
}
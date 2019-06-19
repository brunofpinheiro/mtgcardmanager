package com.br.mtgcardmanager;

import com.br.mtgcardmanager.Helper.UtilsHelper;

import org.apache.http.params.CoreConnectionPNames;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UtilsHelperUnitTest {
    UtilsHelper utils;


    @Test
    public void padronizeForSQL() throws Exception {
        utils = new UtilsHelper();

        String base = "Liliana's Specter";
        String expected = "Liliana''s Specter";

        String returned = utils.padronizeForSQL(base);
        assertEquals(expected, returned);
    }

    @Test
    public void padronizeEdition() throws Exception {
        utils = new UtilsHelper();

        String base = "Sombras Sobre Innistrad / Shadows over Innistrad";
        String expected = "Shadows over Innistrad";

        String returned = utils.padronizeEdition(base);
        assertEquals(expected, returned);
    }

    @Test
    public void padronizeCardName() throws Exception {
        utils = new UtilsHelper();

        String base = "Chittering Rats (Chittering Rats)";
        String expected = "Chittering Rats";

        String returned = utils.padronizeCardName(base);
        assertEquals(expected, returned);
    }
}
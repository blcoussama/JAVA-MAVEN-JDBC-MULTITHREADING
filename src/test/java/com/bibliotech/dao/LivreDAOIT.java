package com.bibliotech.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.support.MySqlIntegrationTest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LivreDAOIT extends MySqlIntegrationTest {

    private final AuteurDAO auteurDAO = new AuteurDAO(CONNEXIONS);
    private final LivreDAO livreDAO = new LivreDAO(CONNEXIONS);
    private Auteur auteur;

    @BeforeEach
    void creerAuteur() throws Exception {
        auteur = new Auteur("Martin", "Claire");
        auteurDAO.ajouterAuteur(auteur);
    }

    @Test
    void ajouteEtRelitAvecLAuteurParJointure() throws Exception {
        livreDAO.ajouterLivre(new Livre("Le Guide du Java", auteur, Categorie.INFORMATIQUE));
        livreDAO.ajouterLivre(new Livre("Une histoire du code", auteur, Categorie.HISTOIRE));

        List<Livre> livres = livreDAO.listerTousLesLivres();

        assertEquals(2, livres.size());
        assertEquals("Le Guide du Java", livres.get(0).getTitre());
        assertEquals(Categorie.HISTOIRE, livres.get(1).getCategorie());
        assertEquals("Claire Martin", livres.get(1).getAuteur().toString());
        assertEquals(2, livreDAO.compterLivres());
    }

    @Test
    void laContrainteUniqueRefuseUnTitreEnDouble() throws Exception {
        livreDAO.ajouterLivre(new Livre("Le Guide du Java", auteur, Categorie.INFORMATIQUE));

        SQLIntegrityConstraintViolationException e = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> livreDAO.ajouterLivre(new Livre("le guide du java", auteur, Categorie.ROMAN)));
        assertEquals(1062, e.getErrorCode(), "doublon détecté sans tenir compte de la casse");
        assertEquals(1, livreDAO.compterLivres());
    }

    @Test
    void laCleEtrangereRefuseUnAuteurInexistant() {
        SQLIntegrityConstraintViolationException e = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> livreDAO.ajouterLivre(new Livre("Orphelin", new Auteur(9999, "X", "Y"), Categorie.ROMAN)));
        assertEquals(1452, e.getErrorCode());
    }

    @Test
    void existeTitreSuitLaCollationDeLaColonne() throws Exception {
        livreDAO.ajouterLivre(new Livre("Les grandes bibliothèques", auteur, Categorie.HISTOIRE));

        assertTrue(livreDAO.existeTitre("Les grandes bibliothèques"));
        assertTrue(livreDAO.existeTitre("LES GRANDES BIBLIOTHEQUES"));
        assertFalse(livreDAO.existeTitre("Absent"));
    }
}

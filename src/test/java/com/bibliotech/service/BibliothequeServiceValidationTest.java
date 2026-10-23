package com.bibliotech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.support.DaoDeTest;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Les règles métier sont vérifiées avant tout accès à la base (la doublure échoue si elle est appelée). */
class BibliothequeServiceValidationTest {

    private final BibliothequeService service = new BibliothequeService(
            DaoDeTest.livreDAO(List::of), DaoDeTest.auteurDAOInutilise(), 2, Duration.ZERO, message -> { });

    private final Auteur auteurEnregistre = new Auteur(1, "Martin", "Claire");

    @Test
    void livreSansTitreRefuse() {
        BibliothequeException e = assertThrows(BibliothequeException.class,
                () -> service.sauvegarderLivre(new Livre("  ", auteurEnregistre, Categorie.ROMAN)));
        assertTrue(e.getMessage().contains("titre"));
    }

    @Test
    void livreSansAuteurEnregistreRefuse() {
        assertThrows(BibliothequeException.class,
                () -> service.sauvegarderLivre(new Livre("Sans auteur", null, Categorie.ROMAN)));
        assertThrows(BibliothequeException.class,
                () -> service.sauvegarderLivre(new Livre("Auteur non enregistré", new Auteur("Nom", "Prénom"),
                        Categorie.ROMAN)));
        assertThrows(BibliothequeException.class,
                () -> service.sauvegarderLivreSiAbsent(new Livre("Sans auteur", null, Categorie.ROMAN)));
    }

    @Test
    void livreSansCategorieRefuse() {
        assertThrows(BibliothequeException.class,
                () -> service.sauvegarderLivre(new Livre("Sans catégorie", auteurEnregistre, null)));
    }

    @Test
    void auteurSansNomRefuse() {
        BibliothequeException e = assertThrows(BibliothequeException.class,
                () -> service.sauvegarderAuteur(new Auteur("", "Jean")));
        assertTrue(e.getMessage().contains("nom"));
        assertThrows(BibliothequeException.class, () -> service.sauvegarderAuteur(null));
    }

    @Test
    void parametresDuServiceVerifies() {
        assertThrows(IllegalArgumentException.class, () -> new BibliothequeService(
                DaoDeTest.livreDAO(List::of), DaoDeTest.auteurDAOInutilise(), 0, Duration.ZERO, m -> { }));
        assertThrows(IllegalArgumentException.class, () -> new BibliothequeService(
                DaoDeTest.livreDAO(List::of), DaoDeTest.auteurDAOInutilise(), 1, Duration.ofSeconds(-1), m -> { }));
        assertEquals(2, service.getConsultationsSimultanees());
    }
}

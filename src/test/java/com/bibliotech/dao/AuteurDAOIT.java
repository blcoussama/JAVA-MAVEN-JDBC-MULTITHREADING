package com.bibliotech.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bibliotech.model.Auteur;
import com.bibliotech.support.MySqlIntegrationTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuteurDAOIT extends MySqlIntegrationTest {

    private final AuteurDAO dao = new AuteurDAO(CONNEXIONS);

    @Test
    void ajouteEtRelitDesAuteurs() throws Exception {
        Auteur claire = new Auteur("Martin", "Claire");
        Auteur paul = new Auteur("Durand", "Paul");

        int idClaire = dao.ajouterAuteur(claire);
        int idPaul = dao.ajouterAuteur(paul);

        assertEquals(idClaire, claire.getId());
        assertTrue(idPaul > idClaire);
        List<Auteur> auteurs = dao.listerTousLesAuteurs();
        assertEquals(List.of("Claire Martin", "Paul Durand"), auteurs.stream().map(Auteur::toString).toList());
    }

    @Test
    void rechercheParId() throws Exception {
        int id = dao.ajouterAuteur(new Auteur("Lefèvre", "Inès"));

        Optional<Auteur> trouve = dao.trouverAuteurParId(id);
        assertTrue(trouve.isPresent());
        assertEquals("Lefèvre", trouve.get().getNom(), "UTF-8 conservé");
        assertTrue(dao.trouverAuteurParId(9999).isEmpty());
    }

    @Test
    void lesValeursSontParametreesEtNonInterpretees() throws Exception {
        String nomPiege = "Robert'); DROP TABLE auteur; --";
        int id = dao.ajouterAuteur(new Auteur(nomPiege, null));

        assertEquals(nomPiege, dao.trouverAuteurParId(id).orElseThrow().getNom());
        assertEquals(1, dao.listerTousLesAuteurs().size());
    }
}

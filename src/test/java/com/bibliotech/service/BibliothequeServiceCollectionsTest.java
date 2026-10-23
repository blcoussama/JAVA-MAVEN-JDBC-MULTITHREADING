package com.bibliotech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.support.DaoDeTest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Traitements en mémoire (Collections, Streams, lambdas) : aucune base de données. */
class BibliothequeServiceCollectionsTest {

    private final Auteur auteur = new Auteur(1, "Martin", "Claire");
    private BibliothequeService service;
    private List<Livre> livres;

    @BeforeEach
    void preparer() {
        service = new BibliothequeService(DaoDeTest.livreDAO(List::of), DaoDeTest.auteurDAOInutilise(),
                2, Duration.ZERO, message -> { });
        livres = List.of(
                new Livre(1, "Le Guide du Java", auteur, Categorie.INFORMATIQUE),
                new Livre(2, "une histoire du code", auteur, Categorie.HISTOIRE),
                new Livre(3, "Algorithmes", auteur, Categorie.INFORMATIQUE),
                new Livre(4, "Les grandes bibliothèques", auteur, Categorie.HISTOIRE),
                new Livre(5, "Le Guide du Java", auteur, Categorie.ROMAN)); // titre en double
    }

    @Test
    void filtrageBoucleEtStreamDonnentLeMemeResultat() {
        List<Livre> boucle = service.filtrerParCategorie(livres, Categorie.INFORMATIQUE);
        List<Livre> stream = service.filtrerParCategorieStream(livres, Categorie.INFORMATIQUE);

        assertEquals(List.of(1, 3), boucle.stream().map(Livre::getId).toList());
        assertEquals(boucle, stream);
    }

    @Test
    void lesCategoriesSontSansDoublon() {
        EnumSet<Categorie> attendu = EnumSet.of(Categorie.INFORMATIQUE, Categorie.HISTOIRE, Categorie.ROMAN);

        assertEquals(attendu, service.obtenirCategoriesUniques(livres));
        assertEquals(attendu, service.obtenirCategoriesUniquesStream(livres));
    }

    @Test
    void lIndexationParTitreToléreLesDoublonsEtGardeLePremier() {
        // Sans fonction de fusion, Collectors.toMap lèverait IllegalStateException (clé en double).
        Map<String, Livre> stream = service.indexerLivresParTitreStream(livres);
        Map<String, Livre> boucle = service.indexerLivresParTitre(livres);

        assertEquals(4, stream.size());
        assertSame(livres.get(0), stream.get("Le Guide du Java"));
        assertEquals(stream, boucle);
    }

    @Test
    void lesTrisParTitreSontInsensiblesALaCasseEtCoherents() {
        List<Livre> enPlace = new ArrayList<>(livres);
        service.trierLivresParTitre(enPlace);
        List<Livre> stream = service.trierLivresParTitreStream(livres);

        assertEquals(List.of("Algorithmes", "Le Guide du Java", "Le Guide du Java",
                "Les grandes bibliothèques", "une histoire du code"), stream.stream().map(Livre::getTitre).toList());
        assertEquals(stream.stream().map(Livre::getTitre).toList(), enPlace.stream().map(Livre::getTitre).toList());
        // La version Stream ne modifie pas la liste source.
        assertEquals(1, livres.get(0).getId());
    }

    @Test
    void lesTrisParIdSontDecroissants() {
        List<Livre> enPlace = new ArrayList<>(livres);
        service.trierLivresParId(enPlace);

        assertEquals(List.of(5, 4, 3, 2, 1), enPlace.stream().map(Livre::getId).toList());
        assertEquals(List.of(5, 4, 3, 2, 1),
                service.trierLivresParNumeroIDStream(livres).stream().map(Livre::getId).toList());
    }

    @Test
    void chainageFiltreTrieEtTransforme() {
        assertEquals(List.of("Les grandes bibliothèques", "une histoire du code"),
                service.titresTriesDeCategorie(livres, Categorie.HISTOIRE));
        assertEquals(List.of("Le Guide du Java", "une histoire du code", "Algorithmes",
                "Les grandes bibliothèques", "Le Guide du Java"), service.obtenirSeulementLesTitres(livres));
    }

    @Test
    void lesTraitementsAcceptentUneListeVide() {
        List<Livre> vide = List.of();

        assertTrue(service.filtrerParCategorieStream(vide, Categorie.ROMAN).isEmpty());
        assertTrue(service.indexerLivresParTitreStream(vide).isEmpty());
        assertTrue(service.trierLivresParTitreStream(vide).isEmpty());
        assertTrue(service.obtenirCategoriesUniquesStream(vide).isEmpty());
    }

    @Test
    void removeIfSupprimeLesTitresContenantLeMotCle() {
        List<Livre> copie = new ArrayList<>(livres);

        assertTrue(service.supprimerLivresParMotCle(copie, "JAVA"));
        assertEquals(List.of(2, 3, 4), copie.stream().map(Livre::getId).toList());
        assertFalse(service.supprimerLivresParMotCle(copie, "absent"));
        assertEquals(5, livres.size());
    }
}

package com.bibliotech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.dao.LivreDAO;
import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.exception.LivreDejaExistantException;
import com.bibliotech.model.Auteur;
import com.bibliotech.model.Categorie;
import com.bibliotech.model.Livre;
import com.bibliotech.support.MySqlIntegrationTest;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** Service branché sur un vrai MySQL : exceptions métier et insertions concurrentes. */
@Timeout(60)
class BibliothequeServiceIT extends MySqlIntegrationTest {

    private static final int NB_THREADS = 8;

    private final BibliothequeService service = new BibliothequeService(
            new LivreDAO(CONNEXIONS), new AuteurDAO(CONNEXIONS), 2, Duration.ZERO, message -> { });
    private final ExecutorService pool = Executors.newFixedThreadPool(NB_THREADS);
    private Auteur auteur;

    @BeforeEach
    void creerAuteur() throws Exception {
        auteur = new Auteur("Moreau", "Hugo");
        service.sauvegarderAuteur(auteur);
    }

    @AfterEach
    void arreterPool() {
        pool.shutdownNow();
    }

    @Test
    void auteurInconnuEtCleEtrangereTraduitsEnExceptionsMetier() {
        BibliothequeException inconnu = assertThrows(BibliothequeException.class,
                () -> service.chercherAuteurParId(9999));
        assertTrue(inconnu.getMessage().contains("9999"));

        BibliothequeException orphelin = assertThrows(BibliothequeException.class,
                () -> service.sauvegarderLivre(new Livre("Orphelin", new Auteur(9999, "X", "Y"), Categorie.ROMAN)));
        assertInstanceOf(SQLException.class, orphelin.getCause());
    }

    @Test
    void insertionsConcurrentesSansVerrouUneSeuleReussitGraceAUnique() throws Exception {
        List<Throwable> echecs = lancerEnMemeTemps(livre -> service.sauvegarderLivre(livre));

        assertEquals(NB_THREADS - 1, echecs.size(), "exactement une insertion réussit");
        echecs.forEach(e -> assertInstanceOf(LivreDejaExistantException.class, e));
        assertEquals(1, service.chargerTousLesLivres().size());
    }

    @Test
    void insertionsConcurrentesAvecSynchronizedUneSeuleReussit() throws Exception {
        List<Throwable> echecs = lancerEnMemeTemps(livre -> service.sauvegarderLivreSiAbsent(livre));

        assertEquals(NB_THREADS - 1, echecs.size(), "exactement une insertion réussit");
        echecs.forEach(e -> {
            assertInstanceOf(LivreDejaExistantException.class, e);
            assertTrue(e.getMessage().contains("vérification Java"), "doublon détecté avant l'INSERT");
        });
        assertEquals(1, service.chargerTousLesLivres().size());
    }

    @Test
    void consultationLitLeCatalogueReel() throws Exception {
        service.sauvegarderLivre(new Livre("Le Guide du Java", auteur, Categorie.INFORMATIQUE));

        assertEquals(1, service.consulterLivresSimultane("lecteur"));
        assertEquals(1, service.recupererTotalLivresLentement("tache"));
        assertEquals(2, service.permisDisponibles());
    }

    private interface Insertion {
        void inserer(Livre livre) throws BibliothequeException;
    }

    /** Démarre NB_THREADS insertions du même titre au même signal et renvoie les échecs. */
    private List<Throwable> lancerEnMemeTemps(Insertion insertion) throws Exception {
        CountDownLatch prets = new CountDownLatch(NB_THREADS);
        CountDownLatch depart = new CountDownLatch(1);
        List<Future<Void>> resultats = new ArrayList<>();
        for (int i = 0; i < NB_THREADS; i++) {
            Callable<Void> tache = () -> {
                Livre livre = new Livre("L'Avare", auteur, Categorie.ROMAN);
                prets.countDown();
                depart.await();
                insertion.inserer(livre);
                return null;
            };
            resultats.add(pool.submit(tache));
        }
        assertTrue(prets.await(10, TimeUnit.SECONDS));
        depart.countDown();

        List<Throwable> echecs = new ArrayList<>();
        for (Future<Void> resultat : resultats) {
            try {
                resultat.get(30, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                echecs.add(e.getCause());
            }
        }
        return echecs;
    }
}

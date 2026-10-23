package com.bibliotech.service;

import static com.bibliotech.support.DaoDeTest.attendreQue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bibliotech.exception.BibliothequeException;
import com.bibliotech.support.DaoDeTest;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Tests déterministes du sémaphore : la « lecture en base » est une doublure qui reste bloquée
 * tant que le test ne la libère pas. Aucun résultat ne dépend de durées ou de l'ordonnanceur.
 */
@Timeout(30)
class BibliothequeServiceSemaphoreTest {

    private final CountDownLatch liberation = new CountDownLatch(1);
    private final ExecutorService pool = Executors.newCachedThreadPool();

    @AfterEach
    void arreter() {
        liberation.countDown();
        pool.shutdownNow();
    }

    private BibliothequeService serviceBloquant(int permis) {
        return new BibliothequeService(
                DaoDeTest.livreDAO(() -> {
                    liberation.await();
                    return List.of();
                }),
                DaoDeTest.auteurDAOInutilise(), permis, Duration.ZERO, message -> { });
    }

    @Test
    void jamaisPlusDeConsultationsSimultaneesQueDePermis() throws Exception {
        BibliothequeService service = serviceBloquant(2);
        List<Future<Integer>> resultats = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            String nom = "u" + i;
            resultats.add(pool.submit(() -> service.consulterLivresSimultane(nom)));
        }

        // Deux threads sont dans la zone protégée, les quatre autres attendent un permis.
        attendreQue(() -> service.getConsultationsEnCours() == 2 && service.threadsEnAttente() == 4,
                "2 consultations en cours et 4 en attente");
        assertEquals(0, service.permisDisponibles());

        liberation.countDown();
        for (Future<Integer> resultat : resultats) {
            assertEquals(0, resultat.get(10, TimeUnit.SECONDS));
        }

        assertEquals(2, service.getMaxConsultationsObservees());
        assertEquals(2, service.permisDisponibles(), "tous les permis sont rendus, aucun en trop");
        assertEquals(0, service.getConsultationsEnCours());
    }

    @Test
    void uneAttenteInterrompueNeRendPasDePermisJamaisObtenu() throws Exception {
        BibliothequeService service = serviceBloquant(1);
        Future<Integer> occupant = pool.submit(() -> service.consulterLivresSimultane("occupant"));
        attendreQue(() -> service.getConsultationsEnCours() == 1, "l'occupant détient le permis");

        AtomicBoolean interrompu = new AtomicBoolean();
        Thread enAttente = new Thread(() -> {
            try {
                service.consulterLivresSimultane("en-attente");
            } catch (InterruptedException e) {
                interrompu.set(true);
            } catch (BibliothequeException e) {
                throw new AssertionError(e);
            }
        });
        enAttente.start();
        attendreQue(() -> service.threadsEnAttente() == 1, "un thread bloqué sur acquire()");

        enAttente.interrupt();
        enAttente.join(10_000);

        assertTrue(interrompu.get(), "l'interruption est propagée, pas avalée");
        // Avec acquire() dans le try, le finally aurait rendu un permis : on lirait 1 ici.
        assertEquals(0, service.permisDisponibles(), "le permis de l'occupant n'a pas été dupliqué");

        liberation.countDown();
        occupant.get(10, TimeUnit.SECONDS);
        assertEquals(1, service.permisDisponibles());
        assertEquals(1, service.getMaxConsultationsObservees());
    }

    @Test
    void uneErreurSqlRendLePermisEtEstTraduite() throws Exception {
        BibliothequeService service = new BibliothequeService(
                DaoDeTest.livreDAO(() -> {
                    throw new SQLException("base indisponible");
                }),
                DaoDeTest.auteurDAOInutilise(), 2, Duration.ZERO, message -> { });

        BibliothequeException e = assertThrows(BibliothequeException.class,
                () -> service.consulterLivresSimultane("u1"));

        assertEquals("base indisponible", e.getCause().getMessage());
        assertEquals(2, service.permisDisponibles());
        assertEquals(0, service.getConsultationsEnCours());
        assertFalse(Thread.currentThread().isInterrupted());
    }
}

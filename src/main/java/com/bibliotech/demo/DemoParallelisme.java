package com.bibliotech.demo;

import com.bibliotech.service.BibliothequeService;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Parallélisme simple : deux traitements de 2 s chacun, exécutés à la suite (≈ 4 s), puis dans
 * deux threads attendus avec join() (≈ 2 s), puis via un ExecutorService (≈ 2 s).
 */
public class DemoParallelisme {

    public static void main(String[] args) throws InterruptedException {
        BibliothequeService service = DemoSupport.service();
        // Première connexion hors mesure : elle inclut le chargement du pilote JDBC et la négociation TLS.
        DemoSupport.chargerLivres(service);

        System.out.println("--- 1. Séquentiel ---");
        long debut = System.nanoTime();
        try {
            service.recupererTotalLivresLentement("Action_1");
            service.recupererTotalLivresLentement("Action_2");
        } catch (Exception e) {
            DemoSupport.arreter(e.getMessage());
        }
        System.out.println("Durée séquentielle : " + millis(debut) + " ms");

        System.out.println("\n--- 2. Deux Thread + join() ---");
        debut = System.nanoTime();
        Thread t1 = new Thread(() -> executer(service, "Thread_A"), "Thread_A");
        Thread t2 = new Thread(() -> executer(service, "Thread_B"), "Thread_B");
        t1.start();
        t2.start();
        // join() bloque le thread principal jusqu'à la fin de chaque thread : la mesure est donc fiable.
        t1.join();
        t2.join();
        System.out.println("Durée avec deux threads : " + millis(debut) + " ms");

        System.out.println("\n--- 3. ExecutorService (pool de 2 threads) ---");
        debut = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Callable<Integer>> taches = List.of(
                    () -> service.recupererTotalLivresLentement("Tache_1"),
                    () -> service.recupererTotalLivresLentement("Tache_2"));
            // invokeAll attend la fin de toutes les tâches ; get() relance une éventuelle erreur.
            for (Future<Integer> resultat : pool.invokeAll(taches)) {
                resultat.get();
            }
        } catch (ExecutionException e) {
            System.err.println("Erreur dans une tâche : " + e.getCause().getMessage());
        } finally {
            pool.shutdown();
        }
        System.out.println("Durée avec ExecutorService : " + millis(debut) + " ms");
    }

    private static void executer(BibliothequeService service, String nom) {
        try {
            service.recupererTotalLivresLentement(nom);
        } catch (InterruptedException e) {
            // On restaure l'indicateur d'interruption au lieu de l'avaler.
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[" + nom + "] " + e.getMessage());
        }
    }

    private static long millis(long debutNanos) {
        return (System.nanoTime() - debutNanos) / 1_000_000;
    }
}

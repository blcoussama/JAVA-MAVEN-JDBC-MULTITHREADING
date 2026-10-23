package com.bibliotech.demo;

import com.bibliotech.service.BibliothequeService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Sémaphore : 5 utilisateurs consultent le catalogue en même temps, mais le service n'autorise
 * que {@value BibliothequeService#CONSULTATIONS_SIMULTANEES_PAR_DEFAUT} consultations simultanées.
 * Chaque consultation dure 2 s : les utilisateurs passent par vagues de deux.
 */
public class DemoSemaphore {

    private static final int NB_UTILISATEURS = 5;

    public static void main(String[] args) throws InterruptedException {
        BibliothequeService service = DemoSupport.service();
        // Première connexion hors mesure : elle inclut le chargement du pilote JDBC et la négociation TLS.
        DemoSupport.chargerLivres(service);

        System.out.println("=== Multithreading et sémaphore ===");
        System.out.println("Scénario   : " + NB_UTILISATEURS + " utilisateurs veulent lire le catalogue.");
        System.out.println("Contrainte : " + service.getConsultationsSimultanees() + " consultations simultanées au maximum.\n");

        long debut = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(NB_UTILISATEURS);
        for (int i = 1; i <= NB_UTILISATEURS; i++) {
            String utilisateur = "utilisateur-" + i;
            pool.submit(() -> {
                try {
                    service.consulterLivresSimultane(utilisateur);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    System.err.println("[" + utilisateur + "] " + e.getMessage());
                }
            });
        }
        System.out.println("[main] " + NB_UTILISATEURS + " tâches soumises, le thread principal n'est pas bloqué.\n");

        // Le thread principal attend la fin de toutes les tâches avant de conclure.
        pool.shutdown();
        if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
            pool.shutdownNow();
        }

        System.out.println("\nDurée totale : " + (System.nanoTime() - debut) / 1_000_000 + " ms");
        System.out.println("Consultations simultanées observées au maximum : " + service.getMaxConsultationsObservees()
                + " (limite : " + service.getConsultationsSimultanees() + ")");
        System.out.println("Permis disponibles à la fin : " + service.permisDisponibles()
                + " (aucun permis perdu ni rendu en trop)");
    }
}

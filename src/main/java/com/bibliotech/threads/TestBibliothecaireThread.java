package com.bibliotech.threads;

import com.bibliotech.services.EmpruntService;
import com.bibliotech.services.BibliothequeService;

/**
 * Tests du système multithreading complet
 * Classe de test SANS JUnit (style Java Application)
 */
public class TestBibliothecaireThread {

    private static EmpruntService empruntService;
    private static BibliothequeService bibliothequeService;
    private static SynchroManager synchroManager;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     SYSTÈME DE TESTS MULTITHREADING - BIBLIOTECH              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // Initialisation
        setUp();

        // Lancer tous les tests
        int testsPasses = 0;
        int testsEchoues = 0;

        try {
            test01_UnSeulThread();
            testsPasses++;
        } catch (Exception e) {
            testsEchoues++;
            System.err.println("❌ TEST 1 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            setUp(); // Réinitialiser
            test02_TroisThreads();
            testsPasses++;
        } catch (Exception e) {
            testsEchoues++;
            System.err.println("❌ TEST 2 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            setUp(); // Réinitialiser
            test03_CinqThreads();
            testsPasses++;
        } catch (Exception e) {
            testsEchoues++;
            System.err.println("❌ TEST 3 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            setUp(); // Réinitialiser
            test04_VerificationSemaphore();
            testsPasses++;
        } catch (Exception e) {
            testsEchoues++;
            System.err.println("❌ TEST 4 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            setUp(); // Réinitialiser
            test05_StressTest();
            testsPasses++;
        } catch (Exception e) {
            testsEchoues++;
            System.err.println("❌ TEST 5 ÉCHOUÉ : " + e.getMessage());
            e.printStackTrace();
        }

        // Résumé final
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    RÉSUMÉ FINAL DES TESTS                      ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Tests passés   : %2d / 5                                       ║%n", testsPasses);
        System.out.printf("║  Tests échoués  : %2d / 5                                       ║%n", testsEchoues);
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        if (testsEchoues == 0) {
            System.out.println("\n🎉 TOUS LES TESTS SONT PASSÉS AVEC SUCCÈS ! 🎉\n");
        } else {
            System.out.println("\n⚠️  CERTAINS TESTS ONT ÉCHOUÉ - VÉRIFIER LES ERREURS ⚠️\n");
        }
    }

    private static void setUp() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║          INITIALISATION TEST MULTITHREADING           ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        empruntService = new EmpruntService();
        bibliothequeService = new BibliothequeService();
        synchroManager = new SynchroManager(5);
        
        System.out.println("✅ Services initialisés");
        System.out.println("✅ SynchroManager créé (5 permits)");
    }

    // ==================== TEST 1 : UN SEUL THREAD ====================
    
    private static void test01_UnSeulThread() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 1 : UN SEUL THREAD (BASELINE)");
        System.out.println("=".repeat(60));

        BibliothecaireThread biblio1 = new BibliothecaireThread(
                "Alice", empruntService, bibliothequeService, synchroManager, 5);

        System.out.println("\n🚀 Démarrage thread unique...");
        long debut = System.currentTimeMillis();

        biblio1.start();
        biblio1.join();

        long duree = System.currentTimeMillis() - debut;
        System.out.println("\n✅ Thread terminé en " + (duree / 1000.0) + " secondes");

        synchroManager.afficherStats();

        // Vérifications
        verifier(biblio1.getTotalOperations() > 0, 
                "Le thread devrait avoir effectué au moins 1 opération");
        verifier(synchroManager.getTotalOperations() > 0, 
                "SynchroManager devrait avoir enregistré des opérations");

        System.out.println("✅ TEST 1 RÉUSSI\n");
    }

    // ==================== TEST 2 : TROIS THREADS ====================
    
    private static void test02_TroisThreads() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 2 : TROIS THREADS EN PARALLÈLE");
        System.out.println("=".repeat(60));

        BibliothecaireThread biblio1 = new BibliothecaireThread(
                "Alice", empruntService, bibliothequeService, synchroManager, 5);
        BibliothecaireThread biblio2 = new BibliothecaireThread(
                "Bob", empruntService, bibliothequeService, synchroManager, 5);
        BibliothecaireThread biblio3 = new BibliothecaireThread(
                "Charlie", empruntService, bibliothequeService, synchroManager, 5);

        System.out.println("\n🚀 Démarrage 3 threads...");
        long debut = System.currentTimeMillis();

        biblio1.start();
        biblio2.start();
        biblio3.start();

        System.out.println("⏳ Threads en cours d'exécution...\n");

        biblio1.join();
        System.out.println("✓ Thread Alice terminé");
        biblio2.join();
        System.out.println("✓ Thread Bob terminé");
        biblio3.join();
        System.out.println("✓ Thread Charlie terminé");

        long duree = System.currentTimeMillis() - debut;
        System.out.println("\n✅ Tous les threads terminés en " + (duree / 1000.0) + " secondes");

        synchroManager.afficherStats();

        int totalOperationsThreads = biblio1.getTotalOperations() +
                biblio2.getTotalOperations() + biblio3.getTotalOperations();

        verifier(totalOperationsThreads > 0, 
                "Les threads devraient avoir effectué des opérations");
        verifier(totalOperationsThreads == synchroManager.getTotalOperations(),
                "Le total du SynchroManager doit correspondre à la somme des threads");

        System.out.println("✅ TEST 2 RÉUSSI\n");
    }

    // ==================== TEST 3 : CINQ THREADS (PRINCIPAL) ====================
    
    private static void test03_CinqThreads() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 3 : CINQ THREADS EN PARALLÈLE (SCÉNARIO PRINCIPAL)");
        System.out.println("=".repeat(60));
        System.out.println("📋 Configuration :");
        System.out.println("   - 5 threads bibliothécaires");
        System.out.println("   - 10 opérations par thread (50 total)");
        System.out.println("   - Semaphore avec 5 permits (limitation)");
        System.out.println("   - Distribution : 40% emprunt, 40% retour, 20% recherche");
        System.out.println("=".repeat(60));

        BibliothecaireThread biblio1 = new BibliothecaireThread(
                "Alice", empruntService, bibliothequeService, synchroManager, 10);
        BibliothecaireThread biblio2 = new BibliothecaireThread(
                "Bob", empruntService, bibliothequeService, synchroManager, 10);
        BibliothecaireThread biblio3 = new BibliothecaireThread(
                "Charlie", empruntService, bibliothequeService, synchroManager, 10);
        BibliothecaireThread biblio4 = new BibliothecaireThread(
                "Diana", empruntService, bibliothequeService, synchroManager, 10);
        BibliothecaireThread biblio5 = new BibliothecaireThread(
                "Ethan", empruntService, bibliothequeService, synchroManager, 10);

        System.out.println("\n🚀 Démarrage des 5 threads...");
        long debut = System.currentTimeMillis();

        biblio1.start();
        biblio2.start();
        biblio3.start();
        biblio4.start();
        biblio5.start();

        System.out.println("⏳ Threads en cours d'exécution...");
        System.out.println("   (Limitation à 5 opérations simultanées par Semaphore)\n");

        // Affichage périodique de l'état
        for (int i = 0; i < 5; i++) {
            Thread.sleep(3000);
            System.out.println("\n--- État après " + ((i + 1) * 3) + " secondes ---");
            synchroManager.afficherStatsCompactes();
        }

        System.out.println("\n⏳ Attente de la fin de tous les threads...\n");

        biblio1.join();
        System.out.println("✓ Thread Alice terminé");
        biblio2.join();
        System.out.println("✓ Thread Bob terminé");
        biblio3.join();
        System.out.println("✓ Thread Charlie terminé");
        biblio4.join();
        System.out.println("✓ Thread Diana terminé");
        biblio5.join();
        System.out.println("✓ Thread Ethan terminé");

        long duree = System.currentTimeMillis() - debut;
        System.out.println("\n✅ TOUS LES THREADS TERMINÉS EN " + (duree / 1000.0) + " SECONDES");

        synchroManager.afficherStats();

        // Résumé par bibliothécaire
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              RÉSUMÉ PAR BIBLIOTHÉCAIRE                    ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.printf("║ Alice   : %2d ops (E:%d/%d R:%d/%d Rech:%d)               ║%n",
                biblio1.getTotalOperations(),
                biblio1.getEmpruntsReussis(), biblio1.getEmpruntsEchoues(),
                biblio1.getRetoursReussis(), biblio1.getRetoursEchoues(),
                biblio1.getRecherchesEffectuees());
        System.out.printf("║ Bob     : %2d ops (E:%d/%d R:%d/%d Rech:%d)               ║%n",
                biblio2.getTotalOperations(),
                biblio2.getEmpruntsReussis(), biblio2.getEmpruntsEchoues(),
                biblio2.getRetoursReussis(), biblio2.getRetoursEchoues(),
                biblio2.getRecherchesEffectuees());
        System.out.printf("║ Charlie : %2d ops (E:%d/%d R:%d/%d Rech:%d)               ║%n",
                biblio3.getTotalOperations(),
                biblio3.getEmpruntsReussis(), biblio3.getEmpruntsEchoues(),
                biblio3.getRetoursReussis(), biblio3.getRetoursEchoues(),
                biblio3.getRecherchesEffectuees());
        System.out.printf("║ Diana   : %2d ops (E:%d/%d R:%d/%d Rech:%d)               ║%n",
                biblio4.getTotalOperations(),
                biblio4.getEmpruntsReussis(), biblio4.getEmpruntsEchoues(),
                biblio4.getRetoursReussis(), biblio4.getRetoursEchoues(),
                biblio4.getRecherchesEffectuees());
        System.out.printf("║ Ethan   : %2d ops (E:%d/%d R:%d/%d Rech:%d)               ║%n",
                biblio5.getTotalOperations(),
                biblio5.getEmpruntsReussis(), biblio5.getEmpruntsEchoues(),
                biblio5.getRetoursReussis(), biblio5.getRetoursEchoues(),
                biblio5.getRecherchesEffectuees());
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        // Vérifications
        int totalOps = biblio1.getTotalOperations() + biblio2.getTotalOperations() +
                       biblio3.getTotalOperations() + biblio4.getTotalOperations() +
                       biblio5.getTotalOperations();

        verifier(totalOps >= 40, 
                "Au moins 40 opérations devraient avoir été effectuées (obtenu: " + totalOps + ")");
        verifier(synchroManager.getPermitsDisponibles() == 5, 
                "Tous les permits doivent être libérés (disponibles: " + 
                synchroManager.getPermitsDisponibles() + ")");
        verifier(synchroManager.getTotalEmprunts() > 0, 
                "Au moins un emprunt devrait avoir été effectué");
        verifier(synchroManager.getTotalRetours() > 0, 
                "Au moins un retour devrait avoir été effectué");
        verifier(synchroManager.getTotalRecherches() > 0, 
                "Au moins une recherche devrait avoir été effectuée");

        System.out.println("✅ TEST 3 RÉUSSI\n");
    }

    // ==================== TEST 4 : VÉRIFICATION SEMAPHORE ====================
    
    private static void test04_VerificationSemaphore() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 4 : VÉRIFICATION SEMAPHORE (SATURATION)");
        System.out.println("=".repeat(60));
        System.out.println("📋 Configuration :");
        System.out.println("   - 10 threads (pour saturer le Semaphore)");
        System.out.println("   - 3 opérations par thread (30 total)");
        System.out.println("   - Vérification : permits toujours entre 0 et 5");
        System.out.println("=".repeat(60));

        BibliothecaireThread[] threads = new BibliothecaireThread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new BibliothecaireThread(
                    "Biblio-" + (i + 1), empruntService, bibliothequeService, synchroManager, 3);
        }

        System.out.println("\n🚀 Démarrage de 10 threads...");
        long debut = System.currentTimeMillis();

        for (BibliothecaireThread thread : threads) {
            thread.start();
        }

        System.out.println("⏳ Monitoring du Semaphore...\n");

        // Monitoring pendant l'exécution
        boolean tousFinis = false;
        int compteur = 0;
        while (!tousFinis && compteur < 20) {
            Thread.sleep(1000);
            int permits = synchroManager.getPermitsDisponibles();
            System.out.printf("[%2ds] Permits disponibles: %d/5 | Ops effectuées: %d%n",
                    ++compteur, permits, synchroManager.getTotalOperations());

            // Vérifier que permits est toujours valide
            verifier(permits >= 0 && permits <= 5,
                    "Permits invalides: " + permits + " (doit être 0-5)");

            // Vérifier si tous finis
            tousFinis = true;
            for (BibliothecaireThread thread : threads) {
                if (thread.isAlive()) {
                    tousFinis = false;
                    break;
                }
            }
        }

        // Attendre tous les threads
        for (BibliothecaireThread thread : threads) {
            thread.join();
        }

        long duree = System.currentTimeMillis() - debut;
        System.out.println("\n✅ Tous les threads terminés en " + (duree / 1000.0) + " secondes");

        synchroManager.afficherStats();

        // Vérification finale
        verifier(synchroManager.getPermitsDisponibles() == 5,
                "Tous les permits doivent être libérés à la fin (disponibles: " +
                synchroManager.getPermitsDisponibles() + ")");

        System.out.println("✅ TEST 4 RÉUSSI\n");
    }

    // ==================== TEST 5 : STRESS TEST ====================
    
    private static void test05_StressTest() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 5 : STRESS TEST (100 OPÉRATIONS)");
        System.out.println("=".repeat(60));
        System.out.println("📋 Configuration :");
        System.out.println("   - 5 threads");
        System.out.println("   - 20 opérations par thread (100 total)");
        System.out.println("   - Durée estimée : 1-2 minutes");
        System.out.println("=".repeat(60));

        BibliothecaireThread biblio1 = new BibliothecaireThread(
                "Alice", empruntService, bibliothequeService, synchroManager, 20);
        BibliothecaireThread biblio2 = new BibliothecaireThread(
                "Bob", empruntService, bibliothequeService, synchroManager, 20);
        BibliothecaireThread biblio3 = new BibliothecaireThread(
                "Charlie", empruntService, bibliothequeService, synchroManager, 20);
        BibliothecaireThread biblio4 = new BibliothecaireThread(
                "Diana", empruntService, bibliothequeService, synchroManager, 20);
        BibliothecaireThread biblio5 = new BibliothecaireThread(
                "Ethan", empruntService, bibliothequeService, synchroManager, 20);

        System.out.println("\n🚀 Démarrage stress test...");
        long debut = System.currentTimeMillis();

        biblio1.start();
        biblio2.start();
        biblio3.start();
        biblio4.start();
        biblio5.start();

        System.out.println("⏳ Stress test en cours...\n");

        // Affichage périodique toutes les 10 secondes
        for (int i = 0; i < 12; i++) {
            Thread.sleep(10000);
            System.out.println("\n--- Progression après " + ((i + 1) * 10) + " secondes ---");
            synchroManager.afficherStatsCompactes();

            // Vérifier si tous finis
            if (!biblio1.isAlive() && !biblio2.isAlive() && !biblio3.isAlive() &&
                !biblio4.isAlive() && !biblio5.isAlive()) {
                break;
            }
        }

        System.out.println("\n⏳ Attente de la fin de tous les threads...\n");

        biblio1.join();
        biblio2.join();
        biblio3.join();
        biblio4.join();
        biblio5.join();

        long duree = System.currentTimeMillis() - debut;
        System.out.println("\n✅ STRESS TEST TERMINÉ EN " + (duree / 1000.0) + " SECONDES");

        synchroManager.afficherStats();

        // Vérifications
        int totalOps = biblio1.getTotalOperations() + biblio2.getTotalOperations() +
                       biblio3.getTotalOperations() + biblio4.getTotalOperations() +
                       biblio5.getTotalOperations();

        verifier(totalOps >= 80,
                "Au moins 80 opérations devraient avoir été effectuées (obtenu: " + totalOps + ")");
        verifier(synchroManager.getPermitsDisponibles() == 5,
                "Tous les permits doivent être libérés");

        System.out.println("✅ TEST 5 RÉUSSI\n");
    }

    // ==================== MÉTHODE UTILITAIRE ====================
    
    private static void verifier(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Assertion échouée: " + message);
        }
    }
}